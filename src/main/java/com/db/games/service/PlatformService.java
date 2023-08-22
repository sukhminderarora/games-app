package com.db.games.service;

import com.db.games.domain.Game;
import com.db.games.domain.Platform;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlatformService {

    List<Platform> getAllPlatforms();

    Platform getPlatformById(Long platformId);

    Platform getPlatformByName(String platformName);

    List<Game> getGamesForPlatformWithPagination(Long platformId, int pageNumber, int pageSize);

    String refreshPlatformData();

    Game getGameDetailsBySlug(String gameSlug);

    String changePlatform(String platformIdOrName, Platform selectedPlatform);

    String getGamesForPlatform(int pageNumber, int pageSize, String platformIdOrName);

    String viewGameDetails(Platform selectedPlatform, int gameNumber);
}