package com.softserveinc.ita.homeproject.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "home.gateway.cqrs.enabled", havingValue = "true", matchIfMissing = true)
public class ZuulConfig {

    @Bean
    public SimpleRouteLocator simpleRouteLocator(CustomRoutes customRoutes, ZuulProperties zuulProperties,
                                                 ServerProperties server) {
        return new CustomRouteLocator(customRoutes, server.getServlet().getContextPath(),
                zuulProperties);
    }
}
