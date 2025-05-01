package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.user.model.UserRole;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdminRequest {
    private UserRole role;
}
