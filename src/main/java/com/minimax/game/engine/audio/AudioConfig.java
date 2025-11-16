package com.minimax.game.engine.audio;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for the audio system.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Data
@Builder
public final class AudioConfig {
    
    @Builder.Default
    private final boolean enabled = true;
    
    @Builder.Default
    private final float masterVolume = 1.0f;
    
    @Builder.Default
    private final int sampleRate = 44100;
    
    @Builder.Default
    private final int bufferSize = 4096;
    
    public static AudioConfig defaultConfig() {
        return AudioConfig.builder()
                .enabled(true)
                .masterVolume(1.0f)
                .sampleRate(44100)
                .bufferSize(4096)
                .build();
    }
}
