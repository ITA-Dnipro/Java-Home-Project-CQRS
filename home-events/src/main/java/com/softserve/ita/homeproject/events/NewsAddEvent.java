package com.softserve.ita.homeproject.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsAddEvent extends AppEvent {

    private Long id;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String title;

    private String text;

    private String description;

    private String photoUrl;

    private String source;

    private Boolean enabled;

    @Override
    public EventType getEventType() {
        return EventType.NEWS_ADD;
    }
}
