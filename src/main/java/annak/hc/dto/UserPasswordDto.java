package annak.hc.dto;

import lombok.Data;

@Data
public class UserPasswordDto {
    private String oldPassword;
    private String newPassword;
    private String submitNewPassword;
}
