package orb.atomic.project.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import orb.atomic.project.http.context.Route;
import orb.atomic.project.util.UrlMatcher;

import java.lang.reflect.Method;

@Getter
@ToString
@RequiredArgsConstructor
public final class ObjectRoute {

    private final Object instance;
    private final UrlMatcher urlMatcher;
    private final Method method;
    private final Route route;

}
