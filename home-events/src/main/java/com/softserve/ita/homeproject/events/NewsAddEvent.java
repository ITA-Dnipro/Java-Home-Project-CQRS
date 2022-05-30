package com.softserve.ita.homeproject.events;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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

    public NewsAddEvent(Long id, LocalDateTime createDate, LocalDateTime updateDate, String title,
                        String text, String description, String photoUrl, String source, Boolean enabled) {
        this.id = id;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.title = title;
        this.text = text;
        this.description = description;
        this.photoUrl = photoUrl;
        this.source = source;
        this.enabled = enabled;
    }

    public NewsAddEvent() {
    }

    @Override
    public EventType getEventType() {
        return EventType.NEWS_ADD;
    }

}
