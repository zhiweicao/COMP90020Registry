package io.friday.registry.common.entity;

public interface NotifyListener {
    void notify(RegisterMeta registerMeta, NotifyEvent event);

    enum NotifyEvent {
        SERVICE_ADDED,
        SERVICE_REMOVED
    }
}
