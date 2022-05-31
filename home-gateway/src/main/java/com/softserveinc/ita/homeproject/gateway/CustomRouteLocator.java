package com.softserveinc.ita.homeproject.gateway;

import java.util.ArrayList;
import java.util.List;

import com.netflix.zuul.context.RequestContext;
import com.softserveinc.ita.homeproject.gateway.CustomRoutes.CustomRoute;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class CustomRouteLocator extends SimpleRouteLocator {

    private final List<CustomRoute> customRoutes;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    public CustomRouteLocator(CustomRoutes customRoutes, String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.customRoutes = new ArrayList<>(customRoutes.getRoutes().values());
    }

    @Override
    protected ZuulRoute getZuulRoute(String adjustedPath) {
        ZuulRoute customZuulRoute = getCustomZuulRoute(adjustedPath);
        if (customZuulRoute != null) {
            return customZuulRoute;
        }
        return super.getZuulRoute(adjustedPath);
    }

    private ZuulRoute getCustomZuulRoute(String adjustedPath) {
        for (CustomRoute customRoute : customRoutes) {
            if (customRouteMatches(customRoute, adjustedPath)) {
                return customRoute.getRoute();
            }
        }
        return null;
    }

    private boolean customRouteMatches(CustomRoute customRoute, String path) {
        return requestMethodMatches(customRoute.getMethod())
                && pathMatches(customRoute.getRoute().getPath(), path);
    }

    private boolean requestMethodMatches(String routeHttpMethod) {
        String currentRequestMethod = RequestContext.getCurrentContext().getRequest().getMethod();
        return routeHttpMethod == null || routeHttpMethod.equalsIgnoreCase(currentRequestMethod);
    }

    private boolean pathMatches(String pattern, String path) {
        return pathMatcher.match(pattern, path);
    }
}
