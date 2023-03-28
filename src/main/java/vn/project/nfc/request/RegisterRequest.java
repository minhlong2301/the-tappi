package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username không được để trống")
    private String userName;

    @NotBlank(message = "Password không được để trống")
    private String passWord;

    @NotBlank(message = "NickName không được bỏ trống")
    private String nickName;

    @NotBlank(message = "Uuid không được để trống")
    private String uuid;

}
