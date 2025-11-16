package com.minimax.game.engine.core;

/**
 * Represents the various states of the GameEngine lifecycle.
 * 
 * This enum tracks the engine's current state throughout its lifecycle,
 * from initialization through shutdown, enabling proper state management
 * and error handling.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
public enum EngineState {
    /** Engine has been created but not initialized */
    UNINITIALIZED,
    
    /** Engine is currently initializing subsystems */
    INITIALIZING,
    
    /** Engine is fully initialized and ready to start */
    INITIALIZED,
    
    /** Engine is running the main game loop */
    RUNNING,
    
    /** Engine is gracefully stopping */
    STOPPING,
    
    /** Engine has been stopped */
    STOPPED,
    
    /** Engine is shutting down and releasing resources */
    SHUTTING_DOWN,
    
    /** Engine has been fully shutdown and cleaned up */
    SHUTDOWN,
    
    /** Engine encountered an error and cannot continue */
    ERROR;

    /**
     * Checks if this state indicates the engine is running.
     * 
     * @return true if the engine is in a running state
     */
    public boolean isRunning() {
        return this == RUNNING;
    }
    
    /**
     * Checks if this state indicates the engine is initialized or running.
     * 
     * @return true if the engine has been initialized
     */
    public boolean isInitialized() {
        return this == INITIALIZED || this == RUNNING || this == STOPPING;
    }
    
    /**
     * Checks if this state indicates the engine has been shut down.
     * 
     * @return true if the engine has been shut down
     */
    public boolean isShutdown() {
        return this == SHUTDOWN || this == ERROR;
    }
}
