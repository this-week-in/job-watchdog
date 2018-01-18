package com.joshlong.jobs.watchdog

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "watchdog")
open class WatchdogProperties(

		/**
		 * How long should the process run without any interaction before
		 * the termination function for the watchdog is invoked?
		 */
		var inactivityThresholdInSeconds: Long = 5 * 60,

		/**
		 * How often should we check to see if there have been any
		 * heartbeat renewal events?
		 */
		var inactivityHeartbeatInSeconds: Long = 1
)