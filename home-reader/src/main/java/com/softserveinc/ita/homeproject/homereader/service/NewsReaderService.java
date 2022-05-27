package com.softserveinc.ita.homeproject.homereader.service;

import java.util.List;

import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import com.softserveinc.ita.homeproject.homereader.model.ReadNews;

public interface NewsReaderService {

    List<ReadNews> findAll();

    ReadNews findById(Long id);
}
