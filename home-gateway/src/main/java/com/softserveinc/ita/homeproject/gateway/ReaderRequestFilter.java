package com.softserveinc.ita.homeproject.gateway;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PROXY_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

@Component
public class ReaderRequestFilter extends ZuulFilter {

    private static final String ZUUL_SERVICE_ID_HEADER = "X-Zuul-Service";

    private static final String READER_ROUTE_ID = "homeReader";

    private final String readerUrl;

    private final List<String> readerRoutes;

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final PathMatcher pathMatcher = new AntPathMatcher();

    public ReaderRequestFilter(@Value("${home.reader.url}") String readerUrl,
                               @Autowired CustomRoutes customRoutes) {
        this.readerUrl = readerUrl;
        this.readerRoutes = customRoutes.getReader();
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return !ctx.containsKey(FORWARD_TO_KEY) // a filter has already forwarded
                && !ctx.containsKey(SERVICE_ID_KEY); // a filter has already determined
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (isReaderRequest(ctx.getRequest())) {
            ctx.set(PROXY_KEY, READER_ROUTE_ID);
            ctx.setRouteHost(getUrl(readerUrl));
            updateZuulServiceIdHeader(ctx, readerUrl);
        }
        return null;
    }

    private boolean isReaderRequest(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod())
                && isReaderUri(request);
    }

    private boolean isReaderUri(HttpServletRequest request) {
        String requestUri = urlPathHelper.getPathWithinApplication(request);
        return readerRoutes.stream()
                .anyMatch(routePattern -> pathMatcher.match(routePattern, requestUri));
    }

    private URL getUrl(String target) {
        try {
            return new URL(target);
        } catch (MalformedURLException var3) {
            throw new IllegalStateException("Target URL is malformed", var3);
        }
    }

    private void updateZuulServiceIdHeader(RequestContext ctx, String newUrl) {
        ctx.getOriginResponseHeaders()
                .stream()
                .filter(e -> e.first().equals(ZUUL_SERVICE_ID_HEADER))
                .findFirst()
                .ifPresent(e -> e.setSecond(newUrl));
    }
}
