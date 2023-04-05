package vn.project.nfc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.project.nfc.jwt.JwtProvider;
import vn.project.nfc.model.User;
import vn.project.nfc.repository.UserRepository;
import vn.project.nfc.request.CheckUserRequest;
import vn.project.nfc.request.LoginRequest;
import vn.project.nfc.request.RegisterRequest;
import vn.project.nfc.response.GlobalResponse;
import vn.project.nfc.response.GlobalUserResponse;
import vn.project.nfc.response.LoginResponse;
import vn.project.nfc.sercurity.impl.UserDetailsImpl;
import vn.project.nfc.utils.GenericService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoderAndDecode;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final GenericService genericService;

    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Transactional
    public GlobalResponse<Object> registerAccount(RegisterRequest registerRequest) {
        Optional<User> user = userRepository.findByUuid(registerRequest.getUuid());
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (!user.isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Bạn chưa có mã thẻ để đăng kí tài khoản")
                    .data(null)
                    .build();
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email đã tồn tại")
                    .data(null)
                    .build();
        }
        if (userRepository.findByNickName(registerRequest.getNickName()).isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Nickname đã tồn tại")
                    .data(null)
                    .build();
        }
        user.get().setNickName(registerRequest.getNickName());
        user.get().setEmail(registerRequest.getEmail());
        user.get().setTelephone(registerRequest.getTelephone());
        user.get().setPassWord(passwordEncoderAndDecode.encode(registerRequest.getPassWord()));
        userRepository.save(user.get());
        BeanUtils.copyProperties(user.get(), globalUserResponse);
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(globalUserResponse)
                .build();
    }

    public GlobalResponse<Object> userLogin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassWord()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
//        Boolean isPasswordMatch = passwordEncoderAndDecode.matches(loginRequest.getPassWord(), userDetailsImpl.getPassword());
        String token = jwtProvider.createJwtToken(userDetailsImpl);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(token);
        loginResponse.setUserName(userDetailsImpl.getUsername());
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(loginResponse)
                .build();
    }

    public GlobalResponse<Object> checkUser(CheckUserRequest checkUserRequest) {
        Optional<User> user = userRepository.findByUuid(checkUserRequest.getUuid());
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (user.isPresent()) {
            BeanUtils.copyProperties(user.get(), globalUserResponse);
            return GlobalResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Thành công")
                    .data(globalUserResponse)
                    .build();
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Chưa tồn tại mã thẻ")
                    .data(null)
                    .build();
        }
    }

    public GlobalResponse<Object> getUserByNickName(String nickName) {
        Optional<User> user = userRepository.findByNickName(nickName);
        if (!user.isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Nickname không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Thành công")
                    .data(user.get())
                    .build();
        }
    }

    public GlobalResponse<Object> getQrCode(String uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            String qrCodeBase64 = genericService.generateQRCode(user.get().getUrl(), 300, 300);
            if (StringUtils.hasText(qrCodeBase64)) {
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(qrCodeBase64)
                        .build();
            }
        }
        return GlobalResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Uuid không tồn tại trên hệ thống")
                .data(null)
                .build();
    }

}
