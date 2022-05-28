package com.softserveinc.ita.homeproject.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PROXY_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ReadRequestFilterTest {

    private static final String TEST_GATEWAY_URL = "https://home-gateway.com";
    private static final String TEST_WRITER_URL = "https://home-writer.com";
    private static final String TEST_READER_URL = "https://home-reader.com";
    private static final List<String> TEST_READER_ROUTES = List.of("/users/**", "/news");

    private static final String READER_ROUTE_NAME = "homeReader";
    private static final String WRITER_ROUTE_NAME = "homeApplication";
    private static final String ZUUL_SERVICE_ID_HEADER = "X-Zuul-Service";

    private ReaderRequestFilter filter;

    @BeforeEach
    void clearRequestContext() {
        getRequestContext().clear();
        filter = createRequestFilter();
    }

    @Test
    void filterAppliesRightAfterDefaultRouting() {
        assertEquals(PRE_TYPE, filter.filterType());
        assertEquals(PRE_DECORATION_FILTER_ORDER + 1, filter.filterOrder());
    }

    @Test
    void filterAppliesByDefault() {
        assertTrue(filter.shouldFilter());
    }

    @Test
    void filterDoesntApplyIfForwardToIsSet() {
        getRequestContext().set(FORWARD_TO_KEY, "dummyValue");
        assertFalse(filter.shouldFilter());
    }

    @Test
    void filterDoesntApplyIfServiceIdIsSet() {
        getRequestContext().set(SERVICE_ID_KEY, "dummyValue");
        assertFalse(filter.shouldFilter());
    }

    @Test
    void filterReroutesReaderRequest() {
        configureRequestContext("GET", "/news");
        filter.run();
        assertRequestContextPointsToReader();
    }

    @Test
    void filterReroutesReaderRequestToRouteWithAntExpression() {
        configureRequestContext("GET", "/users/1");
        filter.run();
        assertRequestContextPointsToReader();
    }

    private void assertRequestContextPointsToReader() {
        assertEquals(READER_ROUTE_NAME, getRequestContext().get(PROXY_KEY));
        assertEquals(createUrl(TEST_READER_URL), getRequestContext().getRouteHost());
        assertEquals(TEST_READER_URL, getZuulServiceId(getRequestContext()));
    }

    @Test
    void filterDoesntRerouteRequestWithNonReaderUri() {
        configureRequestContext("GET", "/nonReaderUri");
        filter.run();
        assertRequestContextPointsToWriter();
    }

    @Test
    void filterDoesntRerouteRequestWithPostMethod() {
        configureRequestContext("POST", "/news");
        filter.run();
        assertRequestContextPointsToWriter();
    }

    private void assertRequestContextPointsToWriter() {
        assertEquals(WRITER_ROUTE_NAME, getRequestContext().get(PROXY_KEY));
        assertEquals(createUrl(TEST_WRITER_URL), getRequestContext().getRouteHost());
        assertEquals(TEST_WRITER_URL, getZuulServiceId(getRequestContext()));
    }

    @Test
    void filterThrowsExceptionOnBrokenReaderUrl() {
        CustomRoutes customRoutes = new CustomRoutes();
        customRoutes.setReader(TEST_READER_ROUTES);
        String brokenUrl = "invalidProtocol://home-reader.com";
        ReaderRequestFilter brokenUrlFilter = new ReaderRequestFilter(brokenUrl, customRoutes);

        configureRequestContext("GET", "/news");

        assertThrows(IllegalStateException.class, brokenUrlFilter::run);
    }

    private ReaderRequestFilter createRequestFilter() {
        CustomRoutes customRoutes = new CustomRoutes();
        customRoutes.setReader(TEST_READER_ROUTES);
        return new ReaderRequestFilter(TEST_READER_URL, customRoutes);
    }

    private void configureRequestContext(String method, String uri) {
        RequestContext context = getRequestContext();
        configureRouteNameAsDefaultFilterDoes(context);
        configureRouteHostAsDefaultFilterDoes(context);
        context.setRequest(createMockRequest(method, uri));
        configureZuulHeaders(context);
    }

    private void configureRouteNameAsDefaultFilterDoes(RequestContext context) {
        context.set(PROXY_KEY, WRITER_ROUTE_NAME);
    }

    private void configureRouteHostAsDefaultFilterDoes(RequestContext context) {
        context.setRouteHost(createUrl(TEST_WRITER_URL));
    }

    private void configureZuulHeaders(RequestContext context) {
        context.addOriginResponseHeader("dummyHeader1", "dummyValue1");
        context.addOriginResponseHeader("dummyHeader2", "dummyValue2");
        addZuulServiceHeaderSameAsDefaultFilterDoes(context);
    }

    private void addZuulServiceHeaderSameAsDefaultFilterDoes(RequestContext context) {
        context.addOriginResponseHeader(ZUUL_SERVICE_ID_HEADER, TEST_WRITER_URL);
    }

    private MockHttpServletRequest createMockRequest(String method, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(TEST_GATEWAY_URL);
        request.setRequestURI(uri);
        request.setMethod(method);
        return request;
    }

    private String getZuulServiceId(RequestContext context) {
        return context.getOriginResponseHeaders()
                .stream()
                .filter(e -> e.first().equals(ZUUL_SERVICE_ID_HEADER))
                .map(Pair::second)
                .findFirst()
                .orElseThrow();
    }

    private RequestContext getRequestContext() {
        return RequestContext.getCurrentContext();
    }

    private URL createUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to create URL " + urlString);
        }
    }
}