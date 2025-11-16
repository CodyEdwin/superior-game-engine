package com.minimax.game.engine.ecs.system;

import com.minimax.game.engine.ecs.ECSWorld;
import com.minimax.game.engine.ecs.component.TransformComponent;
import com.minimax.game.engine.ecs.component.VelocityComponent;
import com.minimax.game.engine.ecs.EntityId;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;

/**
 * Example system that handles movement and position updates for entities.
 * 
 * This system processes entities that have both TransformComponent and
 * VelocityComponent, updating their positions based on their velocity
 * and applying physics simulation.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
public class MovementSystem extends System {
    
    /** Speed multiplier for testing and balancing */
    private static final float SPEED_MULTIPLIER = 1.0f;
    
    /** Maximum position values to prevent entity loss */
    private static final float WORLD_BOUNDS = 1000.0f;

    public MovementSystem() {
        super("MovementSystem");
        setExecutionPhase(SystemPhase.PHYSICS);
        setPriority(10); // Lower priority within physics phase
    }

    @Override
    public void update(ECSWorld world, float deltaTime) throws Exception {
        if (!isEnabled()) {
            return;
        }

        // Get all entities that have both Transform and Velocity components
        List<EntityId> movingEntities = world.getEntitiesWithComponents(
            TransformComponent.class,
            VelocityComponent.class
        );

        if (movingEntities.isEmpty()) {
            return;
        }

        debugLog("Processing {} moving entities", movingEntities.size());

        // Process each moving entity
        for (EntityId entityId : movingEntities) {
            try {
                processMovement(world, entityId, deltaTime);
            } catch (Exception e) {
                errorLog("Failed to process movement for entity {}: {}", entityId, e.getMessage());
                // Continue processing other entities even if one fails
            }
        }
    }

    /**
     * Processes movement for a single entity.
     * 
     * @param world the ECS world
     * @param entityId the entity ID to process
     * @param deltaTime time step in seconds
     */
    private void processMovement(ECSWorld world, EntityId entityId, float deltaTime) {
        // Get current components
        TransformComponent transform = world.getComponent(entityId, TransformComponent.class);
        VelocityComponent velocity = world.getComponent(entityId, VelocityComponent.class);

        if (transform == null || velocity == null) {
            return; // Entity no longer has required components
        }

        // Apply acceleration and update velocity
        VelocityComponent updatedVelocity = velocity.applyAcceleration(deltaTime)
                                                   .applyFriction(deltaTime)
                                                   .clampToMaxSpeed();

        // Update position based on velocity
        float deltaX = updatedVelocity.velocityX() * deltaTime * SPEED_MULTIPLIER;
        float deltaY = updatedVelocity.velocityY() * deltaTime * SPEED_MULTIPLIER;
        float deltaZ = updatedVelocity.velocityZ() * deltaTime * SPEED_MULTIPLIER;

        TransformComponent updatedTransform = transform.translate(deltaX, deltaY, deltaZ);

        // Keep entities within world bounds
        updatedTransform = clampToWorldBounds(updatedTransform);

        // Update components in the ECS world
        world.addComponent(entityId, updatedTransform);
        world.addComponent(entityId, updatedVelocity);

        debugLog("Moved entity {} from ({}, {}, {}) to ({}, {}, {})", 
                entityId,
                transform.x(), transform.y(), transform.z(),
                updatedTransform.x(), updatedTransform.y(), updatedTransform.z());
    }

    /**
     * Clamps entity position to world boundaries.
     * 
     * @param transform the transform to clamp
     * @return clamped transform
     */
    private TransformComponent clampToWorldBounds(TransformComponent transform) {
        float clampedX = Math.max(-WORLD_BOUNDS, Math.min(WORLD_BOUNDS, transform.x()));
        float clampedY = Math.max(-WORLD_BOUNDS, Math.min(WORLD_BOUNDS, transform.y()));
        float clampedZ = Math.max(-WORLD_BOUNDS, Math.min(WORLD_BOUNDS, transform.z()));

        if (clampedX != transform.x() || clampedY != transform.y() || clampedZ != transform.z()) {
            warnLog("Entity position clamped to world bounds: ({}, {}, {})", 
                   clampedX, clampedY, clampedZ);
            return new TransformComponent(clampedX, clampedY, clampedZ, 
                                        transform.rotationX(), transform.rotationY(), transform.rotationZ(),
                                        transform.scaleX(), transform.scaleY(), transform.scaleZ());
        }

        return transform;
    }

    @Override
    public String toString() {
        return String.format("MovementSystem{movingEntities=%d}", 
                           getMovingEntityCount());
    }

    /**
     * Gets the estimated count of moving entities (for statistics).
     * 
     * @return estimated number of moving entities
     */
    private int getMovingEntityCount() {
        // This would need access to the world to get actual count
        // For now, return a placeholder
        return 0;
    }
}

