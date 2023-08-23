package com.db.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Platform implements Serializable {

    @Id
    private Long id;
    private String name;
    private String slug;
    private Long gamesCount;
    private String imageBackground;
    private String image;
    private int yearStart;
    private int yearEnd;

    /*@OneToMany(fetch = FetchType.LAZY, mappedBy = "gamePlatformId.platform")
    private List<GamePlatform> platform;*/
}