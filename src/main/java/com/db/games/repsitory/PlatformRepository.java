package com.db.games.repsitory;

import com.db.games.domain.Platform;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends CrudRepository<Platform, Long> {

}
