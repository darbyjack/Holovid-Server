package io.github.holovid.server.command;

import io.github.holovid.server.HolovidServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public final class StopCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);
    private final HolovidServerApplication server;

    public StopCommand(final HolovidServerApplication server) {
        this.server = server;
    }

    @ShellMethod("stop")
    public void stop() {
        //TODO stop tasks, clear data
        LOGGER.info("Goodbye!");
        System.exit(0);
    }
}
