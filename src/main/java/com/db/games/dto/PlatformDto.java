package com.db.games.dto;

import com.db.games.domain.Platform;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class PlatformDto {
    private int count;
    private String next;
    private String previous;
    private List<Platform> results;
}
