/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.world;

import net.minecraft.world.chunk.WorldChunk;

import java.util.Objects;

import meteordevelopment.meteorclient.utils.misc.Pool;

/**
 * @author Crosby
 * @implNote Shouldn't be put in a {@link Pool} to avoid a race-condition, or in a {@link ThreadLocal} as it is shared between threads.
 */
public final class ChunkDataEvent {
    private final WorldChunk chunk;

    public ChunkDataEvent(WorldChunk chunk) {
        this.chunk = chunk;
    }

    public WorldChunk chunk() {
        return chunk;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ChunkDataEvent that = (ChunkDataEvent) obj;
        return Objects.equals(this.chunk, that.chunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk);
    }

    @Override
    public String toString() {
        return "ChunkDataEvent[" +
            "chunk=" + chunk + ']';
    }
}
