package vn.project.nfc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import vn.project.nfc.request.UpdateRequest;
import vn.project.nfc.request.UserRequest;
import vn.project.nfc.response.GlobalResponse;
import vn.project.nfc.service.NfcService;
import vn.project.nfc.service.ValidateService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/expose/nfc")
public class NfcController {

    private final NfcService nfcService;

    private final ValidateService validateService;

    @PostMapping("/create")
    public ResponseEntity<GlobalResponse<Object>> create(@Valid @RequestBody UserRequest userRequest,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.ok(validateService.getErrorValidate(result));
        }
        return ResponseEntity.ok(nfcService.create(userRequest));
    }

    @PostMapping("/update")
    public ResponseEntity<GlobalResponse<Object>> create(@Valid @RequestBody UpdateRequest updateRequest,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.ok(validateService.getErrorValidate(result));
        }
        return ResponseEntity.ok(nfcService.update(updateRequest));
    }

    @GetMapping("/get-user")
    public ResponseEntity<GlobalResponse<Object>> getUserMyselt () {
        return ResponseEntity.ok(nfcService.getUserMyselft());
    }

    @GetMapping("/test")
    public String getUserFromUUID () {
        return String.valueOf(ResponseEntity.ok(nfcService.testAPI()));
    }


}
