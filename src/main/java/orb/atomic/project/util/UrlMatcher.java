package orb.atomic.project.util;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public final class UrlMatcher {

    private static final Pattern VARIABLES_PATTERN = Pattern.compile("\\:(\\w+)");
    private static final Map<String, String> EMPTY_MAP = new WeakHashMap<>();

    private final String urlFilter;
    private final Pattern pattern;
    private final List<String> variables;

    public final Map<String, String> match(final String url) {
        final Matcher matcher = pattern.matcher(url);

        if (!matcher.matches()) return null;
        if (variables.isEmpty()) return EMPTY_MAP;

        final HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < matcher.groupCount(); i++) {
            final String key = variables.get(i);
            final String value = matcher.group(i + 1);
            hashMap.put(key, value);
        }

        return hashMap;
    }

    public static UrlMatcher createMatcher(String filterUrl) {
        final Matcher matcher = VARIABLES_PATTERN.matcher(filterUrl);

        final StringBuffer stringBuffer = new StringBuffer();
        final List<String> variables = new ArrayList<>();

        while (matcher.find()) {
            variables.add(matcher.group(1));
            matcher.appendReplacement(stringBuffer, "([^/]+)");
        }

        matcher.appendTail(stringBuffer);
        final Pattern compile = Pattern.compile(stringBuffer.toString());

        return new UrlMatcher(filterUrl, compile, variables);
    }
}
