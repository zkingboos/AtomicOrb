package orb.atomic.project.http.context;

import orb.atomic.project.entity.Module;

public @interface Middleware {
    Class<? extends Module>[] value();
}
