package com.db.games.command;

import com.db.games.domain.Platform;
import com.db.games.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
public class GamesDbAppCommands {

    @Autowired
    private PlatformService platformService;

    @ShellMethod(value = "Get all game platforms", key = {"all-platforms", "ap"})
    public String getAllPlatforms() {
        return platformService.getAllPlatforms().stream().map(Platform::toString).collect(Collectors.joining("\n"));
    }
}
