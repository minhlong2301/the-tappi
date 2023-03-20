package vn.project.nfc.request;

import lombok.Data;

import java.util.Date;

@Data
public class UserRequest {

    private String userName;

    private String uuid;

    private String fb;

    private String tiktok;

    private String ig;

    private String avatar;

    private String description;

    private String page;

}
