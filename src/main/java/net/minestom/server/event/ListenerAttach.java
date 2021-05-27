package net.minestom.server.event;

import net.minestom.server.event.handler.EventHandler;
import org.jetbrains.annotations.NotNull;

public interface ListenerAttach<T extends Event> {
    void attachTo(@NotNull EventHandler<T> handler);

    void detachFrom(@NotNull EventHandler<T> handler);
}
