package com.db.games.repsitory;

import com.db.games.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Page<Game> findByPlatforms_GamePlatformId_PlatformId(Long platformId, Pageable pageable);

}