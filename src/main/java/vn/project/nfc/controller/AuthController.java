package vn.project.nfc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.project.nfc.request.CheckUserRequest;
import vn.project.nfc.request.LoginRequest;
import vn.project.nfc.request.RegisterRequest;
import vn.project.nfc.response.GlobalResponse;
import vn.project.nfc.service.AuthService;
import vn.project.nfc.service.ValidateService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ValidateService validateService;

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<GlobalResponse<Object>> registerAccount(@Valid @RequestBody RegisterRequest registerRequest,
                                                                  BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.ok(validateService.getErrorValidate(result));
        }
        return ResponseEntity.ok(authService.registerAccount(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<Object>> userLogin(@Valid @RequestBody LoginRequest loginRequest,
                                                            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.ok(validateService.getErrorValidate(result));
        }
        return ResponseEntity.ok(authService.userLogin(loginRequest));
    }

    @PostMapping("/check-user")
    public ResponseEntity<GlobalResponse<Object>> checkUser(@Valid @RequestBody CheckUserRequest checkUserRequest,
                                                            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.ok(validateService.getErrorValidate(result));
        }
        return ResponseEntity.ok(authService.checkUser(checkUserRequest));
    }

    @GetMapping("/get-user")
    public ResponseEntity<GlobalResponse<Object>> getUserFromNickName(@RequestParam(name = "nickName", required = true) String nickName) {
        return ResponseEntity.ok(authService.getUserByNickName(nickName));
    }

}
