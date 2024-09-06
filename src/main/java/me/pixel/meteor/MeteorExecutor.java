/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package me.pixel.meteor;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MeteorExecutor {
    public static ExecutorService executor;

    private MeteorExecutor() {
    }

    public static void init() {
        AtomicInteger threadNumber = new AtomicInteger(1);

        executor = Executors.newCachedThreadPool((task) -> {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.setName("Pixel-Executor-" + threadNumber.getAndIncrement());
            return thread;
        });
    }

    public static void execute(Runnable task) {
        executor.execute(task);
    }
}