package com.joshlong.jobs.watchdog

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
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
	@ConditionalOnMissingBean(name = ["taskScheduler"])
	@ConditionalOnSingleCandidate(value = Executor::class)
	fun watchdogWithExecutor(watchdogProperties: WatchdogProperties,
	                         executor: Executor,
	                         genericApplicationContext: GenericApplicationContext) =
			Watchdog(watchdogProperties, executor, genericApplicationContext)

	@Bean
	@ConditionalOnBean(name = ["taskScheduler"])
	@ConditionalOnMissingBean
	fun watchdog(watchdogProperties: WatchdogProperties,
	             @Qualifier("taskScheduler") taskScheduler: Executor,
	             genericApplicationContext: GenericApplicationContext) =
			Watchdog(watchdogProperties, taskScheduler, genericApplicationContext)
}