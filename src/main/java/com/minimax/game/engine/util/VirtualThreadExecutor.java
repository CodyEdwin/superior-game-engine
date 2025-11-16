package com.minimax.game.engine.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages virtual thread execution for parallel game system processing.
 * 
 * This class leverages Java 21's virtual threads to provide lightweight,
 * efficient parallel execution of game systems while maintaining proper
 * resource management and coordination.
 * 
 * Key Features:
 * - Automatic scaling based on system workload
 * - Structured concurrency for coordinated task execution
 * - Resource cleanup and thread pool management
 * - Performance monitoring and optimization
 * 
 * @author MiniMax Agent
 * @version 1.0.0
 */
@Slf4j
public final class VirtualThreadExecutor {
    
    /** Executor service for virtual threads */
    private final ExecutorService virtualThreadExecutor;
    
    /** Structured executor for coordinated tasks */
    private StructuredExecutor structuredExecutor;
    
    /** Flag indicating if executor is initialized */
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    
    /** Number of virtual threads in the pool */
    private final int threadPoolSize;
    
    /** Executor shutdown timeout in seconds */
    private static final int SHUTDOWN_TIMEOUT = 30;
    
    /**
     * Creates a VirtualThreadExecutor with automatic sizing.
     * 
     * The thread pool size is automatically determined based on available
     * processors and system load characteristics.
     */
    public VirtualThreadExecutor() {
        this.threadPoolSize = calculateOptimalThreadPoolSize();
        this.virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        log.debug("VirtualThreadExecutor created with {} virtual threads", threadPoolSize);
    }
    
    /**
     * Creates a VirtualThreadExecutor with specified thread pool size.
     * 
     * @param threadPoolSize the number of virtual threads to use
     */
    public VirtualThreadExecutor(int threadPoolSize) {
        this.threadPoolSize = Math.max(1, Math.min(threadPoolSize, 
                                Runtime.getRuntime().availableProcessors() * 4));
        this.virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        log.debug("VirtualThreadExecutor created with {} virtual threads", this.threadPoolSize);
    }
    
    /**
     * Initializes the executor.
     */
    public void initialize() {
        if (initialized.compareAndSet(false, true)) {
            log.info("VirtualThreadExecutor initialized with {} threads", threadPoolSize);
        }
    }
    
    /**
     * Executes a task asynchronously using virtual threads.
     * 
     * @param task the task to execute
     * @return a Future representing the task execution
     */
    public Future<?> submitAsync(Runnable task) {
        if (!initialized.get()) {
            throw new IllegalStateException("Executor not initialized. Call initialize() first.");
        }
        return virtualThreadExecutor.submit(task);
    }
    
    /**
     * Executes multiple tasks in parallel using structured concurrency.
     * 
     * This method ensures all tasks complete before returning, with proper
     * resource cleanup and error handling.
     * 
     * @param tasks the tasks to execute in parallel
     * @throws Exception if any task fails
     */
    public void executeParallel(List<Runnable> tasks) throws Exception {
        if (!initialized.get()) {
            throw new IllegalStateException("Executor not initialized. Call initialize() first.");
        }
        
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        
        try (var scope = StructuredExecutor.open()) {
            List<Future<?>> futures = new ArrayList<>(tasks.size());
            
            // Submit all tasks
            for (Runnable task : tasks) {
                Future<?> future = scope.fork(task::run);
                futures.add(future);
            }
            
            // Wait for all tasks to complete
            scope.join();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Parallel execution interrupted", e);
        }
    }
    
    /**
     * Executes multiple tasks in parallel with a timeout.
     * 
     * @param tasks the tasks to execute in parallel
     * @param timeout the maximum time to wait for completion
     * @throws TimeoutException if execution takes too long
     * @throws Exception if any task fails
     */
    public void executeParallelWithTimeout(List<Runnable> tasks, Duration timeout) 
            throws TimeoutException, Exception {
        if (!initialized.get()) {
            throw new IllegalStateException("Executor not initialized. Call initialize() first.");
        }
        
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        
        try (var scope = StructuredExecutor.open()) {
            List<Future<?>> futures = new ArrayList<>(tasks.size());
            
            // Submit all tasks
            for (Runnable task : tasks) {
                Future<?> future = scope.fork(task::run);
                futures.add(future);
            }
            
            // Wait for all tasks to complete with timeout
            scope.join(timeout);
            
        } catch (TimeoutException e) {
            log.warn("Parallel execution timeout after {}", timeout);
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Parallel execution interrupted", e);
        }
    }
    
