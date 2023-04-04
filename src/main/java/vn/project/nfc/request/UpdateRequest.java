package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateRequest {

    private String content;

    private String avatar;

    private String nickName;
}