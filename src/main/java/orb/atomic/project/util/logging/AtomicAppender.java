package orb.atomic.project.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.joran.spi.ConsoleTarget;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;

public final class AtomicAppender extends OutputStreamAppender<ILoggingEvent> {

    public AtomicAppender() {
        setEncoder(new AtomicEncoder());
    }

    @Override
    public void start() {
        final PrintStream ps = new PrintStream(ConsoleTarget.SystemOut.getStream());
        setOutputStream(AnsiConsole.wrapSystemOut(ps));
        super.start();
    }
}
