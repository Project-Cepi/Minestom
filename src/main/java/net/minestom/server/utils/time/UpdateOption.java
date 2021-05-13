package net.minestom.server.utils.time;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record UpdateOption(long value, @NotNull TimeUnit timeUnit) {

}