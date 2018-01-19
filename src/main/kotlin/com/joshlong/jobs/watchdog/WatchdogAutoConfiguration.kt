package com.joshlong.jobs.watchdog

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@EnableConfigurationProperties(WatchdogProperties::class)
class WatchdogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(value = [Executor::class, TaskExecutor::class,
		TaskScheduler::class, ExecutorService::class])
	fun taskScheduler(): Executor = ConcurrentTaskScheduler(
			Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()))

	@Bean
	@ConditionalOnMissingBean
	fun watchdog(watchdogProperties: WatchdogProperties,
	             executor: Executor,
	             genericApplicationContext: GenericApplicationContext): Watchdog {
		return Watchdog(watchdogProperties, executor, genericApplicationContext)
	}
}