package com.minimax.game.engine.ecs.system;

import lombok.RequiredArgsConstructor;

/**
 * Defines execution phases for systems in the ECS world.
 * 
 * Systems are executed in phases to ensure proper dependencies are respected.
 * Systems within the same phase can potentially run in parallel, while
 * phases themselves execute sequentially.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@RequiredArgsConstructor
public enum SystemPhase {
    
    /** 
     * Input processing phase.
     * Systems that handle user input, network input, or system events.
     * Should complete before other phases to ensure up-to-date input state.
     */
    INPUT(true),
    
    /** 
     * AI and behavior processing phase.
     * Systems that make decisions, run AI logic, or handle scripted behaviors.
     * May run in parallel with INPUT phase completion.
     */
    AI(true),
    
    /** 
     * Physics and movement phase.
     * Systems that handle physics simulation, collision detection, 
     * position updates, and movement calculations.
     * Must run after AI to use latest decision data.
     */
    PHYSICS(true),
    
    /** 
     * Game logic and state management phase.
     * Systems that handle game rules, state changes, scoring,
     * and other core game logic.
     * Runs after physics to incorporate movement results.
     */
    GAME_LOGIC(true),
    
    /** 
     * Rendering preparation phase.
     * Systems that prepare data for rendering, update animation states,
     * build render batches, and organize visual elements.
     * Must run before actual rendering.
     */
    RENDER_PREP(true),
    
    /** 
     * Main rendering phase.
     * Systems that actually perform rendering, draw sprites/meshes,
     * handle lighting, and produce the final frame.
     * Must be the last phase as it produces the visual output.
     */
    RENDER(false),
    
    /** 
     * Audio processing phase.
     * Systems that handle audio playback, 3D audio positioning,
     * sound effects, and music management.
     * Can run in parallel with other phases.
     */
    AUDIO(true),
    
    /** 
     * Cleanup and maintenance phase.
     * Systems that perform cleanup tasks, memory management,
     * entity destruction, and other maintenance operations.
     * Should run last to clean up after all other processing.
     */
    CLEANUP(true);

    /** Whether systems in this phase can run in parallel */
    public final boolean parallelizable;

    /**
     * Checks if this phase can execute in parallel with another phase.
     * 
     * @param other the other phase to check
     * @return true if phases can run in parallel
     */
    public boolean canRunParallelWith(SystemPhase other) {
        if (this == other) return true;
        
        // Certain phases have strict dependencies and cannot run in parallel
        switch (this) {
            case INPUT:
                // INPUT should complete before other phases use input data
                return false;
                
            case PHYSICS:
                // Physics should complete before GAME_LOGIC and RENDER_PREP
                return other != GAME_LOGIC && other != RENDER_PREP && other != RENDER;
                
            case GAME_LOGIC:
                // Game logic should complete before RENDER_PREP and RENDER
                return other != RENDER_PREP && other != RENDER;
                
            case RENDER_PREP:
                // Render prep should complete before RENDER
                return other != RENDER;
                
            case RENDER:
                // Render must be last
                return false;
                
            default:
                // AI, AUDIO, CLEANUP can generally run in parallel
                return other != INPUT && other != PHYSICS && 
                       other != GAME_LOGIC && other != RENDER_PREP && other != RENDER;
        }
    }

    /**
     * Gets the default priority order for phases.
     * 
     * Lower numbers execute first. This defines the recommended execution order.
     * 
     * @return the execution order priority
     */
    public int getDefaultPriority() {
        return switch (this) {
            case INPUT -> 0;
            case AI -> 1;
            case PHYSICS -> 2;
            case GAME_LOGIC -> 3;
            case AUDIO -> 3; // Can run in parallel with GAME_LOGIC
            case RENDER_PREP -> 4;
            case RENDER -> 5;
            case CLEANUP -> 6;
        };
    }

    /**
     * Gets all phases that this phase depends on.
     * 
     * These phases must complete before this phase can execute.
     * 
     * @return array of dependent phases
     */
    public SystemPhase[] getDependencies() {
        return switch (this) {
            case INPUT -> new SystemPhase[0]; // No dependencies
            case AI -> new SystemPhase[]{INPUT}; // Depends on input
            case PHYSICS -> new SystemPhase[]{INPUT, AI}; // Depends on input and AI
            case GAME_LOGIC -> new SystemPhase[]{INPUT, AI, PHYSICS}; // Depends on previous phases
            case AUDIO -> new SystemPhase[]{INPUT, AI}; // Audio can use input and AI data
            case RENDER_PREP -> new SystemPhase[]{INPUT, AI, PHYSICS, GAME_LOGIC}; // Needs all game state
            case RENDER -> new SystemPhase[]{INPUT, AI, PHYSICS, GAME_LOGIC, RENDER_PREP}; // Needs all prep work
            case CLEANUP -> new SystemPhase[]{INPUT, AI, PHYSICS, GAME_LOGIC, RENDER_PREP, RENDER, AUDIO}; // Last phase
        };
    }

    /**
     * Gets all phases that depend on this phase.
     * 
     * These phases require this phase to complete before they can execute.
     * 
     * @return array of phases that depend on this one
     */
    public SystemPhase[] getDependents() {
        return switch (this) {
            case INPUT -> new SystemPhase[]{AI, PHYSICS, GAME_LOGIC, AUDIO, RENDER_PREP, RENDER, CLEANUP};
            case AI -> new SystemPhase[]{PHYSICS, GAME_LOGIC, AUDIO, RENDER_PREP, RENDER, CLEANUP};
            case PHYSICS -> new SystemPhase[]{GAME_LOGIC, RENDER_PREP, RENDER, CLEANUP};
            case GAME_LOGIC -> new SystemPhase[]{RENDER_PREP, RENDER, CLEANUP};
            case AUDIO -> new SystemPhase[]{RENDER, CLEANUP};
            case RENDER_PREP -> new SystemPhase[]{RENDER, CLEANUP};
            case RENDER -> new SystemPhase[]{CLEANUP};
            case CLEANUP -> new SystemPhase[0]; // No dependents
        };
    }

    /**
     * Checks if this phase is required to complete before a target phase.
     * 
     * @param target the target phase to check
     * @return true if this phase must complete before the target
     */
    public boolean isRequiredFor(SystemPhase target) {
        for (SystemPhase dependency : target.getDependencies()) {
            if (this == dependency) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a human-readable description of this phase.
     * 
     * @return phase description
     */
    public String getDescription() {
        return switch (this) {
            case INPUT -> "Process user input, network events, and system messages";
            case AI -> "Run artificial intelligence, behavior trees, and decision making";
            case PHYSICS -> "Handle physics simulation, collision detection, and movement";
            case GAME_LOGIC -> "Execute game rules, state changes, and core mechanics";
            case AUDIO -> "Manage sound playback, 3D positioning, and audio effects";
            case RENDER_PREP -> "Prepare rendering data, animations, and visual elements";
            case RENDER -> "Perform actual rendering and produce the final frame";
            case CLEANUP -> "Clean up resources, destroy entities, and perform maintenance";
        };
    }

    /**
     * Gets the estimated execution time characteristics for this phase.
     * 
     * @return execution characteristics description
     */
    public String getExecutionCharacteristics() {
        return switch (this) {
            case INPUT -> "Fast, lightweight, should complete quickly";
            case AI -> "Variable complexity, may be CPU intensive";
            case PHYSICS -> "Moderate complexity, should be optimized for performance";
            case GAME_LOGIC -> "Varies by game complexity, should be deterministic";
            case AUDIO -> "Generally fast, may involve I/O operations";
            case RENDER_PREP -> "Moderate complexity, depends on entity count";
            case RENDER -> "GPU intensive, should be optimized for graphics performance";
            case CLEANUP -> "Fast, should not impact frame rate";
        };
    }

    /**
     * Gets all phases in recommended execution order.
     * 
     * @return array of phases in order
     */
    public static SystemPhase[] getExecutionOrder() {
        return new SystemPhase[]{INPUT, AI, PHYSICS, GAME_LOGIC, AUDIO, RENDER_PREP, RENDER, CLEANUP};
    }

    /**
     * Gets phases that can run in parallel with the given phase.
     * 
     * @param phase the phase to check
     * @return array of phases that can run in parallel
     */
    public static SystemPhase[] getParallelPhases(SystemPhase phase) {
        return java.util.Arrays.stream(values())
            .filter(p -> p != phase && phase.canRunParallelWith(p))
            .toArray(SystemPhase[]::new);
    }

    @Override
    public String toString() {
        return name() + " (" + getDescription() + ")";
    }
}
