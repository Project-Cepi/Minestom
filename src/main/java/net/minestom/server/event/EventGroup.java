package net.minestom.server.event;

import net.minestom.server.event.handler.EventHandler;
import org.jetbrains.annotations.NotNull;

public class EventGroup<T extends Event> implements ListenerAttach<T> {

    private final EventListener<T>[] listeners;

    @SafeVarargs
    protected EventGroup(@NotNull EventListener<T>... listeners) {
        this.listeners = listeners;
    }

    @Override
    public void attachTo(@NotNull EventHandler<T> handler) {
        for (EventListener<T> listener : listeners) {
            listener.attachTo(handler);
        }
    }

    @Override
    public void detachFrom(@NotNull EventHandler<T> handler) {
        for (EventListener<T> listener : listeners) {
            listener.detachFrom(handler);
        }
    }
}
