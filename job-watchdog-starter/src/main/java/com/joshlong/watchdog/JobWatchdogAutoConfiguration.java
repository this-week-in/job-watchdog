package com.joshlong.watchdog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides a watchdog that periodically emits a heartbeat event that tells you if a service is still around.
 */
@Configuration
@EnableConfigurationProperties(WatchdogProperties.class)
public class JobWatchdogAutoConfiguration {

    @Bean
    Watchdog watchdog(WatchdogProperties properties, GenericApplicationContext applicationContext) {
        return new Watchdog(this.scheduledExecutorService(), applicationContext, properties.inactivityHeartbeatInSeconds(), properties.inactivityThresholdInSeconds());
    }

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }
}


@ConfigurationProperties(prefix = "watchdog")
record WatchdogProperties(long inactivityThresholdInSeconds,
                          long inactivityHeartbeatInSeconds) {
}

class Watchdog implements InitializingBean {

    private final Executor executor;
    private final GenericApplicationContext applicationContext;
    private final long inactivityHeartbeatInSeconds, inactivityThresholdInSeconds;
    private final long window;
    private final AtomicLong lastTick;

    Watchdog(Executor executor, GenericApplicationContext applicationContext,
             long inactivityHeartbeatInSeconds,
             long inactivityThresholdInSeconds) {
        this.executor = executor;
        this.applicationContext = applicationContext;
        this.inactivityHeartbeatInSeconds = inactivityHeartbeatInSeconds;
        this.inactivityThresholdInSeconds = inactivityThresholdInSeconds;
        this.window = Duration.ofSeconds(this.inactivityThresholdInSeconds).toMillis();
        this.lastTick = new AtomicLong(System.currentTimeMillis());
    }


    @EventListener(HeartbeatEvent.class)
    public void onHeartBeat(HeartbeatEvent hbe) {
        this.watch();
    }

    private void watch() {
        this.logMemory();
        this.lastTick.set(System.currentTimeMillis());
    }

    private void stop() {
        log.debug("stop");
        this.logMemory();
        this.applicationContext.close();
    }

    private final Log log = LogFactory.getLog(getClass());

    private void logMemory() {
        if (this.log.isDebugEnabled()) {
            var msg = Map.of(
                    "free memory", Runtime.getRuntime().freeMemory(),
                    "max memory", Runtime.getRuntime().maxMemory(),
                    "total memory", Runtime.getRuntime().totalMemory()
            );
            var messageBuilder = new StringBuilder();
            for (var k : msg.entrySet())
                messageBuilder.append(k).append('=').append(msg.get(k)).append(System.lineSeparator());

            this.log.debug(messageBuilder.toString());
            this.log.debug(
                    Instant.now().atZone(ZoneId.systemDefault())
            );
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.executor.execute(() -> {
            log.debug("starting " + getClass().getName() + " thread.");
            this.logMemory();
            while (true) {
                try {
                    Thread.sleep(inactivityHeartbeatInSeconds * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                var now = System.currentTimeMillis();
                var then = this.lastTick.get();
                var diff = now - then;
                if (diff > window) {
                    stop();
                    break;
                }
            }
            this.log.debug("Finishing " + getClass().getName() + " thread.");

        });
    }
}
