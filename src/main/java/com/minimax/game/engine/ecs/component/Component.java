package com.minimax.game.engine.ecs.component;

import lombok.EqualsAndHashCode;

/**
 * Base interface for all components in the ECS system.
 * 
 * Components are pure data holders - they contain no logic, only state.
 * This allows for data-oriented design where systems operate on component
 * data efficiently without object-oriented overhead.
 * 
 * Components should be immutable or have their lifecycle managed carefully
 * to ensure thread safety and predictable behavior.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@EqualsAndHashCode
public sealed interface Component 
        permits TransformComponent, VelocityComponent, HealthComponent, 
                SpriteComponent, AudioComponent, InputComponent,
                AIComponent, PhysicsComponent {
    
    /**
     * Gets a unique identifier for this component type.
     * 
     * @return the component type identifier
     */
    default String getComponentType() {
        return getClass().getSimpleName();
    }
    
    /**
     * Gets the size of this component in bytes (for memory profiling).
     * 
     * @return the approximate size in bytes
     */
    default int getSizeInBytes() {
        return estimateSize();
    }
    
    /**
     * Estimates the size of this component in memory.
     * 
     * @return the estimated size in bytes
     */
    private int estimateSize() {
        // Rough estimation based on object overhead and field types
        // This can be overridden for more accurate measurements
        return 32; // Base object overhead
    }
}
