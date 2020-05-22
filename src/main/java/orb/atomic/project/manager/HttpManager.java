package orb.atomic.project.manager;

import lombok.RequiredArgsConstructor;
import orb.atomic.project.entity.ObjectRoute;
import orb.atomic.project.http.OrbContext;
import orb.atomic.project.http.context.Route;
import orb.atomic.project.http.context.Router;
import orb.atomic.project.util.UrlMatcher;
import orb.atomic.project.util.Wrench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class HttpManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpManager.class);
    private static final Wrench WRENCH = Wrench.getInstance();

    private static final Class<Router> ROUTER_CLASS = Router.class;
    private static final Class<Route> ROUTE_CLASS = Route.class;

    private final ClzTypeManager typeManager;

    private final Map<Router, List<ObjectRoute>> mappedRouter = new HashMap<>();

    public Object[] findRoute(final String httpMethod, final String urlPath) {
        for (Router router : mappedRouter.keySet()) {
            final List<ObjectRoute> routeList = filterRouteMethod(router, httpMethod);
            if (routeList == null) continue;

            for (ObjectRoute objectRoute : routeList) {
                final Map<String, String> match = objectRoute.getUrlMatcher().match(urlPath);
                if (match == null) continue;

                return new Object[]{objectRoute, match};
            }
        }
        return null;
    }

    public final List<ObjectRoute> filterRouteMethod(final Router router, final String httpMethod) {
        final List<ObjectRoute> routeList = mappedRouter.get(router);
        if (routeList == null) return null;

        final List<ObjectRoute> newRouteList = new ArrayList<>();
        for (ObjectRoute objectRoute : routeList) {
            if (httpMethod.equalsIgnoreCase(objectRoute.getRoute().method())) {
                newRouteList.add(objectRoute);
            }
        }

        return newRouteList;
    }

    public Object[] resolveTypeParameters(
            final OrbContext context,
            final ObjectRoute objectRoute,
            final Map<String, String> variables
    ) {

        final Method method = objectRoute.getMethod();
        final Object[] objects = new Object[method.getParameterCount()];

        objects[0] = context;

        if (variables.isEmpty()) return objects;

        final Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            final Parameter key = parameters[i];
            final String value = variables.get(key.getName());

            final Object typeSupplier = typeManager.supplierType(key.getType(), value);

            if (typeSupplier == null) {
                context.errorMessage("Type not recognized");
                return null;
            }

            objects[i] = typeSupplier;
        }
        return objects;
    }

    public void registerRouter(Object router) {
        final Class<?> routerClass = router.getClass();
        final Router routerAnnotation = routerClass.getAnnotation(ROUTER_CLASS);

        if (routerAnnotation == null) {
            LOGGER.error("O roteador precisa ter uma anotação na classe!");
            return;
        }

        final List<ObjectRoute> routeList = mappedRouter.computeIfAbsent(
                routerAnnotation, (key) -> new ArrayList<>()
        );

        for (Method method : routerClass.getDeclaredMethods()) {
            final Route routeAnnotation = method.getAnnotation(ROUTE_CLASS);
            if (routeAnnotation == null) continue;

            String fullPath = WRENCH.fix0IfNotPresent(
                    routerAnnotation.value()
            ).concat(WRENCH.computeIfNotPresent(routeAnnotation.value()));

            if (method.getParameterCount() > 1) {
                final StringBuilder builder = new StringBuilder(fullPath);

                final Parameter[] parameters = method.getParameters();
                for (int i = 1; i < parameters.length; i++) {
                    final Parameter parameter = parameters[i];
                    builder.append(":").append(parameter.getName()).append("/");
                }

                fullPath = builder.toString();
            }

            final UrlMatcher matcher = UrlMatcher.createMatcher(fullPath);
            final ObjectRoute objectRoute = new ObjectRoute(
                    router,
                    matcher,
                    method,
                    routeAnnotation
            );

            routeList.add(objectRoute);
        }

        mappedRouter.put(routerAnnotation, routeList);
    }
}
