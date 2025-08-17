package com.solvtrends.monolito.model;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    private String oldPassword;
    private String newPassword;

}
