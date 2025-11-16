package com.minimax.game.engine.ecs.component;

import lombok.EqualsAndHashCode;

/**
 * Component representing the velocity and movement state of an entity.
 * 
 * This component stores velocity vectors and acceleration for physics
 * and movement systems to work with.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@EqualsAndHashCode
public final record VelocityComponent(
    /** Velocity along X axis */
    float velocityX,
    /** Velocity along Y axis */
    float velocityY,
    /** Velocity along Z axis */
    float velocityZ,
    /** Acceleration along X axis */
    float accelerationX,
    /** Acceleration along Y axis */
    float accelerationY,
    /** Acceleration along Z axis */
    float accelerationZ,
    /** Maximum speed the entity can reach */
    float maxSpeed,
    /** Friction/drag coefficient (0-1) */
    float friction
) implements Component {
    
    /**
     * Creates a velocity component with zero velocity and acceleration.
     */
    public VelocityComponent() {
        this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.MAX_VALUE, 0.0f);
    }
    
    /**
     * Creates a velocity component for 2D movement.
     * 
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param maxSpeed maximum speed
     */
    public VelocityComponent(float velocityX, float velocityY, float maxSpeed) {
        this(velocityX, velocityY, 0.0f, 0.0f, 0.0f, 0.0f, maxSpeed, 0.0f);
    }
    
    /**
     * Creates a velocity component with velocity and acceleration.
     * 
     * @param velocityX X velocity
     * @param velocityY Y velocity
     * @param velocityZ Z velocity
     * @param accelerationX X acceleration
     * @param accelerationY Y acceleration
     * @param accelerationZ Z acceleration
     */
    public VelocityComponent(float velocityX, float velocityY, float velocityZ,
                           float accelerationX, float accelerationY, float accelerationZ) {
        this(velocityX, velocityY, velocityZ, accelerationX, accelerationY, accelerationZ, Float.MAX_VALUE, 0.0f);
    }
    
    /**
     * Gets the current velocity as a vector.
     * 
     * @return array containing [velocityX, velocityY, velocityZ]
     */
    public float[] getVelocity() {
        return new float[]{velocityX, velocityY, velocityZ};
    }
    
    /**
     * Gets the current acceleration as a vector.
     * 
     * @return array containing [accelerationX, accelerationY, accelerationZ]
     */
    public float[] getAcceleration() {
        return new float[]{accelerationX, accelerationY, accelerationZ};
    }
    
    /**
     * Calculates the magnitude of the velocity vector.
     * 
     * @return the velocity magnitude
     */
    public float getSpeed() {
        return (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
    }
    
    /**
     * Calculates the magnitude of the acceleration vector.
     * 
     * @return the acceleration magnitude
     */
    public float getAccelerationMagnitude() {
        return (float) Math.sqrt(accelerationX * accelerationX + accelerationY * accelerationY + accelerationZ * accelerationZ);
    }
    
    /**
     * Checks if the entity is stationary.
     * 
     * @return true if velocity magnitude is effectively zero
     */
    public boolean isStationary() {
        return getSpeed() < 0.001f;
    }
    
    /**
     * Creates a new velocity component with updated velocity.
     * 
     * @param newVelocityX new X velocity
     * @param newVelocityY new Y velocity
     * @param newVelocityZ new Z velocity
     * @return new velocity component with updated velocity
     */
    public VelocityComponent withVelocity(float newVelocityX, float newVelocityY, float newVelocityZ) {
        return new VelocityComponent(newVelocityX, newVelocityY, newVelocityZ,
                                   accelerationX, accelerationY, accelerationZ, maxSpeed, friction);
    }
    
    /**
     * Creates a new velocity component with updated acceleration.
     * 
     * @param newAccelerationX new X acceleration
     * @param newAccelerationY new Y acceleration
     * @param newAccelerationZ new Z acceleration
     * @return new velocity component with updated acceleration
     */
    public VelocityComponent withAcceleration(float newAccelerationX, float newAccelerationY, float newAccelerationZ) {
        return new VelocityComponent(velocityX, velocityY, velocityZ,
                                   newAccelerationX, newAccelerationY, newAccelerationZ, maxSpeed, friction);
    }
    
    /**
     * Applies acceleration for the given time step.
     * 
     * @param deltaTime time step in seconds
     * @return new velocity component with applied acceleration
     */
    public VelocityComponent applyAcceleration(float deltaTime) {
        return new VelocityComponent(
            velocityX + accelerationX * deltaTime,
            velocityY + accelerationY * deltaTime,
            velocityZ + accelerationZ * deltaTime,
            accelerationX, accelerationY, accelerationZ, maxSpeed, friction
        );
    }
    
    /**
     * Applies friction/drag.
     * 
     * @param deltaTime time step in seconds
     * @return new velocity component with applied friction
     */
    public VelocityComponent applyFriction(float deltaTime) {
        if (friction <= 0.0f) {
            return this;
        }
        
        float frictionMultiplier = Math.max(0.0f, 1.0f - friction * deltaTime);
        return new VelocityComponent(
            velocityX * frictionMultiplier,
            velocityY * frictionMultiplier,
            velocityZ * frictionMultiplier,
            accelerationX, accelerationY, accelerationZ, maxSpeed, friction
        );
    }
    
    /**
     * Clamps velocity to maximum speed.
     * 
     * @return new velocity component with clamped speed
     */
    public VelocityComponent clampToMaxSpeed() {
        float speed = getSpeed();
        if (speed <= maxSpeed || maxSpeed == Float.MAX_VALUE) {
            return this;
        }
        
        float scale = maxSpeed / speed;
        return new VelocityComponent(
            velocityX * scale,
            velocityY * scale,
            velocityZ * scale,
            accelerationX, accelerationY, accelerationZ, maxSpeed, friction
        );
    }
    
    /**
     * Stops all movement.
     * 
     * @return new velocity component with zero velocity and acceleration
     */
    public VelocityComponent stop() {
        return new VelocityComponent(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, maxSpeed, friction);
    }
    
    /**
     * Creates a stationary velocity component.
     * 
     * @return velocity component with zero values
     */
    public static VelocityComponent stationary() {
        return new VelocityComponent();
    }
    
    /**
     * Creates a velocity component with constant velocity.
     * 
     * @param x X velocity
     * @param y Y velocity
     * @param z Z velocity
     * @return velocity component with specified constant velocity
     */
    public static VelocityComponent constant(float x, float y, float z) {
        return new VelocityComponent(x, y, z, 0.0f, 0.0f, 0.0f, Float.MAX_VALUE, 0.0f);
    }
    
    /**
     * Creates a velocity component with gravitational acceleration.
     * 
     * @param gravity gravitational acceleration (typically negative for downward)
     * @return velocity component with gravity acceleration on Y axis
     */
    public static VelocityComponent withGravity(float gravity) {
        return new VelocityComponent(0.0f, 0.0f, 0.0f, 0.0f, gravity, 0.0f, Float.MAX_VALUE, 0.0f);
    }
}

