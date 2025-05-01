package com.glowtique.glowtique.web.mapper;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.web.dto.EditProfileRequest;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class DtoMapper {

    public static EditProfileRequest mapUserToEditRequest(User user) {
        return EditProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .town(user.getTown())
                .shippingAddress(user.getShippingAddress())
                .street(user.getStreet())
                .factureAddress(user.getFactureAddress())
                .build();
    }
}
