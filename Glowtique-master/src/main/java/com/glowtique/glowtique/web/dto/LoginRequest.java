package com.glowtique.glowtique.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class LoginRequest {
    @Email(message = "Must be email!")
    private String email;

    @Size(min = 6, max = 25, message = "Must be between 6 and 25 chars!")
    private String password;

}
