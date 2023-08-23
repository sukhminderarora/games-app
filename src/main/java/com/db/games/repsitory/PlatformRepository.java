package com.db.games.repsitory;

import com.db.games.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findById(Long platformId);
    Optional<Platform> findByName(String platformName);
    
    @Query("SELECT p.id, p.name FROM Platform p")
    List<Object[]> findAllPlatformIdsAndNames();
}