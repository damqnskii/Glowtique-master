package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.order.model.OrderMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String firstName;
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Въведете валиден телефонен номер !")
    private String phoneNumber;
    @NotBlank(message = "Въведете личен адрес !")
    private String shippingAddress;
    @NotBlank()
    @Pattern(regexp = "^[0-9]{4}$", message = "Въведете валиден пощенски код!")
    private String postCode;
    @NotBlank(message = "Въведете град !")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s]+$", message = "Градът трябва да съдържа само букви!")
    private String town;
    private String officeAddress;
    @NotNull(message = "Изберете начин на доставка !")
    private OrderMethod orderMethod;

    private String description;

}
