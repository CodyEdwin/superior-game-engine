package com.minimax.game.engine.demo;

import lombok.extern.slf4j.Slf4j;
import com.minimax.game.engine.core.*;
import com.minimax.game.engine.ecs.*;
import com.minimax.game.engine.ecs.component.*;
import com.minimax.game.engine.ecs.system.*;

/**
 * Demonstrates the capabilities of the Superior Game Engine.
 * 
 * This demo creates a simple 2D game scene with moving entities,
 * health system, and basic rendering to showcase the ECS architecture
 * and modern Java features.
 * 
 * Features demonstrated:
 * - Entity Component System (ECS) with Java 21 features
 * - Modern OpenGL 3.3.4 rendering capabilities
 * - Virtual threads for parallel system execution
 * - Project Lombok for clean, concise code
 * - Comprehensive error handling and logging
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
public class DemoGame {
    
    private static final String DEMO_WINDOW_TITLE = "Superior Game Engine Demo";
    private static final int DEMO_WINDOW_WIDTH = 1280;
    private static final int DEMO_WINDOW_HEIGHT = 720;
    private static final float DEMO_DURATION = 30.0f; // Run for 30 seconds
    
    private GameEngine engine;
    private boolean running = false;
    private float elapsedTime = 0.0f;

    /**
     * Main entry point for the demo.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        log.info("Starting Superior Game Engine Demo");
        
        try {
            DemoGame demo = new DemoGame();
            demo.initialize();
            demo.run();
            demo.cleanup();
            
            log.info("Demo completed successfully!");
            
        } catch (Exception e) {
            log.error("Demo failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Initializes the demo with engine configuration and sample entities.
     */
    public void initialize() throws Exception {
        log.info("Initializing demo...");
        
        // Create engine configuration
        EngineConfig config = EngineConfig.builder()
                .windowConfig(createDemoWindowConfig())
                .renderConfig(createDemoRenderConfig())
                .audioConfig(createDemoAudioConfig())
                .targetFrameRate(60)
                .virtualThreadsEnabled(true)
                .debugMode(true)
                .verboseLogging(true)
                .statisticsEnabled(true)
                .statisticsLogInterval(30) // Log every 30 frames
                .build();

        log.info("Engine configuration: {}", config);
        
        // Create and initialize the engine
        engine = new GameEngine(config);
        engine.initialize();
        
        // Register demo systems
        registerDemoSystems();
        
        // Create sample entities
        createSampleEntities();
        
        log.info("Demo initialization complete!");
    }

    /**
     * Runs the demo game loop.
     */
    public void run() {
        if (engine == null) {
            throw new IllegalStateException("Demo not initialized");
        }
        
        log.info("Starting demo game loop...");
        running = true;
        
        try {
            // Start the engine in a separate thread
            Thread gameThread = new Thread(() -> {
                try {
                    engine.start();
                } catch (Exception e) {
                    log.error("Engine error: {}", e.getMessage(), e);
                }
            }, "Game-Engine-Thread");
            
            gameThread.start();
            
            // Run demo for specified duration
            while (running && elapsedTime < DEMO_DURATION) {
                Thread.sleep(1000); // Check every second
                elapsedTime += 1.0f;
                
                log.info("Demo running... Elapsed: {:.1f}s / {:.1f}s", elapsedTime, DEMO_DURATION);
                
                // Demonstrate dynamic entity creation
                if (Math.random() < 0.1) { // 10% chance per second
                    createRandomEntity();
                }
            }
            
            running = false;
            
            // Wait for engine thread to finish
            gameThread.join();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Demo interrupted");
        }
    }

    /**
     * Cleans up resources.
     */
    public void cleanup() {
        log.info("Cleaning up demo...");
        
        if (engine != null) {
            try {
                engine.shutdown();
            } catch (Exception e) {
                log.error("Error during engine shutdown: {}", e.getMessage(), e);
            }
        }
        
        log.info("Demo cleanup complete");
    }

    /**
     * Creates the demo window configuration.
     * 
     * @return window configuration
     */
    private com.minimax.game.engine.input.WindowConfig createDemoWindowConfig() {
        return com.minimax.game.engine.input.WindowConfig.builder()
                .title(DEMO_WINDOW_TITLE)
                .width(DEMO_WINDOW_WIDTH)
                .height(DEMO_WINDOW_HEIGHT)
                .resizable(true)
                .vsyncEnabled(true)
                .build();
    }

    /**
     * Creates the demo render configuration.
     * 
     * @return render configuration
     */
    private com.minimax.game.engine.render.RenderConfig createDemoRenderConfig() {
        return com.minimax.game.engine.render.RenderConfig.builder()
                .targetVersion(com.minimax.game.engine.render.OpenGLVersion.GL_3_3)
                .debugMode(true)
                .vsyncEnabled(true)
                .multiSamplingLevel(4)
                .build();
    }

    /**
     * Creates the demo audio configuration.
     * 
     * @return audio configuration
     */
    private com.minimax.game.engine.audio.AudioConfig createDemoAudioConfig() {
        return com.minimax.game.engine.audio.AudioConfig.builder()
                .enabled(false) // Disabled for demo to avoid audio dependencies
                .masterVolume(0.5f)
                .build();
    }

    /**
     * Registers all the demo systems with the ECS world.
     */
    private void registerDemoSystems() {
        ECSWorld world = engine.getECSWorld();
        
        // Register example systems
        world.registerSystem(new MovementSystem(), SystemPhase.PHYSICS);
        world.registerSystem(new RenderSystem(), SystemPhase.RENDER);
        world.registerSystem(new HealthSystem(), SystemPhase.GAME_LOGIC);
        
        // Add more systems as needed...
        // world.registerSystem(new PhysicsSystem(), SystemPhase.PHYSICS);
        // world.registerSystem(new AISystem(), SystemPhase.AI);
        // world.registerSystem(new AudioSystem(), SystemPhase.AUDIO);
        
        log.info("Registered {} systems", world.getSystemCount());
    }

    /**
     * Creates sample entities for the demo.
     */
    private void createSampleEntities() {
        ECSWorld world = engine.getECSWorld();
        
        // Create player entity
        EntityId player = world.createEntity();
        world.addComponent(player, TransformComponent.of2D(0, 0));
        world.addComponent(player, VelocityComponent.stationary());
        world.addComponent(player, HealthComponent.healthy(100.0f));
        world.addComponent(player, SpriteComponent.of("player_sprite", "player.png", 32, 32));
        
        // Create some enemies
        for (int i = 0; i < 5; i++) {
            createEnemy();
        }
        
        // Create some projectiles
        for (int i = 0; i < 10; i++) {
            createProjectile();
        }
        
        // Create some particles/effects
        for (int i = 0; i < 20; i++) {
            createParticle();
        }
        
        log.info("Created {} demo entities", world.getEntityCount());
    }

    /**
     * Creates an enemy entity.
     */
    private void createEnemy() {
        ECSWorld world = engine.getECSWorld();
        
        EntityId enemy = world.createEntity();
        float x = (float) (Math.random() * 800 - 400); // Random position
        float y = (float) (Math.random() * 600 - 300);
        
        world.addComponent(enemy, TransformComponent.of2D(x, y));
        world.addComponent(enemy, VelocityComponent.constant(
            (float) (Math.random() * 100 - 50),  // Random velocity
            (float) (Math.random() * 100 - 50),
            0.0f
        ));
        world.addComponent(enemy, HealthComponent.healthy(50.0f));
        world.addComponent(enemy, SpriteComponent.of("enemy_sprite", "enemy.png", 24, 24));
    }

    /**
     * Creates a projectile entity.
     */
    private void createProjectile() {
        ECSWorld world = engine.getECSWorld();
        
        EntityId projectile = world.createEntity();
        float x = (float) (Math.random() * 1200 - 600);
        float y = (float) (Math.random() * 700 - 350);
        
        world.addComponent(projectile, TransformComponent.of2D(x, y));
        world.addComponent(projectile, VelocityComponent.constant(
            (float) (Math.random() * 200 - 100),
            (float) (Math.random() * 200 - 100),
            0.0f
        ));
        world.addComponent(projectile, SpriteComponent.of("projectile_sprite", "bullet.png", 8, 8));
    }

    /**
     * Creates a particle/effect entity.
     */
    private void createParticle() {
        ECSWorld world = engine.getECSWorld();
        
        EntityId particle = world.createEntity();
        float x = (float) (Math.random() * 1200 - 600);
        float y = (float) (Math.random() * 700 - 350);
        
        world.addComponent(particle, TransformComponent.of2D(x, y));
        world.addComponent(particle, VelocityComponent.constant(
            (float) (Math.random() * 50 - 25),
            (float) (Math.random() * 50 - 25),
            0.0f
        ));
        world.addComponent(particle, SpriteComponent.of("particle_sprite", "particle.png", 4, 4));
    }

    /**
     * Creates a random entity with random components.
     */
    private void createRandomEntity() {
        ECSWorld world = engine.getECSWorld();
        
        EntityId entity = world.createEntity();
        
        // Random position
        float x = (float) (Math.random() * 1200 - 600);
        float y = (float) (Math.random() * 700 - 350);
        world.addComponent(entity, TransformComponent.of2D(x, y));
        
        // Random velocity (30% chance)
        if (Math.random() < 0.3) {
            world.addComponent(entity, VelocityComponent.constant(
                (float) (Math.random() * 100 - 50),
                (float) (Math.random() * 100 - 50),
                0.0f
            ));
        }
        
        // Random health (20% chance)
        if (Math.random() < 0.2) {
            world.addComponent(entity, HealthComponent.healthy(25.0f));
        }
        
        // Always add a sprite
        world.addComponent(entity, SpriteComponent.of(
            "random_sprite", 
            "entity.png", 
            (float) (16 + Math.random() * 16), 
            (float) (16 + Math.random() * 16)
        ));
        
        if (world.getEntityCount() % 10 == 0) {
            log.info("Total entities: {}", world.getEntityCount());
        }
    }

    /**
     * Gets engine statistics for display.
     * 
     * @return formatted statistics
     */
    public String getStatistics() {
        if (engine == null) {
            return "Engine not initialized";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== Demo Statistics ===\n");
        sb.append("Elapsed time: ").append(String.format("%.1f", elapsedTime)).append("s\n");
        sb.append("Entities: ").append(engine.getECSWorld().getEntityCount()).append("\n");
        sb.append("Systems: ").append(engine.getECSWorld().getSystemCount()).append("\n");
        sb.append(engine.getStatistics().getPerformanceReport());
        
        return sb.toString();
    }
}
