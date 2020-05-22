package orb.atomic.project;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import orb.atomic.project.factory.ProjectExecutor;
import orb.atomic.project.factory.ThreadFactoryProject;
import orb.atomic.project.manager.ClzTypeManager;
import orb.atomic.project.manager.HttpManager;
import orb.atomic.project.manager.RequestHandler;
import orb.atomic.project.util.ValidationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

@Getter
@RequiredArgsConstructor
public final class AtomicServer {

    private static final Logger ATOMIC_LOGGER = LoggerFactory.getLogger(AtomicServer.class);

    private final String workerName;
    private final int workers;
    private final InetSocketAddress address;

    private final ClzTypeManager typeManager = new ClzTypeManager();
    private final HttpManager configuration = new HttpManager(typeManager);

    private HttpHandler httpHandler;
    private ExecutorService service;
    private ThreadFactory factory;
    private HttpServer mainServer;

    public void registerType(Class<?> clazz, final ValidationRule<?> rule) {
        typeManager.registerType(clazz, rule);
    }

    public void registerRouter(Object... routers) {
        for (Object route : routers) {
            configuration.registerRouter(route);
        }
    }

    @SneakyThrows
    public final void bind() {
        httpHandler = new RequestHandler(configuration, typeManager);

        factory = ThreadFactoryProject.builder()
                .name(workerName)
                .build();

        service = new ProjectExecutor(
                factory, workers
        ).build();

        mainServer = HttpServer.create(address, 10);
        mainServer.setExecutor(service);
        final HttpContext context = mainServer.createContext("/", httpHandler);

        mainServer.start();
        ATOMIC_LOGGER.info("Server is starting at port " + address.getPort());
    }
}
