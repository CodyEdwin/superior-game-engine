package com.minimax.game.engine.ecs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an entity in the ECS system.
 * 
 * An entity is essentially a container that groups related components together.
 * It has no logic of its own - all behavior comes from the components attached
 * to it and the systems that process those components.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Data
@Slf4j
public final class Entity {
    
    /** Unique identifier for this entity */
    private final EntityId id;
    
    /** Human-readable name for debugging and identification */
    private String name;
    
    /** Timestamp when this entity was created */
    private final Instant createdAt;
    
    /** Flag indicating if this entity is active */
    private volatile boolean active = true;
    
    /** Flag indicating if this entity should be destroyed */
    private volatile boolean markedForDestruction = false;
    
    /** Additional metadata for the entity */
    private final ConcurrentHashMap<String, Object> metadata = new ConcurrentHashMap<>();

    /**
     * Creates a new entity with the specified ID.
     * 
     * @param id the unique identifier for this entity
     */
    public Entity(EntityId id) {
        this.id = id;
        this.name = "Entity_" + id.id();
        this.createdAt = Instant.now();
    }

    /**
     * Creates a new entity with the specified ID and name.
     * 
     * @param id the unique identifier for this entity
     * @param name the human-readable name for this entity
     */
    public Entity(EntityId id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = Instant.now();
    }

    /**
     * Marks this entity as active or inactive.
     * 
     * Inactive entities are typically skipped by most systems during processing,
     * improving performance while maintaining the entity in the world.
     * 
     * @param active true to mark as active, false to mark as inactive
     */
    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            log.trace("Entity {} is now {}", id, active ? "active" : "inactive");
        }
    }

    /**
     * Marks this entity for destruction.
     * 
     * Entities marked for destruction will be removed from the world
     * at the end of the current update cycle.
     */
    public void markForDestruction() {
        if (!markedForDestruction) {
            this.markedForDestruction = true;
            log.trace("Entity {} marked for destruction", id);
        }
    }

    /**
     * Checks if this entity should be destroyed.
     * 
     * @return true if the entity is marked for destruction
     */
    public boolean isMarkedForDestruction() {
        return markedForDestruction;
    }

    /**
     * Gets the entity's age in milliseconds.
     * 
     * @return the entity's age in milliseconds
     */
    public long getAgeMilliseconds() {
        return System.currentTimeMillis() - createdAt.toEpochMilli();
    }

    /**
     * Gets the entity's age in seconds.
     * 
     * @return the entity's age in seconds
     */
    public double getAgeSeconds() {
        return getAgeMilliseconds() / 1000.0;
    }

    /**
     * Adds metadata to this entity.
     * 
     * @param key the metadata key
     * @param value the metadata value
     */
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
        log.trace("Added metadata '{}' to entity {}", key, id);
    }

    /**
     * Gets metadata from this entity.
     * 
     * @param key the metadata key
     * @return the metadata value, or null if not found
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Gets typed metadata from this entity.
     * 
     * @param key the metadata key
     * @param type the expected type
     * @param <T> the type parameter
     * @return the metadata value, or null if not found or wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        Object value = metadata.get(key);
        return (value != null && type.isInstance(value)) ? (T) value : null;
    }

    /**
     * Removes metadata from this entity.
     * 
     * @param key the metadata key to remove
     * @return the removed metadata value, or null if not found
     */
    public Object removeMetadata(String key) {
        Object removed = metadata.remove(key);
        if (removed != null) {
            log.trace("Removed metadata '{}' from entity {}", key, id);
        }
        return removed;
    }

    /**
     * Checks if this entity has metadata for the specified key.
     * 
     * @param key the metadata key
     * @return true if metadata exists for this key
     */
    public boolean hasMetadata(String key) {
        return metadata.containsKey(key);
    }

    /**
     * Gets all metadata keys for this entity.
     * 
     * @return an unmodifiable set of metadata keys
     */
    public java.util.Set<String> getMetadataKeys() {
        return Collections.unmodifiableSet(metadata.keySet());
    }

    /**
     * Gets all metadata entries for this entity.
     * 
     * @return an unmodifiable view of the metadata entries
     */
    public java.util.Map<String, Object> getAllMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    /**
     * Creates a deep copy of this entity with a new ID.
     * 
     * @param newId the ID for the copied entity
     * @return a new entity with the same properties but different ID
     */
    public Entity copy(EntityId newId) {
        Entity copy = new Entity(newId, name + "_copy");
        copy.active = this.active;
        copy.metadata.putAll(this.metadata);
        return copy;
    }

    /**
     * Creates a string representation of this entity.
     * 
     * @return a string representation including ID, name, and status
     */
    @Override
    public String toString() {
        return String.format("Entity{id=%s, name='%s', active=%s, age=%.1fs}", 
                           id, name, active, getAgeSeconds());
    }

    /**
     * Checks if this entity is equal to another object.
     * 
     * @param obj the object to compare
     * @return true if objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id.equals(entity.id);
    }

    /**
     * Gets the hash code for this entity.
     * 
     * @return the hash code based on entity ID
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
