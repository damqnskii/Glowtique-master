package com.glowtique.glowtique.web.mapper;

import com.glowtique.glowtique.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.glowtique.glowtique.user.model.User;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MapperUTest {

    @Test
    void gettingCorrectlyMappingToEditRequest() {
        User user = new User();
        user.setFirstName("Damian");
        user.setLastName("Apostolov");
        user.setEmail("damian.apostolov@gmail.com");
        user.setPhoneNumber("0898946872");
        user.setTown("Varna");
        user.setShippingAddress("Nqkude 12");
        user.setStreet("Tam nqkude");
        user.setFactureAddress("Obrochishte 15");

        EditProfileRequest dto = DtoMapper.mapUserToEditRequest(user);

        assertEquals(dto.getFirstName(), user.getFirstName());
        assertEquals(dto.getLastName(), user.getLastName());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(dto.getTown(), user.getTown());
        assertEquals(dto.getShippingAddress(), user.getShippingAddress());
        assertEquals(dto.getStreet(), user.getStreet());
        assertEquals(dto.getFactureAddress(), user.getFactureAddress());

    }
}
