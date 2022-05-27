package com.softserveinc.ita.homeproject.readerapp.service.mapper.config;

public interface TypeMatcher<S, D> {
    boolean match(Class<? extends S> sourceType, Class<? extends D> destinationType);
}
