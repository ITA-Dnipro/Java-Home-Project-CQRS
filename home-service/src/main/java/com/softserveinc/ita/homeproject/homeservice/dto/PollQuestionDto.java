package com.softserveinc.ita.homeproject.homeservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PollQuestionDto extends BaseDto {

    private PollQuestionTypeDto type;

    private String question;

    private Long pollId;
}
