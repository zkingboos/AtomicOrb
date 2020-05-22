package org.atomic.project.boot;

import orb.atomic.project.AtomicServer;
import org.atomic.project.UserRoute;

import java.net.InetSocketAddress;

public final class Launcher {

    private final static String FACTORY_NAME = "Worker #%d";

    public static void main(String[] args) {
        final AtomicServer atomic = new AtomicServer(
                FACTORY_NAME,
                4,
                new InetSocketAddress(80)
        );

        atomic.registerType(Boolean.class, Boolean::parseBoolean);
        atomic.registerType(Integer.class, Integer::parseInt);
        atomic.registerType(Long.class, Long::parseLong);
        atomic.registerType(String.class, (key) -> key);

        atomic.registerRouter(new UserRoute());

        atomic.bind();
    }
}
