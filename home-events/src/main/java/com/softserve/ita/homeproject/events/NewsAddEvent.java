package com.softserve.ita.homeproject.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewsAddEvent extends AppEvent {

    private Long id;

    private String title;

    private String text;

    private String description;

    private Boolean enabled;

    public NewsAddEvent(Long id, String title, String text, String description, Boolean enabled) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.description = description;
        this.enabled = enabled;
    }

    public NewsAddEvent() {
    }

    @Override
    public EventType getEventType() {
        return EventType.NEWS_ADD;
    }

}
