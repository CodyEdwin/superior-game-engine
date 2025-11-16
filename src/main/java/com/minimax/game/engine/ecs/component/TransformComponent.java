package com.minimax.game.engine.ecs.component;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Component representing the position and transformation of an entity.
 * 
 * This component stores the location, rotation, and scale of an entity
 * in 3D space, essential for both physics and rendering systems.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@EqualsAndHashCode
public final record TransformComponent(
    /** X coordinate */
    float x,
    /** Y coordinate */  
    float y,
    /** Z coordinate */
    float z,
    /** Rotation around X axis in degrees */
    float rotationX,
    /** Rotation around Y axis in degrees */
    float rotationY,
    /** Rotation around Z axis in degrees */
    float rotationZ,
    /** Scale factor along X axis */
    float scaleX,
    /** Scale factor along Y axis */
    float scaleY,
    /** Scale factor along Z axis */
    float scaleZ
) implements Component {
    
    /**
     * Creates a transform component at the origin with no rotation or scale.
     */
    public TransformComponent() {
        this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Creates a 2D transform component (z=0, no rotation, uniform scale).
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param scale uniform scale factor
     */
    public TransformComponent(float x, float y, float scale) {
        this(x, y, 0.0f, 0.0f, 0.0f, 0.0f, scale, scale, scale);
    }
    
    /**
     * Creates a transform with position and rotation.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param rotationX rotation around X axis
     * @param rotationY rotation around Y axis
     * @param rotationZ rotation around Z axis
     */
    public TransformComponent(float x, float y, float z, float rotationX, float rotationY, float rotationZ) {
        this(x, y, z, rotationX, rotationY, rotationZ, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Creates a transform with position only.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public TransformComponent(float x, float y, float z) {
        this(x, y, z, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Gets the position as a coordinate tuple.
     * 
     * @return array containing [x, y, z] coordinates
     */
    public float[] getPosition() {
        return new float[]{x, y, z};
    }
    
    /**
     * Gets the rotation as a tuple.
     * 
     * @return array containing [rotationX, rotationY, rotationZ] in degrees
     */
    public float[] getRotation() {
        return new float[]{rotationX, rotationY, rotationZ};
    }
    
    /**
     * Gets the scale as a tuple.
     * 
     * @return array containing [scaleX, scaleY, scaleZ]
     */
    public float[] getScale() {
        return new float[]{scaleX, scaleY, scaleZ};
    }
    
    /**
     * Creates a new transform with updated position.
     * 
     * @param newX new X coordinate
     * @param newY new Y coordinate
     * @param newZ new Z coordinate
     * @return new transform component with updated position
     */
    public TransformComponent withPosition(float newX, float newY, float newZ) {
        return new TransformComponent(newX, newY, newZ, rotationX, rotationY, rotationZ, scaleX, scaleY, scaleZ);
    }
    
    /**
     * Creates a new transform with updated rotation.
     * 
     * @param newRotationX new rotation around X axis
     * @param newRotationY new rotation around Y axis
     * @param newRotationZ new rotation around Z axis
     * @return new transform component with updated rotation
     */
    public TransformComponent withRotation(float newRotationX, float newRotationY, float newRotationZ) {
        return new TransformComponent(x, y, z, newRotationX, newRotationY, newRotationZ, scaleX, scaleY, scaleZ);
    }
    
    /**
     * Creates a new transform with updated scale.
     * 
     * @param newScaleX new X scale
     * @param newScaleY new Y scale
     * @param newScaleZ new Z scale
     * @return new transform component with updated scale
     */
    public TransformComponent withScale(float newScaleX, float newScaleY, float newScaleZ) {
        return new TransformComponent(x, y, z, rotationX, rotationY, rotationZ, newScaleX, newScaleY, newScaleZ);
    }
    
    /**
     * Creates a new transform with uniform scaling.
     * 
     * @param uniformScale uniform scale factor for all axes
     * @return new transform component with uniform scale
     */
    public TransformComponent withUniformScale(float uniformScale) {
        return new TransformComponent(x, y, z, rotationX, rotationY, rotationZ, uniformScale, uniformScale, uniformScale);
    }
    
    /**
     * Calculates the distance to another transform.
     * 
     * @param other the other transform
     * @return the Euclidean distance in 3D space
     */
    public float distanceTo(TransformComponent other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        float dz = this.z - other.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Translates this transform by the given delta values.
     * 
     * @param deltaX X translation
     * @param deltaY Y translation
     * @param deltaZ Z translation
     * @return new transform component with translated position
     */
    public TransformComponent translate(float deltaX, float deltaY, float deltaZ) {
        return new TransformComponent(x + deltaX, y + deltaY, z + deltaZ, 
                                    rotationX, rotationY, rotationZ, scaleX, scaleY, scaleZ);
    }
    
    /**
     * Rotates this transform by the given delta values.
     * 
     * @param deltaRotationX X rotation delta
     * @param deltaRotationY Y rotation delta
     * @param deltaRotationZ Z rotation delta
     * @return new transform component with rotated values
     */
    public TransformComponent rotate(float deltaRotationX, float deltaRotationY, float deltaRotationZ) {
        return new TransformComponent(x, y, z, 
                                    rotationX + deltaRotationX,
                                    rotationY + deltaRotationY,
                                    rotationZ + deltaRotationZ, 
                                    scaleX, scaleY, scaleZ);
    }
    
    /**
     * Scales this transform by the given factors.
     * 
     * @param scaleFactorX X scale factor
     * @param scaleFactorY Y scale factor
     * @param scaleFactorZ Z scale factor
     * @return new transform component with scaled values
     */
    public TransformComponent scale(float scaleFactorX, float scaleFactorY, float scaleFactorZ) {
        return new TransformComponent(x, y, z, 
                                    rotationX, rotationY, rotationZ, 
                                    scaleX * scaleFactorX,
                                    scaleY * scaleFactorY,
                                    scaleZ * scaleFactorZ);
    }
    
    /**
     * Creates a 2D transform with only X and Y coordinates.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @return 2D transform component
     */
    public static TransformComponent of2D(float x, float y) {
        return new TransformComponent(x, y, 1.0f);
    }
    
    /**
     * Creates a 3D transform at the origin.
     * 
     * @return transform component at origin with identity rotation and scale
     */
    public static TransformComponent identity() {
        return new TransformComponent();
    }
    
    /**
     * Creates a transform for an entity centered at the given position.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param size uniform size for the entity
     * @return transform component with specified position and size
     */
    public static TransformComponent centered(float x, float y, float z, float size) {
        return new TransformComponent(x, y, z, 0.0f, 0.0f, 0.0f, size, size, size);
    }
}
