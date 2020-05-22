package orb.atomic.project.http;

import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.OutputStream;

@RequiredArgsConstructor
public final class OrbContext extends HttpExchange {

    @Delegate
    private final HttpExchange exchange;

    @SneakyThrows
    public final void message(final String contentMessage, final int status) {
        exchange.sendResponseHeaders(status, contentMessage.length());
        final OutputStream responseBody = exchange.getResponseBody();

        responseBody.write(contentMessage.getBytes());
        responseBody.close();
    }

    public final void errorMessage(final String errorMessage) {
        message("<h1>ERROR: " + errorMessage + "</h1>", 400);
    }

    public final void message(final String contentMessage) {
        message(contentMessage, 200);
    }
}
