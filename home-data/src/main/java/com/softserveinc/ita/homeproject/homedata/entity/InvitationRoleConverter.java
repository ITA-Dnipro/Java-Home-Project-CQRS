package com.softserveinc.ita.homeproject.homedata.entity;

import javax.persistence.AttributeConverter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InvitationRoleConverter implements AttributeConverter<RoleEnum, String> {

    @Override
    public String convertToDatabaseColumn(RoleEnum attribute) {
        return attribute == null ? null : attribute.getName();
    }

    @Override
    public RoleEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            try {
                return RoleEnum.getEnum(dbData);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(dbData + " not supported.");
            }
        }
    }
}
