package com.joshlong.jobs.watchdog;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "watchdog")
public class WatchdogProperties {

	private long inactivityThresholdInSeconds = 5 * 60;

	public long getInactivityThresholdInSeconds() {
		return inactivityThresholdInSeconds;
	}

	public void setInactivityThresholdInSeconds(long inactivityThresholdInSeconds) {
		this.inactivityThresholdInSeconds = inactivityThresholdInSeconds;
	}

	public long getInactivityHeartbeatInSeconds() {
		return inactivityHeartbeatInSeconds;
	}

	public void setInactivityHeartbeatInSeconds(long inactivityHeartbeatInSeconds) {
		this.inactivityHeartbeatInSeconds = inactivityHeartbeatInSeconds;
	}

	private long inactivityHeartbeatInSeconds = 1;
}
