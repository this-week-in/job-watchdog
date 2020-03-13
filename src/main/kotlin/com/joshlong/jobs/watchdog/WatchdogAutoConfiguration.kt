package com.joshlong.jobs.watchdog

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@EnableConfigurationProperties(WatchdogProperties::class)
class WatchdogAutoConfiguration {

	@Bean
	fun executor(): ScheduledExecutorService =
			Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())

	@Bean
	@ConditionalOnMissingBean
	fun watchdog(properties: WatchdogProperties, applicationContext: GenericApplicationContext) =
			Watchdog(this.executor(),
					properties.inactivityThresholdInSeconds,
					properties.inactivityHeartbeatInSeconds,
					applicationContext)
}