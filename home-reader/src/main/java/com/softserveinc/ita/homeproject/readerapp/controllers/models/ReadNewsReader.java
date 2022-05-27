package com.softserveinc.ita.homeproject.readerapp.controllers.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadNewsReader extends BaseModel{

    private String title;

    private String photoUrl;

    private String description;

    private String source;

    private String text;
}
