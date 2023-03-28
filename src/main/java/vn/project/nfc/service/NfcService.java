package vn.project.nfc.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.project.nfc.model.User;
import vn.project.nfc.repository.UserRepository;
import vn.project.nfc.request.UpdateRequest;
import vn.project.nfc.request.UserRequest;
import vn.project.nfc.response.GlobalResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NfcService {

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    private final UserRepository userRepository;

    private final HttpServletRequest httpServletRequest;

    public GlobalResponse<Object> create(UserRequest userRequest) {
        String userName = this.getUserFromAccessToken();
        if (StringUtils.hasText(userName)) {
            Optional<User> user = userRepository.findByUserName(userName);
            if (user.isPresent()) {
                user.get().setContent(userRequest.getContent());
                userRepository.save(user.get());
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(user.get())
                        .build();
            }
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Username không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        }
        return null;
    }

    public GlobalResponse<Object> update(UpdateRequest updateRequest) {
        String userName = this.getUserFromAccessToken();
        if (StringUtils.hasText(userName)) {
            Optional<User> user = userRepository.findByUserName(userName);
            if (user.isPresent()) {
                user.get().setContent(updateRequest.getContent());
                userRepository.save(user.get());
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(user.get())
                        .build();
            }
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Username không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        }
        return null;
    }

    public GlobalResponse<Object> getUserMyselft() {
        String userName = this.getUserFromAccessToken();
        if (StringUtils.hasText(userName)) {
            Optional<User> user = userRepository.findByUserName(userName);
            if (user.isPresent()) {
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(user.get())
                        .build();
            }
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Username không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        }
        return null;
    }

    private String getUserFromAccessToken() {
        String authorization = httpServletRequest.getHeader("Authorization");
        if (Objects.nonNull(authorization) && authorization.startsWith("Bearer")) {
            String accessToken = authorization.substring(7);
            String encoding = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
            return Jwts.parser().setSigningKey(encoding).parseClaimsJws(accessToken).getBody().getSubject();
        }
        return "";
    }

    public String testAPI() {
        return this.getUserFromAccessToken();

    }
}
