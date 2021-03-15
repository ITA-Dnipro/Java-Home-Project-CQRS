package com.softserveinc.ita.homeproject.application.config.rsql;

import com.softserveinc.ita.homeproject.homedata.entity.House;
import com.softserveinc.ita.homeproject.homeservice.query.QueryConfig;
import com.softserveinc.ita.homeproject.homeservice.query.impl.HouseQueryConfig;
import com.softserveinc.ita.homeproject.homeservice.query.impl.HouseQueryConfig.HouseQueryParamEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class HouseRSQLEndpointConfig implements RSQLEndpointConfig<House, HouseQueryParamEnum>{

    @Autowired
    private HouseQueryConfig queryConfig;

    @Override
    public Map<HouseQueryParamEnum, String> getMappings() {
        HashMap<HouseQueryParamEnum, String> map = new HashMap<>();

        map.put(HouseQueryParamEnum.ADJOINING_AREA, "adjoiningArea");
        map.put(HouseQueryParamEnum.HOUSE_AREA, "houseArea");
        map.put(HouseQueryParamEnum.QUANTITY_FLAT, "quantityFlat");

        return map;
    }

    @Override
    public QueryConfig<House> getQueryConfig() {
        return queryConfig;
    }
}
