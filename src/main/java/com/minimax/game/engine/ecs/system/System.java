package com.minimax.game.engine.ecs.system;

import com.minimax.game.engine.ecs.ECSWorld;
import com.minimax.game.engine.ecs.component.Component;
import lombok.EqualsAndHashCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Base interface for all systems in the ECS architecture.
 * 
 * Systems contain pure logic that operates on entities with specific
 * component combinations. They have no state of their own and operate
 * entirely on the data in components.
 * 
 * Key Characteristics:
 * - Pure logic (no persistent state)
 * - Operates on specific component types
 * - Can be executed in parallel with other systems
 * - No direct entity manipulation
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
@Data
@EqualsAndHashCode
public abstract class System {
    
    /** Name of this system for debugging */
    private String name;
    
    /** Execution phase this system belongs to */
    private SystemPhase executionPhase;
    
    /** System execution priority (lower numbers execute first) */
    private int priority = 0;
    
    /** Flag indicating if system is enabled */
    private boolean enabled = true;
    
    /** Flag indicating if system is debug enabled */
    private boolean debugEnabled = false;

    /**
     * Creates a new system with auto-generated name.
     */
    protected System() {
        this.name = getClass().getSimpleName();
    }

    /**
     * Creates a new system with specified name.
     * 
     * @param name the name for this system
     */
    protected System(String name) {
        this.name = name;
    }

    /**
     * Initializes this system with the ECS world.
     * 
     * This method is called once when the system is registered with the world.
     * It can be used to set up any system-specific data or validate configuration.
     * 
     * @param world the ECS world
     * @throws Exception if initialization fails
     */
    public void initialize(ECSWorld world) throws Exception {
        log.debug("Initializing system '{}'", name);
        // Default implementation does nothing
    }

    /**
     * Updates this system for the current frame.
     * 
     * This method is called every frame with the current delta time.
     * Systems should process entities with the required component types
     * and update their state as needed.
     * 
     * @param world the ECS world
     * @param deltaTime time elapsed since last frame in seconds
     * @throws Exception if system execution fails
     */
    public abstract void update(ECSWorld world, float deltaTime) throws Exception;

    /**
     * Shuts down this system and releases any resources.
     * 
     * This method is called when the ECS world is being shut down.
     * Systems should clean up any resources they allocated during initialization.
     * 
     * @throws Exception if shutdown fails
     */
    public void shutdown() throws Exception {
        log.debug("Shutting down system '{}'", name);
        // Default implementation does nothing
    }

    /**
     * Enables or disables this system.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            log.debug("System '{}' {}", name, enabled ? "enabled" : "disabled");
        }
    }

    /**
     * Enables or disables debug output for this system.
     * 
     * @param debugEnabled true to enable debug output
     */
    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    /**
     * Checks if this system is enabled for execution.
     * 
     * @return true if the system is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Checks if this system has debug output enabled.
     * 
     * @return true if debug output is enabled
     */
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * Sets the execution priority for this system.
     * 
     * Systems with lower priority numbers execute first within their phase.
     * 
     * @param priority the execution priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets the execution priority.
     * 
     * @return the execution priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Logs a debug message if debug output is enabled.
     * 
     * @param message the message to log
     */
    protected void debugLog(String message) {
        if (debugEnabled) {
            log.debug("[{}] {}", name, message);
        }
    }

    /**
     * Logs an info message.
     * 
     * @param message the message to log
     */
    protected void infoLog(String message) {
        log.info("[{}] {}", name, message);
    }

    /**
     * Logs a warning message.
     * 
     * @param message the message to log
     */
    protected void warnLog(String message) {
        log.warn("[{}] {}", name, message);
    }

    /**
     * Logs an error message.
     * 
     * @param message the message to log
     */
    protected void errorLog(String message) {
        log.error("[{}] {}", name, message);
    }

    /**
     * Gets a string representation of this system.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("System{name='%s', phase=%s, priority=%d, enabled=%s}", 
                           name, executionPhase, priority, enabled);
    }
}
