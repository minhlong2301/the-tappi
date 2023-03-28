package vn.project.nfc.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import vn.project.nfc.response.GlobalResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ValidateService {

    public GlobalResponse<Object> getErrorValidate(BindingResult result) {
        Map<String, String> errorValidates = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorValidates.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return GlobalResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Thất bại")
                .data(errorValidates)
                .build();
    }
}
