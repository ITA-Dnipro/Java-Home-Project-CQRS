package com.softserveinc.ita.homeproject.homeservice.service.impl;

import java.util.List;

import com.softserveinc.ita.homeproject.homedata.entity.Contact;
import com.softserveinc.ita.homeproject.homedata.entity.ContactType;
import com.softserveinc.ita.homeproject.homedata.entity.User;
import com.softserveinc.ita.homeproject.homedata.repository.ContactRepository;
import com.softserveinc.ita.homeproject.homeservice.dto.ContactDto;
import com.softserveinc.ita.homeproject.homeservice.exception.AlreadyExistHomeException;
import com.softserveinc.ita.homeproject.homeservice.exception.NotFoundHomeException;
import com.softserveinc.ita.homeproject.homeservice.mapper.ServiceMapper;
import com.softserveinc.ita.homeproject.homeservice.service.UserContactService;
import org.springframework.stereotype.Service;

@Service
public class UserContactServiceImpl extends BaseContactService implements UserContactService {

    private final UserServiceImpl userService;

    public UserContactServiceImpl(ContactRepository contactRepository,
                                  ServiceMapper mapper, UserServiceImpl userService) {
        super(contactRepository, mapper);
        this.userService = userService;
    }

    @Override
    protected void checkAndFillParentEntity(ContactDto contactDto, Contact createContact, Long parentEntityId) {
        var user = mapper.convert(userService.getUserById(parentEntityId), User.class);
        if (Boolean.TRUE.equals(contactDto.getMain())) {
            List<Contact> allByUserIdAndType = contactRepository
                .findAllByUserIdAndType(parentEntityId, mapper.convert(contactDto.getType(), ContactType.class));
            allByUserIdAndType
                .stream()
                .filter(Contact::getMain)
                .findAny()
                .ifPresent(contact -> {
                    throw new AlreadyExistHomeException("User with id "
                        + parentEntityId + " already has main " + contactDto.getType() + " contact");
                });
        }
        createContact.setUser(user);
    }

    @Override
    protected Contact checkAndGetContactByParentId(Long contactId, Long parentEntityId) {
        return contactRepository.findByIdAndUserId(contactId, parentEntityId)
            .filter(Contact::getEnabled)
            .orElseThrow(() -> new NotFoundHomeException("User with id:" + contactId + " is not found"));
    }
}
