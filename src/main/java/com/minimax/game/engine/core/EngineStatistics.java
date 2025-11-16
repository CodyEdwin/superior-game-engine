package com.minimax.game.engine.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Tracks runtime performance and usage statistics for the GameEngine.
 * 
 * This class provides real-time monitoring of engine performance, including
 * frame rates, memory usage, system execution times, and other key metrics.
 * It uses a circular buffer approach for efficient performance monitoring.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
@Data
public class EngineStatistics {
    
    /** Maximum number of frame time samples to keep */
    private static final int MAX_FRAME_SAMPLES = 1000;
    
    /** Number of frames between performance logs */
    private static final int DEFAULT_LOG_INTERVAL = 60;
    
    /** Queue for recent frame times (in seconds) */
    private final Queue<Float> frameTimes = new ConcurrentLinkedQueue<>();
    
    /** Queue for recent FPS values */
    private final Queue<Float> fpsSamples = new ConcurrentLinkedQueue<>();
    
    /** Last performance log timestamp */
    private Instant lastLogTime = Instant.now();
    
    /** Number of frames since last log */
    private int framesSinceLog = 0;
    
    /** Total frames rendered since engine start */
    private long totalFrames = 0;
    
    /** Engine uptime */
    private Instant startTime = Instant.now();
    
    /** Peak FPS achieved */
    private float peakFPS = 0.0f;
    
    /** Lowest FPS achieved */
    private float minFPS = Float.MAX_VALUE;
    
    /** Total time spent in game loop */
    private Duration totalUptime = Duration.ZERO;
    
    /** Number of frames logged for performance analysis */
    @Builder.Default
    private final int logInterval = DEFAULT_LOG_INTERVAL;
    
    /**
     * Records a frame's timing information.
     * 
     * @param frameTime the time taken for this frame in seconds
     */
    public synchronized void recordFrame(float frameTime) {
        // Update basic counters
        totalFrames++;
        framesSinceLog++;
        
        // Calculate FPS
        float fps = frameTime > 0 ? 1.0f / frameTime : 0.0f;
        
        // Update peak/min FPS
        peakFPS = Math.max(peakFPS, fps);
        minFPS = Math.min(minFPS, fps);
        
        // Add to circular buffers
        frameTimes.offer(frameTime);
        fpsSamples.offer(fps);
        
        // Maintain buffer size
        while (frameTimes.size() > MAX_FRAME_SAMPLES) {
            frameTimes.poll();
        }
        while (fpsSamples.size() > MAX_FRAME_SAMPLES) {
            fpsSamples.poll();
        }
        
        // Update uptime
        totalUptime = Duration.between(startTime, Instant.now());
    }
    
    /**
     * Gets the current FPS based on recent samples.
     * 
     * @return the average FPS over recent frames
     */
    public float getCurrentFPS() {
        if (fpsSamples.isEmpty()) {
            return 0.0f;
        }
        
        float sum = 0.0f;
        int count = 0;
        for (float fps : fpsSamples) {
            sum += fps;
            count++;
        }
        return count > 0 ? sum / count : 0.0f;
    }
    
    /**
     * Gets the average frame time over recent samples.
     * 
     * @return the average frame time in seconds
     */
    public float getAverageFrameTime() {
        if (frameTimes.isEmpty()) {
            return 0.0f;
        }
        
        float sum = 0.0f;
        int count = 0;
        for (float frameTime : frameTimes) {
            sum += frameTime;
            count++;
        }
        return count > 0 ? sum / count : 0.0f;
    }
    
    /**
     * Gets the 95th percentile frame time for performance analysis.
     * 
     * @return the 95th percentile frame time in seconds
     */
    public float getP95FrameTime() {
        if (frameTimes.isEmpty()) {
            return 0.0f;
        }
        
        // Copy to array and sort
        Float[] times = frameTimes.toArray(new Float[0]);
        java.util.Arrays.sort(times);
        
        // Get 95th percentile
        int index = (int) Math.ceil(times.length * 0.95) - 1;
        return times[Math.max(0, index)];
    }
    
    /**
     * Gets the current memory usage estimates.
     * 
     * @return memory usage information
     */
    public MemoryUsage getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        return new MemoryUsage(
            usedMemory,
            freeMemory,
            totalMemory,
            maxMemory,
            (float) usedMemory / maxMemory * 100.0f
        );
    }
    
    /**
     * Checks if performance statistics should be logged now.
     * 
     * @return true if it's time to log performance
     */
    public boolean shouldLogPerformance() {
        if (framesSinceLog >= logInterval) {
            framesSinceLog = 0;
            lastLogTime = Instant.now();
            return true;
        }
        return false;
    }
    
    /**
     * Gets comprehensive performance report.
     * 
     * @return a formatted performance report
     */
    public String getPerformanceReport() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Engine Performance Report ===\n");
        sb.append("Uptime: ").append(formatDuration(totalUptime)).append("\n");
        sb.append("Total Frames: ").append(totalFrames).append("\n");
        sb.append("Current FPS: ").append(String.format("%.1f", getCurrentFPS())).append("\n");
        sb.append("Average FPS: ").append(String.format("%.1f", calculateAverageFPS())).append("\n");
        sb.append("Peak FPS: ").append(String.format("%.1f", peakFPS)).append("\n");
        sb.append("Min FPS: ").append(String.format("%.1f", minFPS == Float.MAX_VALUE ? 0.0f : minFPS)).append("\n");
        sb.append("Avg Frame Time: ").append(String.format("%.2f", getAverageFrameTime() * 1000)).append("ms\n");
        sb.append("P95 Frame Time: ").append(String.format("%.2f", getP95FrameTime() * 1000)).append("ms\n");
        
        MemoryUsage memory = getMemoryUsage();
        sb.append("Memory Usage: ").append(formatBytes(memory.usedMemory))
          .append(" / ").append(formatBytes(memory.maxMemory))
          .append(" (").append(String.format("%.1f", memory.usagePercentage)).append("%)\n");
        
        return sb.toString();
    }
    
    /**
     * Calculates the average FPS from all samples.
     * 
     * @return the average FPS
     */
    private float calculateAverageFPS() {
        if (fpsSamples.isEmpty()) {
            return 0.0f;
        }
        
        float sum = 0.0f;
        for (float fps : fpsSamples) {
            sum += fps;
        }
        return sum / fpsSamples.size();
    }
    
    /**
     * Formats a duration into a human-readable string.
     * 
     * @param duration the duration to format
     * @return formatted duration string
     */
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    /**
     * Formats bytes into a human-readable string.
     * 
     * @param bytes the number of bytes
     * @return formatted size string
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Resets all statistics (useful for performance testing).
     */
    public synchronized void reset() {
        frameTimes.clear();
        fpsSamples.clear();
        totalFrames = 0;
        framesSinceLog = 0;
        lastLogTime = Instant.now();
        peakFPS = 0.0f;
        minFPS = Float.MAX_VALUE;
        totalUptime = Duration.ZERO;
        startTime = Instant.now();
    }
    
    /**
     * Represents memory usage information.
     * 
     * @author MiniMax Agent
     * @version 1.0.0
     */
    public record MemoryUsage(
        /** Currently used memory in bytes */
        long usedMemory,
        /** Available memory in bytes */
        long freeMemory,
        /** Total allocated memory in bytes */
        long totalMemory,
        /** Maximum memory that can be allocated */
        long maxMemory,
        /** Percentage of maximum memory being used */
        float usagePercentage
    ) {}
}
