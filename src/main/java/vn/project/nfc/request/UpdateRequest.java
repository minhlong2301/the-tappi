package vn.project.nfc.request;

import lombok.Data;

@Data
public class UpdateRequest {

    private String content;

    private String avatar;

    private String nickName;

    private String description;
}
