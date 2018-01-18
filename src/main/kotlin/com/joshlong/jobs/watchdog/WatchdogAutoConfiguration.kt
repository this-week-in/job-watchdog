package com.joshlong.jobs.watchdog

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@EnableConfigurationProperties(WatchdogProperties::class)
class WatchdogAutoConfiguration {

	@Autowired(required = false)
	private val executors: Array<Executor>? = null

	@Bean
	@ConditionalOnMissingBean(value = [Executor::class, TaskExecutor::class,
		TaskScheduler::class, ExecutorService::class])
	fun watchdogTaskExecutor(): Executor = Executors.newSingleThreadExecutor()

	@Bean
	@ConditionalOnMissingBean
	fun watchdog(watchdogProperties: WatchdogProperties,
	             genericApplicationContext: GenericApplicationContext): Watchdog {

		val executor: Executor = this.executors!!.first()
		return Watchdog(watchdogProperties, executor, genericApplicationContext)
	}


}