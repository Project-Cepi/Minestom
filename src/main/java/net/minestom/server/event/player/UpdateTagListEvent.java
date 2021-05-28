package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.PlayerEvent;
import net.minestom.server.network.packet.server.play.TagsPacket;
import org.jetbrains.annotations.NotNull;

public class UpdateTagListEvent extends PlayerEvent {

    private final TagsPacket packet;

    public UpdateTagListEvent(@NotNull Player player, @NotNull TagsPacket packet) {
        super(player);
        this.packet = packet;
    }

    @NotNull
    public TagsPacket getTags() {
        return packet;
    }
}
