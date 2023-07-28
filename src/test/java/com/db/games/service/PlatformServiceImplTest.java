package com.db.games.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.db.games.domain.Game;
import com.db.games.domain.Platform;
import com.db.games.dto.PlatformDto;
import com.db.games.exception.GamesAPIException;
import com.db.games.repsitory.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PlatformServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PlatformRepository platformRepository;

    @InjectMocks
    private PlatformServiceImpl platformService;

    private static final String GAMES_LIST_API_URL = "https://api.rawg.io/api/games";
    private static final String API_KEY = "a7ad0ae7470743fdb78374044236e6e5";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(platformService, "gamesListApiUrl", GAMES_LIST_API_URL);
        ReflectionTestUtils.setField(platformService, "apiKey", API_KEY);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllPlatforms_Success() {
        PlatformDto platformDto = new PlatformDto();
        Platform platform1 = new Platform();
        platform1.setId(1L);
        platform1.setName("Platform 1");
        Platform platform2 = new Platform();
        platform2.setId(2L);
        platform2.setName("Platform 2");
        platformDto.setResults(Arrays.asList(platform1, platform2));

        String expectedUrl = "https://api.rawg.io/api/games?key=a7ad0ae7470743fdb78374044236e6e5";
        HttpMethod expectedHttpMethod = HttpMethod.GET;

        ResponseEntity<PlatformDto> responseEntity = ResponseEntity.ok(platformDto);
        when(restTemplate.exchange(
                eq(expectedUrl),  // Match the URL
                eq(expectedHttpMethod),  // Match the HTTP method
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<PlatformDto>() {})  // Match the return type
        )).thenReturn(responseEntity);

        List<Platform> platforms = platformService.getAllPlatforms();

        assertThat(platforms).hasSize(2);
        assertThat(platforms.get(0).getName()).isEqualTo("Platform 1");
        assertThat(platforms.get(1).getName()).isEqualTo("Platform 2");
    }


    @Test
    public void testGetAllPlatforms_EmptyResponse() {
        PlatformDto platformDto = new PlatformDto();
        platformDto.setResults(Collections.emptyList());

        ResponseEntity<PlatformDto> responseEntity = ResponseEntity.ok(platformDto);
        String expectedUrl = "https://api.rawg.io/api/games?key=a7ad0ae7470743fdb78374044236e6e5";
        when(restTemplate.exchange(
                eq(expectedUrl),  // Match the URL
                eq(HttpMethod.GET),  // Match the HTTP method
                any(HttpEntity.class),  // Match any HttpEntity argument
                eq(new ParameterizedTypeReference<PlatformDto>() {})  // Match the return type
        )).thenReturn(responseEntity);

        List<Platform> platforms = platformService.getAllPlatforms();

        assertThat(platforms).isEmpty();
    }

    @Test
    public void testGetPlatformById_Success() {
        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("Platform 1");
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));

        Platform resultPlatform = platformService.getPlatformById(1L);

        assertThat(resultPlatform).isNotNull();
        assertThat(resultPlatform.getId()).isEqualTo(1L);
        assertThat(resultPlatform.getName()).isEqualTo("Platform 1");
    }

    @Test
    public void testGetPlatformById_NotFound() {
        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        GamesAPIException ex = assertThrows(GamesAPIException.class, () -> {
            platformService.getPlatformById(1L);
        });

        assertThat(ex.getMessage()).isEqualTo("Platform with id: 1 not found");
        assertThat(ex.getDetailedMessage()).isEqualTo("Platform with id: 1 not found");
    }

    @Test
    public void testGetPlatformByName_Success() {
        Platform platform = new Platform();
        platform.setId(1L);
        platform.setName("Platform 1");
        when(platformRepository.findByName("Platform 1")).thenReturn(Optional.of(platform));

        Platform resultPlatform = platformService.getPlatformByName("Platform 1");

        assertThat(resultPlatform).isNotNull();
        assertThat(resultPlatform.getId()).isEqualTo(1L);
        assertThat(resultPlatform.getName()).isEqualTo("Platform 1");
    }

    @Test
    public void testGetPlatformByName_NotFound() {
        when(platformRepository.findByName("Platform 1")).thenReturn(Optional.empty());

        GamesAPIException ex = assertThrows(GamesAPIException.class, () -> {
            platformService.getPlatformByName("Platform 1");
        });

        assertThat(ex.getMessage()).isEqualTo("Platform with name: Platform 1 not found");
        assertThat(ex.getDetailedMessage()).isEqualTo("Platform with name: Platform 1 not found");
    }

    @Test
    public void testGetGameDetailsBySlug_Success() {
        Game game = new Game();
        game.setSlug("game-slug");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Game.class)))
                .thenReturn(ResponseEntity.ok(game));

        Game resultGame = platformService.getGameDetailsBySlug("game-slug");

        assertThat(resultGame).isNotNull();
        assertThat(resultGame.getSlug()).isEqualTo("game-slug");
    }

}

