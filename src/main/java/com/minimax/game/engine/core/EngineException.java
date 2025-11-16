package com.minimax.game.engine.core;

/**
 * Exception thrown when the GameEngine encounters an unrecoverable error.
 * 
 * This exception wraps underlying exceptions and provides context about
 * which engine subsystem failed, enabling proper error handling and
 * graceful degradation.
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
public class EngineException extends Exception {
    
    /** The engine subsystem that encountered the error */
    private final String subsystem;
    
    /** Additional context about the error */
    private final String context;
    
    /** Error code for programmatic handling */
    private final ErrorCode errorCode;

    /**
     * Creates a new EngineException with a message.
     * 
     * @param message the error message
     */
    public EngineException(String message) {
        this(message, null, null, null);
    }

    /**
     * Creates a new EngineException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public EngineException(String message, Throwable cause) {
        this(message, cause, null, null);
    }

    /**
     * Creates a new EngineException with subsystem and message.
     * 
     * @param subsystem the subsystem that failed
     * @param message the error message
     */
    public EngineException(String subsystem, String message) {
        this(message, null, subsystem, null);
    }

    /**
     * Creates a new EngineException with full context.
     * 
     * @param message the error message
     * @param cause the underlying cause
     * @param subsystem the subsystem that failed
     * @param context additional context information
     */
    public EngineException(String message, Throwable cause, String subsystem, String context) {
        this(message, cause, subsystem, context, null);
    }

    /**
     * Creates a new EngineException with full context and error code.
     * 
     * @param message the error message
     * @param cause the underlying cause
     * @param subsystem the subsystem that failed
     * @param context additional context information
     * @param errorCode the error code for programmatic handling
     */
    public EngineException(String message, Throwable cause, String subsystem, String context, ErrorCode errorCode) {
        super(message, cause);
        this.subsystem = subsystem;
        this.context = context;
        this.errorCode = errorCode;
    }

    /**
     * Gets the subsystem that encountered the error.
     * 
     * @return the subsystem name, or null if not specified
     */
    public String getSubsystem() {
        return subsystem;
    }

    /**
     * Gets additional context about the error.
     * 
     * @return the context information, or null if not specified
     */
    public String getContext() {
        return context;
    }

    /**
     * Gets the error code.
     * 
     * @return the error code, or null if not specified
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Gets a formatted error message with all available context.
     * 
     * @return a detailed error message
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (subsystem != null) {
            sb.append("[").append(subsystem).append("] ");
        }
        
        sb.append(getMessage());
        
        if (context != null) {
            sb.append(" (Context: ").append(context).append(")");
        }
        
        if (errorCode != null) {
            sb.append(" (Error Code: ").append(errorCode).append(")");
        }
        
        return sb.toString();
    }

    /**
     * Error codes for programmatic error handling.
     * 
     * @author MiniMax Agent
     * @version 1.0.0
     */
    public enum ErrorCode {
        /** Initialization failed */
        INITIALIZATION_FAILED,
        
        /** OpenGL context creation failed */
        OPENGL_CONTEXT_FAILED,
        
        /** Asset loading failed */
        ASSET_LOAD_FAILED,
        
        /** Audio system initialization failed */
        AUDIO_INIT_FAILED,
        
        /** Input system initialization failed */
        INPUT_INIT_FAILED,
        
        /** Rendering system error */
        RENDER_FAILED,
        
        /** Memory allocation failed */
        MEMORY_ALLOCATION_FAILED,
        
        /** Thread execution failed */
        THREAD_EXECUTION_FAILED,
        
        /** Configuration error */
        CONFIG_INVALID,
        
        /** Native library loading failed */
        NATIVE_LIBRARY_FAILED,
        
        /** Graphics driver incompatibility */
        DRIVER_INCOMPATIBLE,
        
        /** Resource cleanup failed */
        CLEANUP_FAILED,
        
        /** System dependency missing */
        DEPENDENCY_MISSING,
        
        /** Unknown error */
        UNKNOWN;
        
        /**
         * Creates an EngineException for this error code.
         * 
         * @param message the error message
         * @param cause the underlying cause
         * @return a new EngineException with this error code
         */
        public EngineException with(String message, Throwable cause) {
            return new EngineException(message, cause, null, null, this);
        }
        
        /**
         * Creates an EngineException for this error code with subsystem.
         * 
         * @param subsystem the subsystem that failed
         * @param message the error message
         * @param cause the underlying cause
         * @return a new EngineException with this error code and subsystem
         */
        public EngineException withSubsystem(String subsystem, String message, Throwable cause) {
            return new EngineException(message, cause, subsystem, null, this);
        }
        
        /**
         * Creates an EngineException for this error code with full context.
         * 
         * @param subsystem the subsystem that failed
         * @param message the error message
         * @param context additional context
         * @param cause the underlying cause
         * @return a new EngineException with full context
         */
        public EngineException withContext(String subsystem, String message, String context, Throwable cause) {
            return new EngineException(message, cause, subsystem, context, this);
        }
    }
}
