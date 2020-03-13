package com.joshlong.jobs.watchdog

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "watchdog")
class WatchdogProperties(
	var inactivityThresholdInSeconds: Long = 5 * 60,
	var inactivityHeartbeatInSeconds: Long = 1
)