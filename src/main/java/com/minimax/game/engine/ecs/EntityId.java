package com.minimax.game.engine.ecs;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unique identifier for entities in the ECS system.
 * 
 * This record class provides type-safe entity identification with
 * efficient equals/hashCode implementation and natural ordering.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Value
public final class EntityId implements Comparable<EntityId> {
    /** Unique numeric identifier for this entity */
    long id;

    @Override
    public int compareTo(EntityId other) {
        return Long.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Entity(" + id + ")";
    }
}
