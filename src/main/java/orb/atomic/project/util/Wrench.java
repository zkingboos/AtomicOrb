package orb.atomic.project.util;

import lombok.Getter;

public final class Wrench {

    @Getter(lazy = true)
    private final static Wrench instance = new Wrench();

    public final String fix0IfNotPresent(final String full) {
        if (full.charAt(0) != '/') return "/".concat(full);
        return full;
    }

    public final String computeIfNotPresent(final String full) {
        return fixLastIfNotPresent(fix0IfNotPresent(full));
    }

    public final String fixLastIfNotPresent(final String full) {
        if (full.charAt(full.length() - 1) != '/') return full.concat("/");
        return full;
    }
}
