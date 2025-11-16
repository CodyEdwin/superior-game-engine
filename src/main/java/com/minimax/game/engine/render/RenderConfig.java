package com.minimax.game.engine.render;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Configuration for the rendering system.
 * 
 * Contains all settings related to OpenGL rendering, including
 * context creation, shader management, and performance options.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Data
@Builder
public final class RenderConfig {
    
    /** Target OpenGL context version (major.minor) */
    @NotNull
    @Builder.Default
    private final OpenGLVersion targetVersion = OpenGLVersion.GL_3_3;
    
    /** Enable OpenGL debug output */
    @Builder.Default
    private final boolean debugMode = false;
    
    /** Enable OpenGL error checking */
    @Builder.Default
    private final boolean errorChecking = true;
    
    /** Enable v-sync for frame synchronization */
    @Builder.Default
    private final boolean vsyncEnabled = true;
    
    /** Multi-sampling level for anti-aliasing (0 = disabled) */
    @Min(0)
    @Max(16)
    @Builder.Default
    private final int multiSamplingLevel = 4;
    
    /** Target frame rate for vsync (0 = uncapped) */
    @Min(0)
    @Max(300)
    @Builder.Default
    private final int targetFrameRate = 60;
    
    /** Enable automatic mipmap generation */
    @Builder.Default
    private final boolean autoMipmapGeneration = true;
    
    /** Enable texture compression */
    @Builder.Default
    private final boolean textureCompression = true;
    
    /** Maximum texture size in pixels */
    @Min(256)
    @Max(16384)
    @Builder.Default
    private final int maxTextureSize = 4096;
    
    /** Number of texture units available */
    @Min(8)
    @Max(128)
    @Builder.Default
    private final int maxTextureUnits = 32;
    
    /** Maximum number of vertices that can be rendered in a single draw call */
    @Min(1000)
    @Max(1000000)
    @Builder.Default
    private final int maxVerticesPerDraw = 100000;
    
    /** Enable anisotropic filtering */
    @Builder.Default
    private final boolean anisotropicFiltering = true;
    
    /** Maximum anisotropy level (1-16) */
    @Min(1)
    @Max(16)
    @Builder.Default
    private final int maxAnisotropy = 16;
    
    /** Shadow map resolution */
    @Min(256)
    @Max(8192)
    @Builder.Default
    private final int shadowMapResolution = 1024;
    
    /** Number of shadow maps (cascades) */
    @Min(1)
    @Max(8)
    @Builder.Default
    private final int shadowMapCascades = 4;
    
    /** Enable bloom post-processing effect */
    @Builder.Default
    private final boolean bloomEnabled = false;
    
    /** Bloom threshold */
    @Min(0.0f)
    @Max(2.0f)
    @Builder.Default
    private final float bloomThreshold = 1.0f;
    
    /** Bloom intensity */
    @Min(0.0f)
    @Max(2.0f)
    @Builder.Default
    private final float bloomIntensity = 0.5f;
    
    /** Enable gamma correction */
    @Builder.Default
    private final boolean gammaCorrection = true;
    
    /** Gamma value for gamma correction */
    @Min(1.0f)
    @Max(3.0f)
    @Builder.Default
    private final float gamma = 2.2f;
    
    /** Clear color (red, green, blue, alpha) */
    @NotNull
    @Builder.Default
    private final float[] clearColor = {0.1f, 0.1f, 0.15f, 1.0f};
    
    /** Clear depth value */
    @Min(0.0f)
    @Max(1.0f)
    @Builder.Default
    private final float clearDepth = 1.0f;

    /**
     * Creates a default render configuration.
     * 
     * @return default configuration
     */
    public static RenderConfig defaultConfig() {
        return RenderConfig.builder()
                .targetVersion(OpenGLVersion.GL_3_3)
                .debugMode(false)
                .errorChecking(true)
                .vsyncEnabled(true)
                .multiSamplingLevel(4)
                .targetFrameRate(60)
                .autoMipmapGeneration(true)
                .textureCompression(true)
                .maxTextureSize(4096)
                .maxTextureUnits(32)
                .maxVerticesPerDraw(100000)
                .anisotropicFiltering(true)
                .maxAnisotropy(16)
                .shadowMapResolution(1024)
                .shadowMapCascades(4)
                .bloomEnabled(false)
                .bloomThreshold(1.0f)
                .bloomIntensity(0.5f)
                .gammaCorrection(true)
                .gamma(2.2f)
                .clearColor(new float[]{0.1f, 0.1f, 0.15f, 1.0f})
                .clearDepth(1.0f)
                .build();
    }

    /**
     * Creates a debug render configuration.
     * 
     * @return debug configuration with extra validation and logging
     */
    public static RenderConfig debugConfig() {
        return RenderConfig.builder()
                .targetVersion(OpenGLVersion.GL_3_3)
                .debugMode(true)
                .errorChecking(true)
                .vsyncEnabled(false) // Disable for debugging
                .multiSamplingLevel(0) // No AA for clarity
                .targetFrameRate(30) // Slower for debugging
                .autoMipmapGeneration(true)
                .textureCompression(false) // Easier debugging
                .maxTextureSize(2048) // Smaller for faster loading
                .maxTextureUnits(16) // Reduced for testing
                .maxVerticesPerDraw(10000) // Smaller batches
                .anisotropicFiltering(false)
                .maxAnisotropy(1)
                .shadowMapResolution(512) // Lower resolution
                .shadowMapCascades(2)
                .bloomEnabled(false)
                .gammaCorrection(true)
                .gamma(2.2f)
                .clearColor(new float[]{0.2f, 0.2f, 0.3f, 1.0f}) // Different color for visibility
                .clearDepth(1.0f)
                .build();
    }