/**
 * Component representing the health and damage state of an entity.
 * 
 * This component stores current health, maximum health, and damage state
 * for health management and combat systems.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@EqualsAndHashCode
public final record HealthComponent(
    /** Current health points */
    float currentHealth,
    /** Maximum health points */
    float maxHealth,
    /** Armor/damage reduction (0-1) */
    float armor,
    /** Health regeneration rate per second */
    float regenerationRate,
    /** Flag indicating if entity is alive */
    boolean isAlive
) implements Component {
    
    /**
     * Creates a health component with specified health values.
     * 
     * @param health initial health value
     */
    public HealthComponent(float health) {
        this(health, health, 0.0f, 0.0f, health > 0.0f);
    }
    
    /**
     * Creates a health component with max health.
     * 
     * @param currentHealth current health
     * @param maxHealth maximum health
     */
    public HealthComponent(float currentHealth, float maxHealth) {
        this(currentHealth, maxHealth, 0.0f, 0.0f, currentHealth > 0.0f);
    }
    
    /**
     * Creates a full health component.
     * 
     * @param maxHealth maximum health value
     * @param armor armor value (0-1)
     * @param regenerationRate regeneration rate per second
     */
    public HealthComponent(float maxHealth, float armor, float regenerationRate) {
        this(maxHealth, maxHealth, armor, regenerationRate, true);
    }
    
    /**
     * Gets the health percentage (0-1).
     * 
     * @return health percentage
     */
    public float getHealthPercentage() {
        return maxHealth > 0.0f ? Math.max(0.0f, Math.min(1.0f, currentHealth / maxHealth)) : 0.0f;
    }
    
    /**
     * Checks if the entity is at full health.
     * 
     * @return true if current health equals max health
     */
    public boolean isAtFullHealth() {
        return Math.abs(currentHealth - maxHealth) < 0.001f;
    }
    
    /**
     * Checks if the entity is critically low on health.
     * 
     * @return true if health is less than 25% of maximum
     */
    public boolean isCritical() {
        return getHealthPercentage() < 0.25f;
    }
    
    /**
     * Applies damage to the entity.
     * 
     * @param damage damage amount
     * @return new health component with applied damage
     */
    public HealthComponent takeDamage(float damage) {
        if (damage <= 0.0f || !isAlive) {
            return this;
        }
        
        float actualDamage = damage * (1.0f - Math.min(1.0f, Math.max(0.0f, armor)));
        float newHealth = Math.max(0.0f, currentHealth - actualDamage);
        boolean stillAlive = newHealth > 0.0f;
        
        return new HealthComponent(newHealth, maxHealth, armor, regenerationRate, stillAlive);
    }
    
    /**
     * Heals the entity.
     * 
     * @param healAmount healing amount
     * @return new health component with applied healing
     */
    public HealthComponent heal(float healAmount) {
        if (healAmount <= 0.0f || !isAlive) {
            return this;
        }
        
        float newHealth = Math.min(maxHealth, currentHealth + healAmount);
        return new HealthComponent(newHealth, maxHealth, armor, regenerationRate, true);
    }
    
    /**
     * Applies regeneration for the given time step.
     * 
     * @param deltaTime time step in seconds
     * @return new health component with applied regeneration
     */
    public HealthComponent applyRegeneration(float deltaTime) {
        if (!isAlive || regenerationRate <= 0.0f || isAtFullHealth()) {
            return this;
        }
        
        float healAmount = regenerationRate * deltaTime;
        return heal(healAmount);
    }
    
    /**
     * Sets the health to full.
     * 
     * @return new health component with full health
     */
    public HealthComponent restoreToFull() {
        return new HealthComponent(maxHealth, maxHealth, armor, regenerationRate, true);
    }
    
    /**
     * Kills the entity (sets health to 0).
     * 
     * @return new health component representing death
     */
    public HealthComponent kill() {
        return new HealthComponent(0.0f, maxHealth, armor, regenerationRate, false);
    }
    
    /**
     * Creates a healthy component.
     * 
     * @param maxHealth maximum health
     * @return healthy component with full health
     */
    public static HealthComponent healthy(float maxHealth) {
        return new HealthComponent(maxHealth);
    }
    
    /**
     * Creates a dead component.
     * 
     * @param maxHealth maximum health (for reference)
     * @return dead component
     */
    public static HealthComponent dead(float maxHealth) {
        return new HealthComponent(0.0f, maxHealth, 0.0f, 0.0f, false);
    }
}

