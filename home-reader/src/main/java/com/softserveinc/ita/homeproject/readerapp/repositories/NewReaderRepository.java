package com.softserveinc.ita.homeproject.readerapp.repositories;

import com.softserveinc.ita.homeproject.readerapp.models.NewsReader;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewReaderRepository extends MongoRepository<NewsReader, Long> {
}
