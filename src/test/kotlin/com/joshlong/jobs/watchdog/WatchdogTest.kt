package com.joshlong.jobs.watchdog

import org.assertj.core.api.Assertions
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
class WatchdogTest {

	private val window = 5
	private val executor = SimpleAsyncTaskExecutor()
	private val applicationContext = Mockito.mock(GenericApplicationContext::class.java)
	private val watchdogProperties = WatchdogProperties().apply {
		inactivityThresholdInSeconds = window - 1L
	}
	private val watchdog = Watchdog(watchdogProperties, executor, applicationContext)

	@Test
	fun watchAndStopAfterInactivity() {
		val start = System.currentTimeMillis()
		this.watchdog.afterPropertiesSet()
		val renewals = 3
		(0 until renewals).forEach {
			Thread.sleep(1000)
			this.watchdog.watch()
		}
		Thread.sleep(window * 1000L)
		Mockito.verify(this.applicationContext, times(1)).close()
		Assertions.assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo((window + renewals) * 1000L)
	}

	@EnableAutoConfiguration
	@Configuration
	class SampleApp1 {

		@Bean("testTaskExecutor")
		fun executor(): Executor = Executors.newSingleThreadExecutor()
	}

	@EnableAutoConfiguration
	@Configuration
	class SampleApp2

	@EnableAutoConfiguration
	@Configuration
	class SampleApp3 {

		@Bean
		fun te1() = Executors.newSingleThreadExecutor()

		@Bean
		@Primary
		fun te2() = Executors.newCachedThreadPool()
	}

	@Test
	fun configWithContextProvidedExecutor() {
		val ac = SpringApplication.run(SampleApp1::class.java)
		val executor = ac.getBean("testTaskExecutor", Executor::class.java)
		Assertions.assertThat(executor)
		val wd: Watchdog = ac.getBean(Watchdog::class.java)
		Assertions.assertThat(wd.executor == executor)
		ac.close()
	}

	@Test
	fun configWithDefaultExecutor() {
		val ac = SpringApplication.run(SampleApp2::class.java)
		val wdteBeanName = "watchdogTaskExecutor"
		Assertions.assertThat(ac.containsBean(wdteBeanName)).isTrue()
		val executor = ac.getBean(wdteBeanName, Executor::class.java)
		Assertions.assertThat(executor)
		val wd: Watchdog = ac.getBean(Watchdog::class.java)
		Assertions.assertThat(wd.executor == executor)
		ac.close()
	}

	@Test
	fun configWithMultipleExecutors() {
		val ac = SpringApplication.run(SampleApp3::class.java)
		val executors: Map<String, Executor> = ac.getBeansOfType(Executor::class.java)
		val wd: Watchdog = ac.getBean(Watchdog::class.java)
		Assertions.assertThat(executors.values.contains(wd.executor)).isTrue()
		ac.close()
	}
}