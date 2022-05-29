package com.softserveinc.ita.homeproject.homereader.repositories;

import com.softserveinc.ita.homeproject.homereader.model.NewsReader;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsReaderRepository extends MongoRepository<NewsReader, Long> {
}
