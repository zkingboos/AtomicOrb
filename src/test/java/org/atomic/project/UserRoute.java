package org.atomic.project;

import orb.atomic.project.entity.depency.Injection;
import orb.atomic.project.entity.middleware.AuthStrategy;
import orb.atomic.project.entity.middleware.Render;
import orb.atomic.project.http.OrbContext;
import orb.atomic.project.http.context.Middleware;
import orb.atomic.project.http.context.Route;
import orb.atomic.project.http.context.Router;

@Router("users/dashboard")
@Middleware({AuthStrategy.class})
public final class UserRoute {

    @Injection //soon
    private Render render;

    @Route("view/edit")
    public void viewKing(OrbContext context, Integer id) {
        context.message("What's up, your id is " + id);
    }

    @Route
    public void viewUser(OrbContext context) {
        context.message("Welcome to user index page");
    }
}