/**
 * Component representing visual sprite information for an entity.
 * 
 * This component stores texture information, rendering properties,
 * and visual state for the rendering system.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@EqualsAndHashCode
public final record SpriteComponent(
    /** Texture asset identifier */
    String textureId,
    /** Sprite width in world units */
    float width,
    /** Sprite height in world units */
    float height,
    /** Horizontal offset from entity position */
    float offsetX,
    /** Vertical offset from entity position */
    float offsetY,
    /** Sprite layer/depth for rendering order */
    int layer,
    /** Opacity (0-1, where 1 is fully opaque) */
    float opacity,
    /** Flag indicating if sprite is visible */
    boolean visible,
    /** Tint color as RGB values (0-1) */
    float[] tintColor,
    /** Flip sprite horizontally */
    boolean flippedX,
    /** Flip sprite vertically */
    boolean flippedY
) implements Component {
    
    /**
     * Creates a basic sprite component.
     * 
     * @param textureId the texture identifier
     */
    public SpriteComponent(String textureId) {
        this(textureId, 1.0f, 1.0f, 0.0f, 0.0f, 0, 1.0f, true, new float[]{1.0f, 1.0f, 1.0f}, false, false);
    }
    
    /**
     * Creates a sprite component with size.
     * 
     * @param textureId the texture identifier
     * @param width sprite width
     * @param height sprite height
     */
    public SpriteComponent(String textureId, float width, float height) {
        this(textureId, width, height, 0.0f, 0.0f, 0, 1.0f, true, new float[]{1.0f, 1.0f, 1.0f}, false, false);
    }
    
    /**
     * Creates a sprite component with layer and visibility.
     * 
     * @param textureId the texture identifier
     * @param layer rendering layer
     * @param visible visibility flag
     */
    public SpriteComponent(String textureId, int layer, boolean visible) {
        this(textureId, 1.0f, 1.0f, 0.0f, 0.0f, layer, 1.0f, visible, new float[]{1.0f, 1.0f, 1.0f}, false, false);
    }
    
    /**
     * Gets the center offset from entity position.
     * 
     * @return array containing [offsetX, offsetY]
     */
    public float[] getOffset() {
        return new float[]{offsetX, offsetY};
    }
    
    /**
     * Checks if the sprite is fully opaque.
     * 
     * @return true if opacity is 1.0
     */
    public boolean isOpaque() {
        return opacity >= 0.999f;
    }
    
    /**
     * Checks if the sprite has no tint.
     * 
     * @return true if tint is white (1, 1, 1)
     */
    public boolean isUntinted() {
        return tintColor[0] == 1.0f && tintColor[1] == 1.0f && tintColor[2] == 1.0f;
    }
    
    /**
     * Creates a new sprite component with updated visibility.
     * 
     * @param visible new visibility state
     * @return new sprite component with updated visibility
     */
    public SpriteComponent withVisibility(boolean visible) {
        return new SpriteComponent(textureId, width, height, offsetX, offsetY, layer, 
                                 opacity, visible, tintColor, flippedX, flippedY);
    }
    
    /**
     * Creates a new sprite component with updated opacity.
     * 
     * @param newOpacity new opacity value (0-1)
     * @return new sprite component with updated opacity
     */
    public SpriteComponent withOpacity(float newOpacity) {
        return new SpriteComponent(textureId, width, height, offsetX, offsetY, layer, 
                                 Math.max(0.0f, Math.min(1.0f, newOpacity)), 
                                 visible, tintColor, flippedX, flippedY);
    }
    
    /**
     * Creates a new sprite component with updated tint color.
     * 
     * @param r red component (0-1)
     * @param g green component (0-1)
     * @param b blue component (0-1)
     * @return new sprite component with updated tint
     */
    public SpriteComponent withTint(float r, float g, float b) {
        return new SpriteComponent(textureId, width, height, offsetX, offsetY, layer, 
                                 opacity, visible, new float[]{r, g, b}, flippedX, flippedY);
    }
    
    /**
     * Creates a new sprite component with updated layer.
     * 
     * @param newLayer new rendering layer
     * @return new sprite component with updated layer
     */
    public SpriteComponent withLayer(int newLayer) {
        return new SpriteComponent(textureId, width, height, offsetX, offsetY, newLayer, 
                                 opacity, visible, tintColor, flippedX, flippedY);
    }
    
    /**
     * Creates a new sprite component with updated size.
     * 
     * @param newWidth new width
     * @param newHeight new height
     * @return new sprite component with updated size
     */
    public SpriteComponent withSize(float newWidth, float newHeight) {
        return new SpriteComponent(textureId, newWidth, newHeight, offsetX, offsetY, layer, 
                                 opacity, visible, tintColor, flippedX, flippedY);
    }
    
    /**
     * Creates a new sprite component with flip toggled.
     * 
     * @param flipX flip horizontally
     * @param flipY flip vertically
     * @return new sprite component with updated flip state
     */
    public SpriteComponent withFlip(boolean flipX, boolean flipY) {
        return new SpriteComponent(textureId, width, height, offsetX, offsetY, layer, 
                                 opacity, visible, tintColor, flipX, flipY);
    }
    
    /**
     * Creates an invisible sprite component.
     * 
     * @param textureId the texture identifier
     * @return invisible sprite component
     */
    public static SpriteComponent invisible(String textureId) {
        return new SpriteComponent(textureId, 1.0f, 1.0f, 0.0f, 0.0f, 0, 1.0f, false, 
                                 new float[]{1.0f, 1.0f, 1.0f}, false, false);
    }
    
    /**
     * Creates a semi-transparent sprite component.
     * 
     * @param textureId the texture identifier
     * @param opacity opacity value (0-1)
     * @return semi-transparent sprite component
     */
    public static SpriteComponent transparent(String textureId, float opacity) {
        return new SpriteComponent(textureId, 1.0f, 1.0f, 0.0f, 0.0f, 0, 
                                 Math.max(0.0f, Math.min(1.0f, opacity)), 
                                 true, new float[]{1.0f, 1.0f, 1.0f}, false, false);
    }
}
