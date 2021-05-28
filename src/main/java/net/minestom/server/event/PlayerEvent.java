package net.minestom.server.event;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerEvent extends EntityEvent {

    protected final Player player;

    public PlayerEvent(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    /**
     * Gets the player.
     *
     * @return the player
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }
}
