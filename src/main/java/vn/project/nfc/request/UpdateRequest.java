package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateRequest {

    private String content;

    private String avatar;

    @NotBlank(message = "Nickname không được để trống")
    private String nickName;

    private String description;

    private String templates;
}
