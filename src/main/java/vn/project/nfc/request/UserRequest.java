package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRequest {

    @NotBlank(message = "content không được để trống")
    private String content;

    @NotBlank(message = "avatar không được để trống")
    private String avatar;

    @NotBlank(message = "nickName không được để trống")
    private String nickName;

    @NotBlank(message = "description không được để trống")
    private String description;

    private String userName;
}
