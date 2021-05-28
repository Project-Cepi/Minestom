package net.minestom.server.event.item;

import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.EntityEvent;
import net.minestom.server.event.Event;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PickupItemEvent extends EntityEvent implements CancellableEvent {

    private final ItemEntity itemEntity;

    private boolean cancelled;

    // TODO living entity event?
    public PickupItemEvent(@NotNull LivingEntity livingEntity, @NotNull ItemEntity itemEntity) {
        super(livingEntity);
        this.itemEntity = itemEntity;
    }

    @NotNull
    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    /**
     * @deprecated use {@link #getItemEntity()} and {@link ItemEntity#getItemStack()}.
     */
    @Deprecated
    @NotNull
    public ItemStack getItemStack() {
        return getItemEntity().getItemStack();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
