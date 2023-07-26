package com.db.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class GamePlatform implements Serializable {

    @EmbeddedId
    private GamePlatformId gamePlatformId;

    @ManyToOne
    @MapsId("gameId")
    private Game game;

}