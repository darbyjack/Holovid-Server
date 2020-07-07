package io.github.holovid.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public final class StopCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);

    @ShellMethod("stop")
    public void stop() {
        LOGGER.info("Goodbye!");
        System.exit(0);
    }
}
