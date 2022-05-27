package com.softserveinc.ita.homeproject.readerapp.service;

import java.util.List;

import com.softserveinc.ita.homeproject.readerapp.controllers.models.ReadNewsReader;
import com.softserveinc.ita.homeproject.readerapp.models.NewsReader;

public interface NewsReaderService {

    List<ReadNewsReader> findAll();

    NewsReader createNews(NewsReader newsReader);
}
