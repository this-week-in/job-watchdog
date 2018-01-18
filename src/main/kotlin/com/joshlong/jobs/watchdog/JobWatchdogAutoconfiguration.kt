package com.joshlong.jobs.watchdog

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("watchdog")
class WatchdogProperties(
		/**
		 * How long should the process run without any interaction before the termination function for the watchdog is invoked?
		 */
		val inactivityThresholdInSeconds: Long = 5 * 60,

		/**
		 * How often should we check to see if there have been any heartbeat renewal events?
		 */
		val inactivityHeartbeatInSeconds: Long = 1)