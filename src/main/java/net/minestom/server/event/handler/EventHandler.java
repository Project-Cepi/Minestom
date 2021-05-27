package net.minestom.server.event.handler;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventCallback;
import net.minestom.server.extensions.IExtensionObserver;
import net.minestom.server.extras.selfmodification.MinestomRootClassLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

/**
 * Represents an element which can have {@link Event} listeners assigned to it.
 */
public interface EventHandler<T extends Event> extends IExtensionObserver {

    /**
     * Gets a {@link Map} containing all the listeners assigned to a specific {@link Event} type.
     *
     * @return a {@link Map} with all the listeners
     */
    @NotNull
    Map<Class<T>, Collection<EventCallback<T>>> getEventCallbacksMap();

    /**
     * Gets a {@link Collection} containing all the listeners assigned to a specific extension (represented by its name).
     * Used to unload all callbacks when the extension is unloaded
     *
     * @return a {@link Collection} with all the listeners
     */
    @NotNull
    Collection<EventCallback<?>> getExtensionCallbacks(@NotNull String extension);

    /**
     * Adds a new event callback for the specified type {@code eventClass}.
     *
     * @param eventClass    the event class
     * @param eventCallback the event callback
     * @param <E>           the event type
     * @return true if the callback collection changed as a result of the call
     */
    @ApiStatus.Internal
    default <E extends T> boolean addEventCallback(@NotNull Class<E> eventClass, @NotNull EventCallback<E> eventCallback) {
        String extensionSource = MinestomRootClassLoader.findExtensionObjectOwner(eventCallback);
        if (extensionSource != null) {
            MinecraftServer.getExtensionManager().getExtension(extensionSource).observe(this);
            getExtensionCallbacks(extensionSource).add(eventCallback);
        }

        Collection<EventCallback<E>> callbacks = getEventCallbacks(eventClass);
        return callbacks.add(eventCallback);
    }

    /**
     * Removes an event callback from the specified type {@code eventClass}.
     *
     * @param eventClass    the event class
     * @param eventCallback the event callback
     * @param <E>           the event type
     * @return true if the callback was removed as a result of this call
     */
    @ApiStatus.Internal
    default <E extends T> boolean removeEventCallback(@NotNull Class<E> eventClass, @NotNull EventCallback<E> eventCallback) {
        Collection<EventCallback<E>> callbacks = getEventCallbacks(eventClass);
        String extensionSource = MinestomRootClassLoader.findExtensionObjectOwner(eventCallback);
        if(extensionSource != null) {
            getExtensionCallbacks(extensionSource).remove(eventCallback);
        }

        return callbacks.remove(eventCallback);
    }

    /**
     * Gets the event callbacks of a specific event type.
     *
     * @param eventClass the event class
     * @param <E>        the event type
     * @return all event callbacks for the specified type {@code eventClass}
     */
    @NotNull
    default <E extends T> Collection<EventCallback<E>> getEventCallbacks(@NotNull Class<E> eventClass) {
        return getEventCallbacksMap().computeIfAbsent((Class) eventClass, clazz -> new CopyOnWriteArraySet<>());
    }

    /**
     * Gets a {@link Stream} containing all the {@link EventCallback}, no matter to which {@link Event} they are linked.
     *
     * @return a {@link Stream} containing all the callbacks
     */
    @NotNull
    default Stream<EventCallback<T>> getEventCallbacks() {
        return getEventCallbacksMap().values().stream().flatMap(Collection::stream);
    }

    /**
     * Calls the specified {@link Event} with all the assigned {@link EventCallback}.
     * <p>
     * Events are always called in the current thread.
     *
     * @param eventClass the event class
     * @param event      the event object
     * @param <E>        the event type
     */
    default <E extends T> void callEvent(@NotNull Class<E> eventClass, @NotNull E event) {

        try {
            // Local listeners
            final Collection<EventCallback<E>> eventCallbacks = getEventCallbacks(eventClass);
            runEvent(eventCallbacks, event);
        } catch (Exception exception) {
            MinecraftServer.getExceptionManager().handleException(exception);
        }
    }

    /**
     * Remove all event callbacks owned by the given extension
     * @param extension the extension to remove callbacks from
     */
    default void removeCallbacksOwnedByExtension(@NotNull String extension) {
        Collection<EventCallback<?>> extensionCallbacks = getExtensionCallbacks(extension);
        for (EventCallback<?> callback : extensionCallbacks) {

            // try to remove this callback from all callback collections
            //  we do this because we do not have information about the event class at this point
            for (Collection<EventCallback<T>> eventCallbacks : getEventCallbacksMap().values()) {
                eventCallbacks.remove(callback);
            }
        }

        extensionCallbacks.clear();
    }

    private <E extends Event> void runEvent(@NotNull Collection<EventCallback<E>> eventCallbacks, @NotNull E event) {
        for (EventCallback<E> eventCallback : eventCallbacks) {
            eventCallback.run(event);
        }
    }

    @Override
    default void onExtensionUnload(@NotNull String extensionName) {
        removeCallbacksOwnedByExtension(extensionName);
    }

}
