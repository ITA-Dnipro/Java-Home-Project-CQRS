package com.softserveinc.ita.homeproject.homereader.service;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.softserveinc.ita.homeproject.homereader.model.ReadNews;
import org.springframework.data.domain.PageRequest;

public interface NewsReaderService {

    List<ReadNews> findAll();

    List<ReadNews> findAll(BooleanExpression predicate,
                           PageRequest pageable);

    ReadNews findById(Long id);
}