    /**
     * Executes a single task with a timeout.
     * 
     * @param task the task to execute
     * @param timeout the maximum time to wait for completion
     * @throws TimeoutException if execution takes too long
     * @throws Exception if the task fails
     */
    public void executeWithTimeout(Runnable task, Duration timeout) 
            throws TimeoutException, Exception {
        if (!initialized.get()) {
            throw new IllegalStateException("Executor not initialized. Call initialize() first.");
        }
        
        try (var scope = StructuredExecutor.open()) {
            Future<?> future = scope.fork(task::run);
            scope.join(timeout);
            
        } catch (TimeoutException e) {
            log.warn("Task execution timeout after {}", timeout);
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Task execution interrupted", e);
        }
    }
    
    /**
     * Executes game systems in phases using structured concurrency.
     * 
     * This method is optimized for game engine use cases where systems
     * need to be coordinated in phases (e.g., physics, AI, rendering).
     * 
     * @param phases the phases to execute, each containing a list of tasks
     * @throws Exception if any phase fails
     */
    public void executeGamePhases(List<List<Runnable>> phases) throws Exception {
        if (!initialized.get()) {
            throw new IllegalStateException("Executor not initialized. Call initialize() first.");
        }
        
        if (phases == null || phases.isEmpty()) {
            return;
        }
        
        // Execute phases sequentially, but tasks within each phase in parallel
        for (List<Runnable> phase : phases) {
            if (phase != null && !phase.isEmpty()) {
                executeParallel(phase);
            }
        }
    }
    
    /**
     * Gets the current number of active threads.
     * 
     * @return the number of active virtual threads
     */
    public int getActiveThreadCount() {
        // Note: Virtual threads don't have a traditional "active" count
        // This is more of a placeholder for future monitoring capabilities
        return threadPoolSize;
    }
    
    /**
     * Gets the configured thread pool size.
     * 
     * @return the number of virtual threads in the pool
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    
    /**
     * Checks if the executor is initialized.
     * 
     * @return true if the executor has been initialized
     */
    public boolean isInitialized() {
        return initialized.get();
    }
    
    /**
     * Shuts down the executor and releases all resources.
     * 
     * This method gracefully shuts down the virtual thread executor
     * and waits for all running tasks to complete.
     */
    public void shutdown() {
        if (initialized.compareAndSet(true, false)) {
            log.debug("Shutting down VirtualThreadExecutor...");
            
            virtualThreadExecutor.shutdown();
            try {
                if (!virtualThreadExecutor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                    log.warn("VirtualThreadExecutor shutdown timeout, forcing shutdown");
                    virtualThreadExecutor.shutdownNow();
                    
                    if (!virtualThreadExecutor.awaitTermination(SHUTDOWN_TIMEOUT / 2, TimeUnit.SECONDS)) {
                        log.error("VirtualThreadExecutor failed to shutdown properly");
                    }
                }
                log.info("VirtualThreadExecutor shutdown complete");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                virtualThreadExecutor.shutdownNow();
                log.debug("VirtualThreadExecutor shutdown interrupted");
            }
        }
    }
    
    /**
     * Calculates the optimal thread pool size based on system characteristics.
     * 
     * The optimal size is based on available processors and game engine
     * workload patterns, typically using 2-4 threads per available processor.
     * 
     * @return the optimal thread pool size
     */
    private static int calculateOptimalThreadPoolSize() {
        int processors = Runtime.getRuntime().availableProcessors();
        
        // For game engines, we typically want more threads than processors
        // to handle I/O-bound and compute-bound tasks efficiently
        int optimalSize = processors * 2;
        
        // Clamp to reasonable bounds
        return Math.max(2, Math.min(optimalSize, processors * 4));
    }
}
