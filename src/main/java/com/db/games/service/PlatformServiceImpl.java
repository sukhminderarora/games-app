package com.db.games.service;

import com.db.games.domain.Game;
import java.util.Optional;
import com.db.games.domain.Platform;
import com.db.games.dto.PlatformDto;
import com.db.games.exception.GamesAPIException;
import com.db.games.repsitory.GameRepository;
import com.db.games.repsitory.PlatformRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlatformServiceImpl implements PlatformService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Value("${games.list.url}")
    private String gamesListApiUrl;

    @Value("${api.key}")
    private String apiKey;

    @Override
    public List<Platform> getAllPlatforms() {
        LOGGER.debug("Start of getAllPlatforms method ==> ");
        HttpHeaders headers = buildRequest();
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            String url = buildUrl(gamesListApiUrl);
            LOGGER.debug("Start of Rest Template Exchange with params {}  ==> ", url);
            ResponseEntity<PlatformDto> response = restTemplate.exchange(url, HttpMethod.GET, request,
                    new ParameterizedTypeReference<PlatformDto>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                PlatformDto resp = response.getBody();
                if (resp != null && CollectionUtils.isNotEmpty(resp.getResults())) {
                    LOGGER.debug("End of Rest Template Exchange and response received {}  with size ==> ", resp, resp.getResults().size());
                    return resp.getResults();
                } else {
                    LOGGER.debug("Empty or null RESPONSE Received from API: {}", resp);
                    return Collections.emptyList();
                }
            } else {
                LOGGER.error("API Response Error - Status Code: {}", response.getStatusCodeValue());
                throw new GamesAPIException("API Response Error - Status Code: " + response.getStatusCodeValue(),
                        "Exception Occurred While Getting Data from Service");
            }
        } catch (RestClientException e) {
            LOGGER.error("Exception occurred while calling the API: {}", e.getMessage());
            throw new GamesAPIException(e.getMessage(),
                    "Exception Occurred While Getting Data from Service");
        }
    }

    @Override
    public List<Game> getGamesForPlatformWithPagination(Long platformId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("released").ascending());
            Page<Game> gamePage = gameRepository.findByPlatforms_GamePlatformId_PlatformId(platformId, pageable);

            if (gamePage.isEmpty()) {
                return Collections.emptyList();
            }

            return gamePage.getContent();
        } catch (Exception e) {
            LOGGER.error("Exception occurred {}", e);
            throw new GamesAPIException(e.getMessage(),
                    "Exception Occurred While Getting Games from the Database");
        }
    }

    @Override
    public Platform getPlatformById(Long platformId) {
        LOGGER.debug("Fetching platform by ID: {}", platformId);
        Optional<Platform> platformOptional = platformRepository.findById(platformId);
        if (platformOptional.isPresent()) {
            Platform platform = platformOptional.get();
            LOGGER.debug("Platform found: {}", platform);
            return platform;
        } else {
            LOGGER.debug("Platform not found for ID: {}", platformId);
            throw new GamesAPIException("Platform not found", "Platform with id: " + platformId + " not found");
        }
    }

    @Override
    public Platform getPlatformByName(String platformName) {
        LOGGER.debug("Fetching platform by name: {}", platformName);
        Optional<Platform> platformOptional = platformRepository.findByName(platformName);
        if (platformOptional.isPresent()) {
            Platform platform = platformOptional.get();
            LOGGER.debug("Platform found: {}", platform);
            return platform;
        } else {
            LOGGER.debug("Platform not found for name: {}", platformName);
            throw new GamesAPIException("Platform not found", "Platform with name: " + platformName + " not found");
        }
    }

    @Override
    @Scheduled(fixedDelay = 600000)
    public String refreshPlatformData() {
        List<Platform> platformsFromApi = getAllPlatforms();

        platformsFromApi.forEach(platform -> {
            LOGGER.debug("Platform ID: {}, Name: {}, Hash Code: {}", platform.getId(), platform.getName(), platform.hashCode());
        });

        for (Platform apiPlatform : platformsFromApi) {
            Platform existingPlatform = platformRepository.findByName(apiPlatform.getName()).orElse(null);
            if (existingPlatform != null) {
                updatePlatformAttributes(existingPlatform, apiPlatform);
            } else {
                platformRepository.save(apiPlatform);
            }
        }

        return "Platform data refreshed from API.";
    }

    @Override
    public Game getGameDetailsBySlug(String gameSlug) {
        HttpHeaders headers = buildRequest();
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            String apiUrl = "https://api.rawg.io/api/games/" + gameSlug + "?key=" + apiKey;
            ResponseEntity<Game> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, Game.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Game game = response.getBody();
                if (game != null) {
                    return game;
                } else {
                    throw new GamesAPIException("Game not found", "Game with slug: " + gameSlug + " not found");
                }
            } else {
                throw new GamesAPIException("API Response Error - Status Code: " + response.getStatusCodeValue(),
                        "Exception Occurred While Getting Data from Service");
            }
        } catch (RestClientException e) {
            throw new GamesAPIException(e.getMessage(), "Exception Occurred While Getting Data from Service");
        }
    }

    @Override
    public String changePlatform(String platformIdOrName, Platform selectedPlatform) {
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
                List<String> platformNamesInDatabase = platformRepository.findAllPlatformIdsAndNames().stream()
                        .map(data -> (String) data[1])
                        .collect(Collectors.toList());
                return "Platform not found in the database.\nPlatform names in the database: " + platformNamesInDatabase;
            }
        } catch (GamesAPIException e) {
            return e.getMessage();
        }
    }

    @Override
    public String getGamesForPlatform(int pageNumber, int pageSize, String platformIdOrName) {
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
                selectedPlatform = getPlatformById(platformId);
            } catch (NumberFormatException ex) {
                selectedPlatform = getPlatformByName(platformIdOrName);
            }

            if (selectedPlatform == null) {
                return "Platform not found";
            }

            List<Game> games = getGamesForPlatformWithPagination(selectedPlatform.getId(), pageNumber, pageSize);

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

    @Override
    public String viewGameDetails(Platform selectedPlatform, int gameNumber) {
        if (selectedPlatform == null) {
            return "No platform selected. Please select a platform first.";
        }

        List<Game> games = getGamesForPlatformWithPagination(selectedPlatform.getId(), 1, 20);

        if (gameNumber < 1 || gameNumber > games.size()) {
            return "Invalid game number. Please select a valid game number from the list.";
        }

        Game selectedGame = games.get(gameNumber - 1);

        if (selectedGame.getDescription() == null) {
            try {
                Game detailedGameInfo = getGameDetailsBySlug(selectedGame.getSlug());
                selectedGame.setDescription(detailedGameInfo.getDescription());
                gameRepository.save(selectedGame);
            } catch (GamesAPIException e) {
                return "Error occurred while fetching game details: " + e.getMessage();
            }
        }
        return detailsForGame(selectedGame);
    }

    @PostConstruct
    public void initializePlatforms() {
        if (platformRepository.count() == 0) {
            List<Platform> platformsFromApi = getAllPlatforms();
            platformRepository.saveAll(platformsFromApi);
        }
    }

    private static String detailsForGame(Game selectedGame) {
        return "Details for game " + selectedGame.getName() + ":\n"
                + "Release Date: " + selectedGame.getReleaseDate() + "\n"
                + "Description: " + selectedGame.getDescription() + "\n"
                + "------------------------------------------";
    }

    private void updatePlatformAttributes(Platform existingPlatform, Platform apiPlatform) {
        existingPlatform.setImage(apiPlatform.getImage());
        existingPlatform.setImageBackground(apiPlatform.getImageBackground());
        platformRepository.save(existingPlatform);
    }

    private HttpHeaders buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        return headers;
    }

    private String buildUrl(String apiUrl) {
        return apiUrl + "?key=" + apiKey;
    }
}