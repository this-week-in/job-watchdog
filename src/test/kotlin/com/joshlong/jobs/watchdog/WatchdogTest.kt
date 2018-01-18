package com.joshlong.jobs.watchdog

import org.assertj.core.api.Assertions
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.core.task.SimpleAsyncTaskExecutor

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
class WatchdogTest {

	private val window = 5
	private val executor = SimpleAsyncTaskExecutor()
	private val applicationContext = Mockito.mock(org.springframework.context.support.GenericApplicationContext::class.java)
	private val watchdog = Watchdog(WatchdogProperties(inactivityThresholdInSeconds = window - 1L),
			executor, applicationContext)

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
}