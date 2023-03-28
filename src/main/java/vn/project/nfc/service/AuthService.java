package vn.project.nfc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.project.nfc.jwt.JwtProvider;
import vn.project.nfc.model.User;
import vn.project.nfc.repository.UserRepository;
import vn.project.nfc.request.CheckUserRequest;
import vn.project.nfc.request.LoginRequest;
import vn.project.nfc.request.RegisterRequest;
import vn.project.nfc.response.CheckUserResponse;
import vn.project.nfc.response.GlobalResponse;
import vn.project.nfc.response.LoginResponse;
import vn.project.nfc.sercurity.impl.UserDetailsImpl;

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

    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Transactional
    public GlobalResponse<Object> registerAccount(RegisterRequest registerRequest) {
        if (!userRepository.findByUuid(registerRequest.getUuid()).isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Vui lòng liên hệ với số điện thoại 0853222490 để được cấp mã thẻ")
                    .data(null)
                    .build();
        }
        if (!userRepository.findByUserNameAndNickName(registerRequest.getUserName(), registerRequest.getNickName()).isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Tên đăng nhập hoặc biệt danh đã tồn tại")
                    .data(null)
                    .build();
        }
        User user = new User();
        user.setNickName(registerRequest.getNickName());
        user.setUserName(registerRequest.getUserName());
        user.setPassWord(passwordEncoderAndDecode.encode(registerRequest.getPassWord()));
        userRepository.save(user);
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(user)
                .build();
    }

    public GlobalResponse<Object> userLogin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassWord()));
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
        CheckUserResponse checkUserResponse = new CheckUserResponse();
        if (userRepository.findByUuid(checkUserRequest.getUuid()).isPresent()) {
            checkUserResponse.setTrangThai(1);
        } else {
            checkUserResponse.setTrangThai(0);
        }
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(checkUserResponse)
                .build();
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

}
