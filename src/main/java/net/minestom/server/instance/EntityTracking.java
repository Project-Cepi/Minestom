package net.minestom.server.instance;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Set;

import static net.minestom.server.utils.chunk.ChunkUtils.forChunksInRange;

/**
 * Defines how {@link Entity entities} are tracked within an {@link Instance instance}.
 * <p>
 * Implementations are expected to be thread-safe, {@link #synchronize(EntityTracking)} can be used to synchronize every call.
 */
@ApiStatus.Experimental
public interface EntityTracking {

    static @NotNull EntityTracking synchronize(@NotNull EntityTracking entityTracking) {
        return entityTracking instanceof EntityTrackingImpl.Synchronized ?
                entityTracking : new EntityTrackingImpl.Synchronized(entityTracking);
    }

    /**
     * Register an entity to be tracked.
     */
    void register(@NotNull Entity entity, @NotNull Point point, @Nullable Update< Entity> update);

    /**
     * Unregister an entity tracking.
     */
    void unregister(@NotNull Entity entity, @NotNull Point point, @Nullable Update< Entity> update);

    /**
     * Called every time an entity move, you may want to verify if the new
     * position is in a different chunk.
     */
    void move(@NotNull Entity entity, @NotNull Point oldPoint, @NotNull Point newPoint, @Nullable Update<Entity> update);

    /**
     * Gets the entities newly visible and invisible from one position to another.
     */
    <T extends Entity> void difference(@NotNull Point from, @NotNull Point to, @NotNull Target<T> target, @NotNull Update<T> update);

    /**
     * Gets the entities present in the specified chunk.
     */
    <T extends Entity> void chunkEntities(int chunkX, int chunkZ, @NotNull Target<T> target, @NotNull Query<T> query);

    default <T extends Entity> void chunkEntities(@NotNull Point point, @NotNull Target<T> target, @NotNull Query<T> query) {
        chunkEntities(point.chunkX(), point.chunkZ(), target, query);
    }

    /**
     * Gets the entities present in range of the specified chunk.
     * <p>
     * This is used for auto-viewable features.
     */
    default <T extends Entity> void visibleEntities(@NotNull Point point, @NotNull Target<T> target, @NotNull Query<T> query) {
        forChunksInRange(point, MinecraftServer.getEntityViewDistance(),
                (chunkX, chunkZ) -> chunkEntities(chunkX, chunkZ, target, query));
    }

    /**
     * Gets the entities within a range.
     */
    default <T extends Entity> void nearbyEntities(@NotNull Point point, double range, @NotNull Target<T> target, @NotNull Query<T> query) {
        final int chunkRange = Math.abs((int) (range / Chunk.CHUNK_SECTION_SIZE)) + 1;
        final double squaredRange = range * range;
        ChunkUtils.forChunksInRange(point, chunkRange, (chunkX, chunkZ) ->
                chunkEntities(chunkX, chunkZ, target, entity -> {
                    if (point.distanceSquared(entity.getPosition()) < squaredRange) {
                        query.consume(entity);
                    }
                }));
    }

    /**
     * Gets all the entities tracked by this class.
     */
    @UnmodifiableView
    @NotNull Set<@NotNull Entity> entities();

    /**
     * Gets all the players tracked by this class.
     */
    @UnmodifiableView
    @NotNull Set<@NotNull Player> players();

    @ApiStatus.NonExtendable
    interface Target<E extends Entity> {
        Target<Entity> ENTITIES = EntityTrackingImpl.create(Entity.class);
        Target<Player> PLAYERS = EntityTrackingImpl.create(Player.class);

        static List<Target<?>> values() {
            return List.of(ENTITIES, PLAYERS);
        }

        static int count() {
            return EntityTrackingImpl.TARGET_COUNTER.get();
        }

        Class<E> type();

        int ordinal();
    }

    /**
     * Callback to know the newly visible entities and those to remove.
     */
    interface Update<E extends Entity> {
        void add(E entity);

        void remove(E entity);
    }

    /**
     * Query entities.
     * <p>
     * This is not a functional interface, we reserve the right to add other methods.
     */
    interface Query<E extends Entity> {
        void consume(E entity);
    }
}