    /**
     * Creates a performance-optimized render configuration.
     * 
     * @return performance configuration with optimizations enabled
     */
    public static RenderConfig performanceConfig() {
        return RenderConfig.builder()
                .targetVersion(OpenGLVersion.GL_3_3)
                .debugMode(false)
                .errorChecking(false) // Disable in production
                .vsyncEnabled(false) // Uncapped framerate
                .multiSamplingLevel(8) // Higher quality AA
                .targetFrameRate(0) // Uncapped
                .autoMipmapGeneration(true)
                .textureCompression(true) // Better memory usage
                .maxTextureSize(8192) // Higher resolution support
                .maxTextureUnits(64) // More texture units
                .maxVerticesPerDraw(1000000) // Large batches
                .anisotropicFiltering(true)
                .maxAnisotropy(16)
                .shadowMapResolution(4096) // High quality shadows
                .shadowMapCascades(8)
                .bloomEnabled(true)
                .bloomThreshold(0.8f)
                .bloomIntensity(0.6f)
                .gammaCorrection(true)
                .gamma(2.2f)
                .clearColor(new float[]{0.05f, 0.05f, 0.08f, 1.0f}) // Dark background
                .clearDepth(1.0f)
                .build();
    }
}

/**
 * OpenGL version enum for configuration.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
public enum OpenGLVersion {
    GL_3_0(3, 0, "OpenGL 3.0"),
    GL_3_1(3, 1, "OpenGL 3.1"),
    GL_3_2(3, 2, "OpenGL 3.2"),
    GL_3_3(3, 3, "OpenGL 3.3 Core Profile"),
    GL_4_0(4, 0, "OpenGL 4.0"),
    GL_4_1(4, 1, "OpenGL 4.1"),
    GL_4_2(4, 2, "OpenGL 4.2"),
    GL_4_3(4, 3, "OpenGL 4.3"),
    GL_4_4(4, 4, "OpenGL 4.4"),
    GL_4_5(4, 5, "OpenGL 4.5"),
    GL_4_6(4, 6, "OpenGL 4.6");

    private final int majorVersion;
    private final int minorVersion;
    private final String displayName;

    OpenGLVersion(int major, int minor, String displayName) {
        this.majorVersion = major;
        this.minorVersion = minor;
        this.displayName = displayName;
    }

    /**
     * Gets the major version number.
     * 
     * @return the major version
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Gets the minor version number.
     * 
     * @return the minor version
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Gets the GLSL shader version for this OpenGL version.
     * 
     * @return the GLSL version string
     */
    public String getGLSLVersion() {
        if (majorVersion == 3) {
            return "330"; // OpenGL 3.3 uses GLSL 3.30
        } else if (majorVersion == 4) {
            return String.format("4%02d", minorVersion * 10); // OpenGL 4.x uses GLSL 4.x0
        }
        return "330"; // Default to GLSL 3.30
    }

    /**
     * Checks if this version supports a specific feature.
     * 
     * @param feature the feature to check
     * @return true if the version supports the feature
     */
    public boolean supportsFeature(OpenGLFeature feature) {
        return switch (feature) {
            case VAO -> majorVersion >= 3;
            case VBO -> majorVersion >= 3;
            case FBO -> true; // Supported since OpenGL 3.0
            case TESSELATION_SHADERS -> majorVersion >= 4;
            case GEOMETRY_SHADERS -> majorVersion >= 3;
            case COMPUTE_SHADERS -> majorVersion >= 4;
            case DIRECT_STATE_ACCESS -> majorVersion >= 4;
            case TEXTURE_BUFFER_OBJECTS -> majorVersion >= 3;
            case MULTI_DRAW_INDIRECT -> majorVersion >= 4;
            case VERTEX_ATTRIB_DIVISOR -> majorVersion >= 3;
            case TEXTURE_STORAGE -> majorVersion >= 4;
            case SHADER_STORAGE_BUFFER -> majorVersion >= 4;
            case ATOMIC_COUNTERS -> majorVersion >= 4;
        };
    }

    /**
     * Gets the display name for this version.
     * 
     * @return the display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}

/**
 * OpenGL features that can be checked for version support.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
public enum OpenGLFeature {
    /** Vertex Array Objects (VAO) */
    VAO,
    
    /** Vertex Buffer Objects (VBO) */
    VBO,
    
    /** Frame Buffer Objects (FBO) */
    FBO,
    
    /** Tessellation shaders */
    TESSELATION_SHADERS,
    
    /** Geometry shaders */
    GEOMETRY_SHADERS,
    
    /** Compute shaders */
    COMPUTE_SHADERS,
    
    /** Direct State Access (DSA) */
    DIRECT_STATE_ACCESS,
    
    /** Texture Buffer Objects (TBO) */
    TEXTURE_BUFFER_OBJECTS,
    
    /** Multi-Draw Indirect */
    MULTI_DRAW_INDIRECT,
    
    /** Vertex attribute divisors */
    VERTEX_ATTRIB_DIVISOR,
    
    /** Immutable texture storage */
    TEXTURE_STORAGE,
    
    /** Shader Storage Buffer Objects */
    SHADER_STORAGE_BUFFER,
    
    /** Atomic counters */
    ATOMIC_COUNTERS;
}
