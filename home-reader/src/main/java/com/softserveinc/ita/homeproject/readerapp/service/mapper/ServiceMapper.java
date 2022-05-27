package com.softserveinc.ita.homeproject.readerapp.service.mapper;

import java.util.Set;
import javax.annotation.PostConstruct;

import com.softserveinc.ita.homeproject.readerapp.controllers.models.BaseModel;
import com.softserveinc.ita.homeproject.readerapp.models.BaseDocument;
import com.softserveinc.ita.homeproject.readerapp.service.mapper.config.AbstractTypeConverter;
import com.softserveinc.ita.homeproject.readerapp.service.mapper.config.ServiceMappingConfig;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.ConditionalConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceMapper {

    private static final String ENTITY_PACKAGE = "com.softserveinc.ita.homeproject.readerapp.models";

    private static final String DTO_PACKAGE = "com.softserveinc.ita.homeproject.readerapp.controllers.models";

    private ModelMapper modelMapper;

    private final Set<ServiceMappingConfig<?,?>> serviceMappingConfigs;

    @PostConstruct
    @SuppressWarnings({"unchecked cast", "rawtypes"})
    public void init() {
        modelMapper = new ModelMapper();

        ConditionalConverter<BaseDocument, BaseModel> conditionalConverter =
            new AbstractTypeConverter<>(ENTITY_PACKAGE, DTO_PACKAGE);
        ConditionalConverter<BaseModel, BaseDocument> conditionalConverter2 =
            new AbstractTypeConverter<>(DTO_PACKAGE, ENTITY_PACKAGE);

        modelMapper.getConfiguration().getConverters().add(0, conditionalConverter);
        modelMapper.getConfiguration().getConverters().add(0, conditionalConverter2);

        for (ServiceMappingConfig serviceMappingConfig : serviceMappingConfigs) {
            TypeMap typeMap = modelMapper.typeMap(
                    serviceMappingConfig.getSourceType(), serviceMappingConfig.getDestinationType());
            serviceMappingConfig.addMappings(typeMap);
        }
    }

    public <D> D convert(Object source, Class<D> destination) {
        return modelMapper.map(source, destination);
    }
}
