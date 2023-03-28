package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CheckUserRequest {

    @NotBlank(message = "Uuid không được để trống")
    private String uuid;
}
