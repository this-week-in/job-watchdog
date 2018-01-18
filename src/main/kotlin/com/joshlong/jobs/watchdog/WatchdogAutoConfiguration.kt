package com.joshlong.jobs.watchdog

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.task.TaskExecutor

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@EnableConfigurationProperties(WatchdogProperties::class)
class WatchdogAutoConfiguration {


	@Bean
	@ConditionalOnMissingBean
	fun watchdog(watchdogProperties: WatchdogProperties,
	             taskExecutor: TaskExecutor,
	             genericApplicationContext: GenericApplicationContext) = Watchdog(watchdogProperties, taskExecutor, genericApplicationContext)

}