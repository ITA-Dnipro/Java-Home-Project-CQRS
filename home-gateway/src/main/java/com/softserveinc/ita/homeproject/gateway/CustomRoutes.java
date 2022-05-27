package com.softserveinc.ita.homeproject.gateway;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("home.gateway.custom-routes")
public class CustomRoutes {

    private List<String> reader;

    public List<String> getReader() {
        return reader;
    }

    public void setReader(List<String> reader) {
        this.reader = reader;
    }
}
