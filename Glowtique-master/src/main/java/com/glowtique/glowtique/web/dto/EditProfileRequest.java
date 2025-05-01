package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.user.model.Country;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditProfileRequest {
    @NotBlank(message = "Въведете име!")
    @Size(min= 2, max = 20, message = "Името Ви трябва да бъде между от 2 и 20 символа")
    private String firstName;
    @NotBlank(message = "Въведете фамилия!")
    @Size(min = 2, max = 20, message = "Фамилията трябва да бъде между от 2 и 20 символа")
    private String lastName;

    @Email(message = "Грешен формат за имейл!")
    @NotBlank(message = "Въведете имейл адрес!")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Въведете валиден телефонен номер")
    private String phoneNumber;

    private Country country;

    private String town;

    private String street;

    private String factureAddress;

    private String shippingAddress;
}
