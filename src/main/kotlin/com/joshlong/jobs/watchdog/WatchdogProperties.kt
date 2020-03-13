package com.joshlong.jobs.watchdog

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "watchdog")
class WatchdogProperties(
	var inactivityThresholdInSeconds: Long = 5 * 60,
	var inactivityHeartbeatInSeconds: Long = 1
)