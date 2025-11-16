package com.minimax.game.engine.math;

/**
 * Math utilities for the game engine.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
public final class MathUtils {
    
    private MathUtils() {
        // Utility class - no instantiation
    }
    
    /**
     * Linear interpolation between two values.
     * 
     * @param a start value
     * @param b end value
     * @param t interpolation factor (0-1)
     * @return interpolated value
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    
    /**
     * Clamps a value between min and max.
     * 
     * @param value value to clamp
     * @param min minimum value
     * @param max maximum value
     * @return clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Converts degrees to radians.
     * 
     * @param degrees angle in degrees
     * @return angle in radians
     */
    public static float toRadians(float degrees) {
        return degrees * (float) Math.PI / 180.0f;
    }
    
    /**
     * Converts radians to degrees.
     * 
     * @param radians angle in radians
     * @return angle in degrees
     */
    public static float toDegrees(float radians) {
        return radians * 180.0f / (float) Math.PI;
    }
}
