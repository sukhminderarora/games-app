package com.db.games.service;

import com.db.games.domain.Platform;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlatformService {

    List<Platform> getAllPlatforms();
}
