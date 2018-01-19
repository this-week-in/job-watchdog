package com.joshlong.jobs.watchdog

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import java.util.concurrent.Executor

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@EnableConfigurationProperties(WatchdogProperties::class)
class WatchdogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	fun watchdog(watchdogProperties: WatchdogProperties,
	             executor: Executor,
	             genericApplicationContext: GenericApplicationContext) =
			Watchdog(watchdogProperties, executor, genericApplicationContext)
}