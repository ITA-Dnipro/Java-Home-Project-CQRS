package com.softserve.ita.homeproject.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsDeleteEvent extends AppEvent{

    private Long id;

    @Override
    public EventType getEventType() {
        return EventType.NEWS_DELETE;
    }
}
