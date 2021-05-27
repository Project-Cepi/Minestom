package net.minestom.server.event;

import net.minestom.server.event.handler.EventHandler;
import org.jetbrains.annotations.NotNull;

public class EventGroup implements ListenerAttach {

    private final EventListener<?>[] listeners;

    protected EventGroup(@NotNull EventListener<?>... listeners) {
        this.listeners = listeners;
    }

    @Override
    public void attachTo(@NotNull EventHandler... handlers) {
        for (EventListener<?> listener : listeners) {
            listener.attachTo(handlers);
        }
    }

    @Override
    public void detachFrom(@NotNull EventHandler... handlers) {
        for (EventListener<?> listener : listeners) {
            listener.detachFrom(handlers);
        }
    }
}
