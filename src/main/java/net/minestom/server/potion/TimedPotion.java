package net.minestom.server.potion;

import org.jetbrains.annotations.NotNull;

public record TimedPotion(@NotNull Potion potion, long startingTime) {

}
