package com.softserveinc.ita.homeproject.homeservice.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Model {
    private String message;

    private Integer age;

}
