package vn.project.nfc.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateRequest {

    @NotBlank(message = "Content không được để trống")
    private String content;
}
