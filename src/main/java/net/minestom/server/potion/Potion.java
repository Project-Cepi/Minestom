package net.minestom.server.potion;

import net.minestom.server.entity.Entity;
import net.minestom.server.network.packet.server.play.EntityEffectPacket;
import net.minestom.server.network.packet.server.play.RemoveEntityEffectPacket;
import org.jetbrains.annotations.NotNull;

public record Potion(@NotNull PotionEffect effect, byte amplifier, int duration, byte flags) {

    /**
     * Creates a new potion.
     *
     * @param effect    The type of potion.
     * @param amplifier The strength of the potion.
     * @param duration  The length of the potion in ticks.
     */
    public static Potion of(@NotNull PotionEffect effect, byte amplifier, int duration) {
        return Potion.of(effect, amplifier, duration, true, true, false);
    }

    /**
     * Creates a new potion.
     *
     * @param effect    The type of potion.
     * @param amplifier The strength of the potion.
     * @param duration  The length of the potion in ticks.
     * @param particles If the potion has particles.
     */
    public static Potion of(@NotNull PotionEffect effect, byte amplifier, int duration, boolean particles) {
        return Potion.of(effect, amplifier, duration, particles, true, false);
    }

    /**
     * Creates a new potion.
     *
     * @param effect    The type of potion.
     * @param amplifier The strength of the potion.
     * @param duration  The length of the potion in ticks.
     * @param particles If the potion has particles.
     * @param icon      If the potion has an icon.
     */
    public static Potion of(@NotNull PotionEffect effect, byte amplifier, int duration, boolean particles, boolean icon) {
        return Potion.of(effect, amplifier, duration, particles, icon, false);
    }

    /**
     * Creates a new potion.
     *
     * @param effect    The type of potion.
     * @param amplifier The strength of the potion.
     * @param duration  The length of the potion in ticks.
     * @param particles If the potion has particles.
     * @param icon      If the potion has an icon.
     * @param ambient   If the potion came from a beacon.
     */
    public static Potion of(@NotNull PotionEffect effect, byte amplifier, int duration, boolean particles, boolean icon, boolean ambient) {
        byte flags = 0;
        if (ambient) {
            flags = (byte) (flags | 0x01);
        }
        if (particles) {
            flags = (byte) (flags | 0x02);
        }
        if (icon) {
            flags = (byte) (flags | 0x04);
        }

        return new Potion(effect, amplifier, duration, flags);
    }

    /**
     * Sends a packet that a potion effect has been applied to the entity.
     * <p>
     * Used internally by {@link net.minestom.server.entity.Player#addEffect(Potion)}
     *
     * @param entity the entity to add the effect to
     */
    public void sendAddPacket(@NotNull Entity entity) {
        EntityEffectPacket entityEffectPacket = new EntityEffectPacket();
        entityEffectPacket.entityId = entity.getEntityId();
        entityEffectPacket.potion = this;
        entity.sendPacketToViewersAndSelf(entityEffectPacket);
    }

    /**
     * Sends a packet that a potion effect has been removed from the entity.
     * <p>
     * Used internally by {@link net.minestom.server.entity.Player#removeEffect(PotionEffect)}
     *
     * @param entity the entity to remove the effect from
     */
    public void sendRemovePacket(@NotNull Entity entity) {
        RemoveEntityEffectPacket removeEntityEffectPacket = new RemoveEntityEffectPacket();
        removeEntityEffectPacket.entityId = entity.getEntityId();
        removeEntityEffectPacket.effect = effect;
        entity.sendPacketToViewersAndSelf(removeEntityEffectPacket);
    }
}
