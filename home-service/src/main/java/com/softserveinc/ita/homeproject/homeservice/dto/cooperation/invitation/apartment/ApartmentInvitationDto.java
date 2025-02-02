package com.softserveinc.ita.homeproject.homeservice.dto.cooperation.invitation.apartment;

import com.softserveinc.ita.homeproject.homeservice.dto.cooperation.invitation.InvitationDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApartmentInvitationDto extends InvitationDto {

    private String apartmentNumber;

    private Long apartmentId;
}
