package orb.atomic.project.factory;

import lombok.Builder;

import java.util.concurrent.ThreadFactory;

@Builder
public final class ThreadFactoryProject implements ThreadFactory {

    private int concurrent = 0;

    private final String name;
    private final boolean daemon;

    public Thread newThread(Runnable runnable) {
        final String formatName = String.format(name, concurrent++);

        final Thread thread = new Thread(runnable, formatName);
        thread.setDaemon(daemon);

        return thread;
    }
}
