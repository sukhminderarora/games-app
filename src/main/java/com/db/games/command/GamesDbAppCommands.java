package com.db.games.command;

import com.db.games.domain.Platform;
import com.db.games.repsitory.GameRepository;
import com.db.games.repsitory.PlatformRepository;
import com.db.games.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ShellComponent
public class GamesDbAppCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(GamesDbAppCommands.class);

    @Autowired
    private PlatformService platformService;

    private Platform selectedPlatform;

    @ShellMethod(value = "Get all game platforms", key = {"all-platforms", "ap"})
    public String getAllPlatforms() {
        return platformService.getAllPlatforms().stream().map(Platform::toString).collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = "Change platform", key = {"change-platform", "cp"})
    public String changePlatform(@ShellOption(help = "Platform ID or name") String platformIdOrName) {
        return platformService.changePlatform(platformIdOrName, selectedPlatform);
    }

    @ShellMethod(value = "Show games for the selected platform", key = {"show-games", "sg"})
    public String getGamesForPlatform(@ShellOption(defaultValue = "1") int pageNumber,
                                      @ShellOption(defaultValue = "20") int pageSize,
                                      String platformIdOrName) {
        return platformService.getGamesForPlatform(pageNumber, pageSize, platformIdOrName);
    }

    @ShellMethod(value = "View details of a specific game", key = {"game-details", "gd"})
    public String viewGameDetails(@ShellOption(help = "Game number (index) from the list") int gameNumber) {
        if (selectedPlatform == null) {
            return "No platform selected. Please select a platform first.";
        }
        return platformService.viewGameDetails(selectedPlatform, gameNumber);
    }

    @ShellMethod(value = "Reset stored data", key = {"reset-data", "rd"})
    public String resetStoredData() {
        selectedPlatform = null;
        return "Data reset completed.";
    }

    @ShellMethod(value = "Manually refresh platform data from API", key = {"refresh-data", "rfd"})
    public String refreshPlatformDataFromApi() {
        return platformService.refreshPlatformData();
    }

}