/**
 * Example system that handles rendering of sprites and visual elements.
 * 
 * This system processes entities that have both TransformComponent and
 * SpriteComponent, preparing them for the rendering pipeline.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
public class RenderSystem extends System {
    
    /** Current render batch for organization */
    private final List<RenderableEntity> renderBatch = new ArrayList<>();

    public RenderSystem() {
        super("RenderSystem");
        setExecutionPhase(SystemPhase.RENDER);
        setPriority(100); // High priority within render phase
    }

    @Override
    public void update(ECSWorld world, float deltaTime) throws Exception {
        if (!isEnabled()) {
            return;
        }

        // Clear previous render batch
        renderBatch.clear();

        // Get all renderable entities (those with transform and sprite)
        List<EntityId> renderableEntities = world.getEntitiesWithComponents(
            TransformComponent.class,
            com.minimax.game.engine.ecs.component.SpriteComponent.class
        );

        if (renderableEntities.isEmpty()) {
            return;
        }

        debugLog("Processing {} renderable entities", renderableEntities.size());

        // Build render batch
        for (EntityId entityId : renderableEntities) {
            try {
                TransformComponent transform = world.getComponent(entityId, TransformComponent.class);
                var sprite = world.getComponent(entityId, com.minimax.game.engine.ecs.component.SpriteComponent.class);

                if (transform != null && sprite != null && sprite.visible()) {
                    RenderableEntity renderable = new RenderableEntity(entityId, transform, sprite);
                    renderBatch.add(renderable);
                }
            } catch (Exception e) {
                errorLog("Failed to add entity {} to render batch: {}", entityId, e.getMessage());
            }
        }

        // Sort by layer for proper rendering order
        renderBatch.sort((a, b) -> Integer.compare(a.sprite.layer(), b.sprite.layer()));

        // Render all entities in batch
        renderEntities();

        debugLog("Rendered {} entities in {} layers", 
                renderBatch.size(), 
                renderBatch.stream().mapToInt(e -> e.sprite.layer()).distinct().count());
    }

    /**
     * Renders all entities in the current batch.
     */
    private void renderEntities() {
        if (renderBatch.isEmpty()) {
            return;
        }

        // Group by layer for efficient rendering
        int currentLayer = renderBatch.get(0).sprite.layer();
        
        for (RenderableEntity renderable : renderBatch) {
            if (renderable.sprite.layer() != currentLayer) {
                // Switch to new layer
                currentLayer = renderable.sprite.layer();
                debugLog("Rendering layer {}", currentLayer);
            }
            
            renderEntity(renderable);
        }
    }

    /**
     * Renders a single entity.
     * 
     * @param renderable the entity to render
     */
    private void renderEntity(RenderableEntity renderable) {
        debugLog("Rendering entity {} at ({}, {}, {}) with texture '{}'", 
                renderable.entityId,
                renderable.transform.x(), renderable.transform.y(), renderable.transform.z(),
                renderable.sprite.textureId());

        // In a real implementation, this would:
        // 1. Bind the appropriate shader program
        // 2. Set up the vertex buffer objects
        // 3. Apply transformations (position, rotation, scale)
        // 4. Set texture and material properties
        // 5. Draw the geometry
        // 6. Clean up OpenGL state
    }

    /**
     * Represents an entity ready for rendering.
     * 
     * @author MiniMax Agent
     * @version 1.0.0
     */
    private record RenderableEntity(
        EntityId entityId,
        TransformComponent transform,
        com.minimax.game.engine.ecs.component.SpriteComponent sprite
    ) {}

    @Override
    public String toString() {
        return String.format("RenderSystem{renderBatch=%d}", renderBatch.size());
    }
}

/**
 * Example system that handles health and damage mechanics.
 * 
 * This system processes entities with HealthComponent, handling
 * health regeneration, death detection, and damage effects.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
public class HealthSystem extends System {

    public HealthSystem() {
        super("HealthSystem");
        setExecutionPhase(SystemPhase.GAME_LOGIC);
        setPriority(20); // Medium priority within game logic phase
    }

    @Override
    public void update(ECSWorld world, float deltaTime) throws Exception {
        if (!isEnabled()) {
            return;
        }

        // Get all entities with health components
        List<EntityId> entitiesWithHealth = world.getEntitiesWithComponents(
            com.minimax.game.engine.ecs.component.HealthComponent.class
        );

        if (entitiesWithHealth.isEmpty()) {
            return;
        }

        debugLog("Processing {} entities with health", entitiesWithHealth.size());

        // Process health for each entity
        for (EntityId entityId : entitiesWithHealth) {
            try {
                processHealth(world, entityId, deltaTime);
            } catch (Exception e) {
                errorLog("Failed to process health for entity {}: {}", entityId, e.getMessage());
            }
        }
    }

    /**
     * Processes health for a single entity.
     * 
     * @param world the ECS world
     * @param entityId the entity ID to process
     * @param deltaTime time step in seconds
     */
    private void processHealth(ECSWorld world, EntityId entityId, float deltaTime) {
        var health = world.getComponent(entityId, com.minimax.game.engine.ecs.component.HealthComponent.class);

        if (health == null) {
            return;
        }

        // Apply regeneration if entity is alive and has regeneration
        com.minimax.game.engine.ecs.component.HealthComponent updatedHealth = health;
        
        if (health.isAlive() && health.regenerationRate() > 0.0f) {
            updatedHealth = health.applyRegeneration(deltaTime);
        }

        // Check for death
        if (health.isAlive() && !updatedHealth.isAlive()) {
            handleDeath(world, entityId, updatedHealth);
        }

        // Update health component if it changed
        if (updatedHealth != health) {
            world.addComponent(entityId, updatedHealth);
        }
    }

    /**
     * Handles the death of an entity.
     * 
     * @param world the ECS world
     * @param entityId the entity that died
     * @param finalHealth the final health state
     */
    private void handleDeath(ECSWorld world, EntityId entityId, 
                           com.minimax.game.engine.ecs.component.HealthComponent finalHealth) {
        infoLog("Entity {} has died", entityId);

        // In a real implementation, this might:
        // 1. Remove health component
        // 2. Add death animation component
        // 3. Trigger death events
        // 4. Play death sound
        // 5. Schedule entity destruction
        // 6. Award points or loot
        
        // For now, just mark the entity as inactive
        // This could be done in the entity itself or through another system
    }

    @Override
    public String toString() {
        return "HealthSystem";
    }
}
