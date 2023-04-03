package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String passWord;

    @NotBlank(message = "NickName không được bỏ trống")
    private String nickName;

    @NotBlank(message = "Uuid không được để trống")
    private String uuid;

    @NotBlank(message = "Telephone không được để trống")
    private String telephone;
}
