package vn.project.nfc.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.project.nfc.model.User;
import vn.project.nfc.repository.UserRepository;
import vn.project.nfc.request.UpdateRequest;
import vn.project.nfc.request.UserRequest;
import vn.project.nfc.response.GlobalResponse;
import vn.project.nfc.response.GlobalUserResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
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
        String email = this.getEmailFromAccessToken();
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (StringUtils.hasText(email)) {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                if (StringUtils.hasText(userRequest.getContent())) {
                    user.get().setContent(userRequest.getContent());
                }
                if (StringUtils.hasText(userRequest.getAvatar())) {
                    user.get().setAvatar(userRequest.getAvatar());
                }
                if (StringUtils.hasText(userRequest.getNickName())) {
                    user.get().setNickName(userRequest.getNickName());
                }
                if (StringUtils.hasText(userRequest.getDescription())) {
                    user.get().setDescription(userRequest.getDescription());
                }
                if (StringUtils.hasText(userRequest.getUserName())) {
                    user.get().setUserName(userRequest.getUserName());
                }
                userRepository.save(user.get());
                BeanUtils.copyProperties(user.get(), globalUserResponse);
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(globalUserResponse)
                        .build();
            }
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        }
        return null;
    }

    public GlobalResponse<Object> update(UpdateRequest updateRequest) {
        String email = this.getEmailFromAccessToken();
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (StringUtils.hasText(email)) {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                if (StringUtils.hasText(updateRequest.getContent())) {
                    user.get().setContent(updateRequest.getContent());
                }
                if (StringUtils.hasText(updateRequest.getAvatar())) {
                    user.get().setAvatar(updateRequest.getAvatar());
                }
                if (StringUtils.hasText(updateRequest.getNickName())) {
                    Optional<User> userNickName = userRepository.findByNickName(updateRequest.getNickName());
                    if (userNickName.isPresent()) {
                        if (userNickName.get().getEmail().equals(email)) {
                            user.get().setNickName(updateRequest.getNickName());
                        } else {
                            return GlobalResponse.builder()
                                    .status(HttpStatus.BAD_REQUEST.value())
                                    .message("NickName đã tồn tại trên hệ thống")
                                    .data(null)
                                    .build();
                        }
                    } else {
                        user.get().setNickName(updateRequest.getNickName());
                    }
                }
                user.get().setUserName(updateRequest.getUserName());
                user.get().setDescription(updateRequest.getDescription());
                user.get().setTemplates(updateRequest.getTemplates());
                user.get().setUpdateAt(new Date());
                userRepository.save(user.get());
                BeanUtils.copyProperties(user.get(), globalUserResponse);
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(globalUserResponse)
                        .build();
            }
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Email không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        }
        return null;
    }

    public GlobalResponse<Object> getUserMyselft() {
        String email = this.getEmailFromAccessToken();
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (StringUtils.hasText(email)) {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                user.get().setUpdateAt(new Date());
                userRepository.save(user.get());
                BeanUtils.copyProperties(user.get(), globalUserResponse);
                return GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Thành công")
                        .data(globalUserResponse)
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

    private String getEmailFromAccessToken() {
        String authorization = httpServletRequest.getHeader("Authorization");
        if (Objects.nonNull(authorization) && authorization.startsWith("Bearer")) {
            String accessToken = authorization.substring(7);
            String encoding = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
            return Jwts.parser().setSigningKey(encoding).parseClaimsJws(accessToken).getBody().getSubject();
        }
        return "";
    }

    public String testAPI() {
        return this.getEmailFromAccessToken();

    }
}
