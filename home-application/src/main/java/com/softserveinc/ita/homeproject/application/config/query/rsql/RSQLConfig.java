package com.softserveinc.ita.homeproject.application.config.query.rsql;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import com.softserveinc.ita.homeproject.application.config.StringToEnumConverter;
import com.softserveinc.ita.homeproject.application.config.query.QueryParamEnum;
import com.softserveinc.ita.homeproject.homedata.entity.BaseEntity;
import io.github.perplexhub.rsql.RSQLCommonSupport;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RSQLConfig {

    @Autowired
    private Set<RSQLEndpointConfig<? extends BaseEntity, ? extends QueryParamEnum>> rsqlEndpointConfigSet;

    @PostConstruct
    public void init() {
        rsqlEndpointConfigSet.forEach(config -> {
            Class<? extends BaseEntity> entityClass = config.getQueryConfig().getEntityClass();
            for (Map.Entry<? extends QueryParamEnum, String> entry : config.getMappings().entrySet()) {
                RSQLCommonSupport.addMapping(entityClass, entry.getValue(), entry.getKey().getParameter());
            }

            RSQLJPASupport.addConverter(new StringToEnumConverter());

            RSQLCommonSupport.addPropertyWhitelist(
                entityClass,
                config.getQueryConfig().getWhiteListEnums().stream()
                    .map(QueryParamEnum::getParameter)
                    .collect(Collectors.toList())
            );
        });
    }
}
