package com.minimax.game.engine.core;

import lombok.extern.slf4j.Slf4j;
import com.minimax.game.engine.ecs.ECSWorld;
import com.minimax.game.engine.render.RenderManager;
import com.minimax.game.engine.input.InputManager;
import com.minimax.game.engine.audio.AudioManager;
import com.minimax.game.engine.asset.AssetManager;
import com.minimax.game.engine.util.VirtualThreadExecutor;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.Future;
import java.time.Duration;
import java.time.Instant;

/**
 * The central orchestrator of the Superior Game Engine.
 * 
 * This class manages the entire game engine lifecycle and coordinates between
 * all subsystems including rendering, audio, input, and the ECS world.
 * 
 * Key Features:
 * - Java 21 Virtual Threads for parallel system execution
 * - Automatic resource management and cleanup
 * - Structured concurrency for coordinated system updates
 * - Thread-safe state management
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
public final class GameEngine {
    
    /** Engine configuration and settings */
    private final EngineConfig config;
    
    /** The ECS world containing all game entities and systems */
    private final ECSWorld ecsWorld;
    
    /** Manages rendering operations and OpenGL context */
    private final RenderManager renderManager;
    
    /** Handles all input from keyboard, mouse, and game controllers */
    private final InputManager inputManager;
    
    /** Manages audio playback and spatial audio */
    private final AudioManager audioManager;
    
    /** Handles loading and caching of game assets */
    private final AssetManager assetManager;
    
    /** Virtual thread executor for parallel system processing */
    private final VirtualThreadExecutor threadExecutor;
    
    /** Engine state tracking */
    private volatile EngineState state = EngineState.UNINITIALIZED;
    
    /** Timing information for frame calculation */
    private Instant lastFrameTime;
    private float targetFrameTime;
    
    /** Statistics tracking */
    private final EngineStatistics statistics = new EngineStatistics();

    /**
     * Creates a new GameEngine with the specified configuration.
     * 
     * @param config the engine configuration containing window settings,
     *               rendering options, and other engine parameters
     */
    public GameEngine(EngineConfig config) {
        this.config = config;
        this.ecsWorld = new ECSWorld();
        this.renderManager = new RenderManager(config.renderConfig());
        this.inputManager = new InputManager(config.windowConfig());
        this.audioManager = new AudioManager(config.audioConfig());
        this.assetManager = new AssetManager();
        this.threadExecutor = new VirtualThreadExecutor();
        
        // Calculate target frame time for performance monitoring
        this.targetFrameTime = 1.0f / config.targetFrameRate();
        this.lastFrameTime = Instant.now();
        
        log.info("GameEngine created with config: {}", config);
    }

    /**
     * Initializes all engine subsystems.
     * 
     * This method must be called before starting the game loop.
     * It initializes OpenGL context, audio system, input handling,
     * and any other required subsystems.
     * 
     * @throws EngineException if initialization fails
     */
    public void initialize() throws EngineException {
        log.info("Initializing GameEngine subsystems...");
        
        try {
            // Initialize subsystems in dependency order
            renderManager.initialize();
            log.info("✓ RenderManager initialized");
            
            inputManager.initialize();
            log.info("✓ InputManager initialized");
            
            audioManager.initialize();
            log.info("✓ AudioManager initialized");
            
            assetManager.initialize();
            log.info("✓ AssetManager initialized");
            
            ecsWorld.initialize();
            log.info("✓ ECS World initialized");
            
            threadExecutor.initialize();
            log.info("✓ VirtualThreadExecutor initialized");
            
            state = EngineState.INITIALIZED;
            log.info("GameEngine initialization complete!");
            
        } catch (Exception e) {
            state = EngineState.ERROR;
            log.error("Failed to initialize GameEngine: {}", e.getMessage(), e);
            throw new EngineException("Engine initialization failed", e);
        }
    }

    /**
     * Starts the main game loop.
     * 
     * This method blocks until the game is stopped or encounters an error.
     * It coordinates parallel execution of game systems using Java 21's
     * structured concurrency for optimal performance.
     * 
     * @throws EngineException if the game loop encounters a fatal error
     */
    public void start() throws EngineException {
        if (state != EngineState.INITIALIZED) {
            throw new EngineException("Engine must be initialized before starting. Current state: " + state);
        }
        
        log.info("Starting main game loop...");
        state = EngineState.RUNNING;
        
        try {
            while (state == EngineState.RUNNING && !Thread.currentThread().isInterrupted()) {
                gameLoop();
            }
        } catch (Exception e) {
            state = EngineState.ERROR;
            log.error("Game loop error: {}", e.getMessage(), e);
            throw new EngineException("Game loop failed", e);
        } finally {
            log.info("Game loop ended. Final state: {}", state);
        }
    }

    /**
     * Executes a single frame of the game loop.
     * 
     * This method coordinates the update and render phases using structured
     * concurrency to ensure proper system coordination and resource management.
     */
    private void gameLoop() {
        final Instant frameStart = Instant.now();
        final float deltaTime = calculateDeltaTime();
        
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<>()) {
            // Update phase: Run systems in parallel where possible
            scope.fork(() -> {
                update(deltaTime);
                return null;
            });
            
            // Render phase: Run after updates complete
            scope.fork(() -> {
                render();
                return null;
            });
            
            // Wait for all systems to complete
            scope.join();
            
        } catch (Exception e) {
            log.error("Frame execution failed: {}", e.getMessage(), e);
            state = EngineState.ERROR;
        }
        
        // Update statistics
        final Instant frameEnd = Instant.now();
        final float frameTime = (float) Duration.between(frameStart, frameEnd).toNanos() / 1_000_000_000f;
        statistics.recordFrame(frameTime);
        
        // Frame limiting if needed
        performFrameLimiting(frameTime);
        
        // Log performance periodically
        if (statistics.shouldLogPerformance()) {
            log.info("Frame time: {:.2f}ms, FPS: {:.1f}, Entities: {}, Systems: {}", 
                    frameTime * 1000, statistics.getAverageFPS(), 
                    ecsWorld.getEntityCount(), ecsWorld.getSystemCount());
        }
    }

    /**
     * Updates all game systems.
     * 
     * This method runs all registered systems in parallel where possible,
     * using the ECS world's system dependencies to ensure proper execution order.
     * 
     * @param deltaTime the time elapsed since the last frame in seconds
     */
    private void update(float deltaTime) {
        // Update input system first
        inputManager.update(deltaTime);
        
        // Update all ECS systems
        ecsWorld.update(deltaTime);
        
        // Update audio system
        audioManager.update(deltaTime);
        
        // Update asset manager (background loading, etc.)
        assetManager.update(deltaTime);
    }

    /**
     * Renders the current frame.
     * 
     * This method clears the screen, updates the render view,
     * and draws all renderable entities from the ECS world.
     */
    private void render() {
        renderManager.beginFrame();
        renderManager.render(ecsWorld);
        renderManager.endFrame();
    }

    /**
     * Calculates the time elapsed since the last frame.
     * 
     * @return the delta time in seconds
     */
    private float calculateDeltaTime() {
        final Instant now = Instant.now();
        final float deltaTime = (float) Duration.between(lastFrameTime, now).toNanos() / 1_000_000_000f;
        lastFrameTime = now;
        
        // Clamp delta time to prevent large jumps (e.g., when debugger breaks)
        return Math.min(deltaTime, 0.1f);
    }

    /**
     * Performs frame limiting if the frame rate is too high.
     * 
     * @param frameTime the time taken for the current frame in seconds
     */
    private void performFrameLimiting(float frameTime) {
        if (config.frameLimitEnabled() && frameTime < targetFrameTime) {
            final float sleepTime = targetFrameTime - frameTime;
            try {
                Thread.sleep((long) (sleepTime * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.debug("Frame limiting interrupted");
            }
        }
    }

    /**
     * Stops the game engine gracefully.
     * 
     * This method signals the game loop to stop and begins cleanup
     * of all engine resources.
     */
    public void stop() {
        log.info("Stopping GameEngine...");
        state = EngineState.STOPPING;
        
        // Signal systems to stop
        inputManager.shutdown();
        audioManager.shutdown();
        assetManager.shutdown();
        ecsWorld.shutdown();
        threadExecutor.shutdown();
    }

    /**
     * Shuts down the engine and releases all resources.
     * 
     * This method should be called when the engine is no longer needed.
     * It ensures proper cleanup of OpenGL resources, audio contexts,
     * and other native resources.
     */
    public void shutdown() {
        log.info("Shutting down GameEngine...");
        
        try {
            // Cleanup in reverse order of initialization
            threadExecutor.shutdown();
            ecsWorld.dispose();
            assetManager.dispose();
            audioManager.dispose();
            inputManager.dispose();
            renderManager.dispose();
            
            state = EngineState.SHUTDOWN;
            log.info("GameEngine shutdown complete. Final statistics:\n{}", statistics);
            
        } catch (Exception e) {
            log.error("Error during engine shutdown: {}", e.getMessage(), e);
            state = EngineState.ERROR;
        }
    }

    /**
     * Gets the current engine state.
     * 
     * @return the current engine state
     */
    public EngineState getState() {
        return state;
    }

    /**
     * Gets the ECS world instance.
     * 
     * @return the ECS world containing all game entities and systems
     */
    public ECSWorld getECSWorld() {
        return ecsWorld;
    }

    /**
     * Gets the render manager.
     * 
     * @return the render manager for graphics operations
     */
    public RenderManager getRenderManager() {
        return renderManager;
    }

    /**
     * Gets the input manager.
     * 
     * @return the input manager for handling user input
     */
    public InputManager getInputManager() {
        return inputManager;
    }

    /**
     * Gets the audio manager.
     * 
     * @return the audio manager for sound operations
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * Gets the asset manager.
     * 
     * @return the asset manager for loading and managing game assets
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Gets the engine configuration.
     * 
     * @return the engine configuration
     */
    public EngineConfig getConfig() {
        return config;
    }

    /**
     * Gets the engine statistics.
     * 
     * @return runtime performance and usage statistics
     */
    public EngineStatistics getStatistics() {
        return statistics;
    }
}
