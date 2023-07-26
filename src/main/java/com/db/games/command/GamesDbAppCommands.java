package com.db.games.command;

import com.db.games.domain.Game;
import com.db.games.domain.Platform;
import com.db.games.exception.GamesAPIException;
import com.db.games.repsitory.GameRepository;
import com.db.games.repsitory.PlatformRepository;
import com.db.games.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ShellComponent
public class GamesDbAppCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(GamesDbAppCommands.class);

    @Autowired
    private PlatformService platformService;

    private Platform selectedPlatform;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private GameRepository gameRepository;

    @ShellMethod(value = "Get all game platforms", key = {"all-platforms", "ap"})
    public String getAllPlatforms() {
        return platformService.getAllPlatforms().stream().map(Platform::toString).collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = "Change platform", key = {"change-platform", "cp"})
    public String changePlatform(@ShellOption(help = "Platform ID or name") String platformIdOrName) {
        try {
            Platform platform = null;
            try {
                Long platformId = Long.parseLong(platformIdOrName);
                platform = platformRepository.findById(platformId).orElse(null);
            } catch (NumberFormatException ignored) {
                platform = platformRepository.findByName(platformIdOrName).orElse(null);
            }

            if (platform != null) {
                if (platformRepository.existsById(platform.getId())) {
                    selectedPlatform = platform;
                    return "Selected platform: " + selectedPlatform.getName();
                } else {
                    return "Platform exists in the database but is not available for selection.";
                }
            } else {
                List<Object[]> platformIdsAndNames = platformRepository.findAllPlatformIdsAndNames();
                List<String> platformNamesInDatabase = new ArrayList<>();
                for (Object[] data : platformIdsAndNames) {
                    String platformName = (String) data[1];
                    platformNamesInDatabase.add(platformName);
                }
                return "Platform not found in the database.\nPlatform names in the database: " + platformNamesInDatabase;
            }
        } catch (GamesAPIException e) {
            return e.getMessage();
        }
    }

    @ShellMethod(value = "Show games for the selected platform", key = {"show-games", "sg"})
    public String getGamesForPlatform(@ShellOption(defaultValue = "1") int pageNumber,
                                      @ShellOption(defaultValue = "20") int pageSize,
                                      String platformIdOrName) {
        if (pageNumber < 1) {
            return "Invalid page number. Page number must be greater than or equal to 1.";
        }
        if (pageSize < 1) {
            return "Invalid page size. Page size must be greater than or equal to 1.";
        }
        try {
            Platform selectedPlatform;
            try {
                Long platformId = Long.parseLong(platformIdOrName);
                selectedPlatform = platformService.getPlatformById(platformId);
            } catch (NumberFormatException ex) {
                selectedPlatform = platformService.getPlatformByName(platformIdOrName);
            }

            if (selectedPlatform == null) {
                return "Platform not found";
            }

            List<Game> games = platformService.getGamesForPlatformWithPagination(selectedPlatform.getId(), pageNumber, pageSize);

            StringBuilder output = new StringBuilder();
            output.append("Games for platform: ").append(selectedPlatform.getName()).append("\n");

            for (int i = 0; i < games.size(); i++) {
                Game game = games.get(i);
                output.append((i + 1) + ". " + game.getName()).append("\n");
            }
            int totalGames = games.size();
            int totalPages = (totalGames + pageSize - 1) / pageSize;
            output.append("\nPage ").append(pageNumber).append(" of ").append(totalPages).append("\n");
            output.append("Use 'game-details <game_number>' to view details of a specific game.\n");

            return output.toString();
        } catch (GamesAPIException e) {
            return "Error occurred while fetching games for the selected platform: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error occurred: " + e.getMessage();
        }
    }

    @ShellMethod(value = "View details of a specific game", key = {"game-details", "gd"})
    public String viewGameDetails(@ShellOption(help = "Game number (index) from the list") int gameNumber) {
        if (selectedPlatform == null) {
            return "No platform selected. Please select a platform first.";
        }

        List<Game> games = platformService.getGamesForPlatformWithPagination(selectedPlatform.getId(), 1, 20);

        if (gameNumber < 1 || gameNumber > games.size()) {
            return "Invalid game number. Please select a valid game number from the list.";
        }

        Game selectedGame = games.get(gameNumber - 1);

        if (selectedGame.getDescription() == null) {
            try {
                Game detailedGameInfo = platformService.getGameDetailsBySlug(selectedGame.getSlug());
                selectedGame.setDescription(detailedGameInfo.getDescription());
                gameRepository.save(selectedGame);
            } catch (GamesAPIException e) {
                return "Error occurred while fetching game details: " + e.getMessage();
            }
        }
        return "Details for game " + selectedGame.getName() + ":\n"
                + "Release Date: " + selectedGame.getReleaseDate() + "\n"
                + "Description: " + selectedGame.getDescription() + "\n"
                + "------------------------------------------";
    }

    @ShellMethod(value = "Reset stored data", key = {"reset-data", "rd"})
    public String resetStoredData() {
        selectedPlatform = null;
        return "Data reset completed.";
    }

    @ShellMethod(value = "Manually refresh platform data from API", key = {"refresh-data", "rd"})
    public String refreshPlatformDataFromApi() {
        platformService.refreshPlatformData();
        return "Platform data refreshed from API.";
    }

}