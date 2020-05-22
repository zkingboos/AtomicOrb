package orb.atomic.project.manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import orb.atomic.project.entity.ObjectRoute;
import orb.atomic.project.http.OrbContext;
import orb.atomic.project.util.Wrench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RequiredArgsConstructor
public final class RequestHandler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private static final Wrench WRENCH = Wrench.getInstance();

    private final HttpManager manager;
    private final ClzTypeManager typeManager;

    @Override
    public void handle(HttpExchange exchange) {
        final OrbContext context = new OrbContext(exchange);

        final String unstablePath = exchange.getRequestURI().getPath();
        final String stablePath = WRENCH.computeIfNotPresent(unstablePath);
        final String requestMethod = exchange.getRequestMethod();

        final Object[] router = manager.findRoute(requestMethod, stablePath);

        if (router == null) {
            context.message("Route not found, verify the url and try again.");
            return;
        }

        LOGGER.trace("Incoming request at ".concat(stablePath));

        final ObjectRoute objectRoute = (ObjectRoute) router[0];
        final Map<String, String> variables = (Map) router[1];

        final Object[] objects = manager.resolveTypeParameters(context, objectRoute, variables);

        try {
            objectRoute
                    .getMethod()
                    .invoke(objectRoute.getInstance(), objects);
        } catch (Exception e) {
            context.errorMessage(e.getMessage());
        }
    }
}
