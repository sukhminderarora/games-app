package com.db.games.service;

import com.db.games.domain.Platform;
import com.db.games.dto.PlatformDto;
import com.db.games.exception.GamesAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PlatformServiceImpl implements PlatformService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${games.list.url}")
    private String gamesListApiUrl;

    @Value("${api.key}")
    private String apiKey;

    @Override
    public List<Platform> getAllPlatforms() {
        LOGGER.debug("Start of getAllGames method ==> ");
        HttpHeaders headers = buildRequest();
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            String url = buildUrl(gamesListApiUrl);
            LOGGER.debug("Start of Rest Template Exhange withparams {}  ==> ", url);
            ResponseEntity<PlatformDto> response = restTemplate.exchange(url, HttpMethod.GET, request,
                    new ParameterizedTypeReference<PlatformDto>() {
                    });
            PlatformDto resp = response.getBody();
            if (CollectionUtils.isEmpty(resp.getResults())) {
                LOGGER.debug("Empty RESPONSE Received from API", resp);
                return resp.getResults();
            }
            LOGGER.debug("End of Rest Template Exhange and response received {}  with size ==> ", resp, resp.getResults().size());
            return response.getBody().getResults();
        } catch (Exception e) {
            LOGGER.error("Exception occurred {}", e);
            throw new GamesAPIException(e.getMessage(),
                    "Exception Occurred While Getting Data from Service");
        }
    }

    private HttpHeaders buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        return headers;
    }

    private String buildUrl(String apiUrl) {
        return apiUrl + "?key=" + apiKey;
    }
}
