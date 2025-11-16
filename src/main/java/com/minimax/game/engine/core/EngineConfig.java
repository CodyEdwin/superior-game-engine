package com.minimax.game.engine.core;

import lombok.Builder;
import lombok.Data;
import com.minimax.game.engine.render.RenderConfig;
import com.minimax.game.engine.audio.AudioConfig;
import com.minimax.game.engine.input.WindowConfig;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Configuration class for the Superior Game Engine.
 * 
 * This class contains all the settings and parameters needed to configure
 * the engine at startup, including window settings, rendering options,
 * audio configuration, and performance targets.
 * 
 * Uses Lombok's @Builder for clean, type-safe configuration construction.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Data
@Builder
public final class EngineConfig {
    
    /** Window configuration settings */
    @NotNull
    private final WindowConfig windowConfig;
    
    /** Rendering system configuration */
    @NotNull
    private final RenderConfig renderConfig;
    
    /** Audio system configuration */
    @NotNull
    private final AudioConfig audioConfig;
    
    /** Target frames per second (0 = uncapped) */
    @Min(0)
    @Max(300)
    @Builder.Default
    private final int targetFrameRate = 60;
    
    /** Enable frame limiting to stay within target FPS */
    @Builder.Default
    private final boolean frameLimitEnabled = false;
    
    /** Enable virtual threads for parallel system execution */
    @Builder.Default
    private final boolean virtualThreadsEnabled = true;
    
    /** Maximum number of parallel systems to run concurrently */
    @Min(1)
    @Max(32)
    @Builder.Default
    private final int maxParallelSystems = 8;
    
    /** Enable engine statistics collection and logging */
    @Builder.Default
    private final boolean statisticsEnabled = true;
    
    /** Log performance statistics every N frames */
    @Min(1)
    @Max(1000)
    @Builder.Default
    private final int statisticsLogInterval = 60;
    
    /** Enable OpenGL debug output */
    @Builder.Default
    private final boolean debugMode = false;
    
    /** Enable verbose logging */
    @Builder.Default
    private final boolean verboseLogging = false;
    
    /** Working directory for asset loading */
    @Builder.Default
    private final String workingDirectory = "assets/";
    
    /** Maximum memory to use for asset caching (in MB) */
    @Min(16)
    @Max(2048)
    @Builder.Default
    private final int maxAssetCacheSize = 512;
    
    /** Auto-save engine state every N seconds (0 = disabled) */
    @Min(0)
    @Max(3600)
    @Builder.Default
    private final int autoSaveInterval = 0;
    
    /**
     * Creates a default engine configuration with sensible defaults.
     * 
     * @return a default configuration suitable for most games
     */
    public static EngineConfig defaultConfig() {
        return EngineConfig.builder()
                .windowConfig(WindowConfig.defaultConfig())
                .renderConfig(RenderConfig.defaultConfig())
                .audioConfig(AudioConfig.defaultConfig())
                .targetFrameRate(60)
                .frameLimitEnabled(false)
                .virtualThreadsEnabled(true)
                .maxParallelSystems(4)
                .statisticsEnabled(true)
                .statisticsLogInterval(60)
                .debugMode(false)
                .verboseLogging(false)
                .workingDirectory("assets/")
                .maxAssetCacheSize(256)
                .autoSaveInterval(0)
                .build();
    }
    
    /**
     * Creates a configuration optimized for development/debugging.
     * 
     * @return a configuration with extra debugging and logging enabled
     */
    public static EngineConfig debugConfig() {
        return EngineConfig.builder()
                .windowConfig(WindowConfig.defaultConfig())
                .renderConfig(RenderConfig.defaultConfig())
                .audioConfig(AudioConfig.defaultConfig())
                .targetFrameRate(30) // Slower for debugging
                .frameLimitEnabled(true)
                .virtualThreadsEnabled(true)
                .maxParallelSystems(2) // Sequential for debugging
                .statisticsEnabled(true)
                .statisticsLogInterval(30) // More frequent logging
                .debugMode(true)
                .verboseLogging(true)
                .workingDirectory("assets/")
                .maxAssetCacheSize(128) // Smaller for faster development
                .autoSaveInterval(300) // Auto-save every 5 minutes
                .build();
    }
    
    /**
     * Creates a configuration optimized for maximum performance.
     * 
     * @return a configuration with performance optimizations enabled
     */
    public static EngineConfig performanceConfig() {
        return EngineConfig.builder()
                .windowConfig(WindowConfig.defaultConfig())
                .renderConfig(RenderConfig.defaultConfig())
                .audioConfig(AudioConfig.defaultConfig())
                .targetFrameRate(0) // Uncapped
                .frameLimitEnabled(false)
                .virtualThreadsEnabled(true)
                .maxParallelSystems(16) // Maximum parallelism
                .statisticsEnabled(true)
                .statisticsLogInterval(300) // Less frequent logging
                .debugMode(false)
                .verboseLogging(false)
                .workingDirectory("assets/")
                .maxAssetCacheSize(1024) // Larger cache for better performance
                .autoSaveInterval(0)
                .build();
    }
}
