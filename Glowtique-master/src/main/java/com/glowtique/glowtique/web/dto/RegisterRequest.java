package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.user.model.UserGender;
import com.glowtique.glowtique.validation.PasswordMatchValidator;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatchValidator
public class RegisterRequest {
    @Size(min = 2, max = 20, message = "Първото Ви име трябва да е между 2 и 20 символа.")
    @NotNull(message = "Въведете име!")
    public String firstName;

    @Size(min = 2, max = 20, message = "Фамилията Ви трябва да е между 2 и 20 символа.")
    @NotNull(message = "Въведете фамилия!")
    public String lastName;

    @Email(message = "Грешен формат на имейл!")
    public String email;

    public UserGender gender;

    @NotNull(message = "Въведете дата на раждане!")
    @Past(message = "Датата на раждане не може да бъде в бъдещето!")
    public LocalDate birthday;

    @Size(min = 6, max = 25, message = "Паролата трябва да е между 6 и 25 символа!")
    @NotBlank(message = "Не може да бъде празна паролата Ви")
    @NotNull(message = "Въведете парола")
    public String password;

    @Size(min = 6, max = 25, message = "Паролата трябва да е между 6 и 25 символа!")
    @NotBlank(message = "Не може да бъде празна паролата Ви")
    @NotNull(message = "Повторете паролата")
    public String confirmPassword;

}
