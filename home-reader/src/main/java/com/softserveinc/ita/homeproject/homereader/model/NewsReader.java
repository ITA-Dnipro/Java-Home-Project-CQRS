package com.softserveinc.ita.homeproject.homereader.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class NewsReader{

    @Id
    private Long id;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String title;

    private String text;

    private String description;

    private String photoUrl;

    private String source;

    private Boolean enabled;
}
