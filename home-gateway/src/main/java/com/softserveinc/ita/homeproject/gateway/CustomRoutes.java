package com.softserveinc.ita.homeproject.gateway;

import java.util.Map;
import javax.annotation.PostConstruct;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("home.gateway.custom-routes")
public class CustomRoutes {

    @Data
    public static class CustomRoute {
        private ZuulRoute route;

        private String method;
    }

    private Map<String, CustomRoute> routes;

    @PostConstruct
    public void init() {
        for(Map.Entry<String, CustomRoute> entry : routes.entrySet()) {
            CustomRoute route = entry.getValue();
            route.getRoute().setId(entry.getKey());
        }
    }
}
