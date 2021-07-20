package com.softserveinc.ita.homeproject.homeservice.service;

import com.softserveinc.ita.homeproject.homedata.entity.Cooperation;
import com.softserveinc.ita.homeproject.homeservice.dto.CooperationDto;

public interface CooperationService extends QueryableService<Cooperation, CooperationDto> {

    CooperationDto createCooperation(CooperationDto createCooperationDto);

    CooperationDto updateCooperation(CooperationDto oldCooperationDto, CooperationDto updateCooperationDto);

    void deactivateCooperation(CooperationDto cooperationDto);
}
