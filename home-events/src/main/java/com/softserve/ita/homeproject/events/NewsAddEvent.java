package com.softserve.ita.homeproject.events;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsAddEvent extends AppEvent{

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String title;

    private String text;

    private String description;

    private String photoUrl;

    private String source;

    private Boolean enabled;

}
