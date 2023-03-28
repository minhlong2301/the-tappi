package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank(message = "Username không được để trống")
    private String userName;

    @NotBlank(message = "Password không được để trống")
    private String passWord;
}
