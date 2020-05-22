package orb.atomic.project.factory;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@RequiredArgsConstructor
public final class ProjectExecutor {

    private final ThreadFactory factory;
    private final int workers;

    public ExecutorService build() {
        return Executors.newFixedThreadPool(workers, factory);
    }
}
