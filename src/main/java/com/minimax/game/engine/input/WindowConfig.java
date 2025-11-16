package com.minimax.game.engine.input;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for window and input handling.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Data
@Builder
public final class WindowConfig {
    
    @Builder.Default
    private final String title = "Superior Game Engine";
    
    @Builder.Default
    private final int width = 800;
    
    @Builder.Default
    private final int height = 600;
    
    @Builder.Default
    private final boolean resizable = true;
    
    @Builder.Default
    private final boolean vsyncEnabled = true;
    
    @Builder.Default
    private final boolean fullscreen = false;
    
    @Builder.Default
    private final boolean decorated = true;
    
    public static WindowConfig defaultConfig() {
        return WindowConfig.builder()
                .title("Superior Game Engine")
                .width(800)
                .height(600)
                .resizable(true)
                .vsyncEnabled(true)
                .fullscreen(false)
                .decorated(true)
                .build();
    }
}
