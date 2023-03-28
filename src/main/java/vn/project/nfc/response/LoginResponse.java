package vn.project.nfc.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String userName;

    private String type = "Bearer";

    private String accessToken;

    private String role;

    private String refreshToken;
}
