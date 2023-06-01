package com.joshlong.watchdog;

import org.springframework.context.ApplicationEvent;

/**
 * Broadcast to signal that the service is still alive.
 */
public class HeartbeatEvent extends ApplicationEvent {

    public HeartbeatEvent() {
        super(Watchdog.class.getName());
    }
}
