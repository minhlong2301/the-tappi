package vn.project.nfc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

    private final JavaMailSender javaMailSender;

    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Transactional
    public GlobalResponse<Object> registerAccount(RegisterRequest registerRequest) throws MessagingException {
        Optional<User> user = userRepository.findByUuid(registerRequest.getUuid());
        if (!user.isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Bạn chưa có mã thẻ để đăng kí tài khoản")
                    .data(null)
                    .build();
        }
        if (StringUtils.hasText(user.get().getEmail())) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Thẻ này đã được đăng kí")
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
        String content = "<h3>Xin chào " + registerRequest.getNickName() + "</h3>" +
                "<p>LIAM xin gửi lời cảm ơn chân thành đến " + registerRequest.getNickName() + " vì bạn tin tưởng và lựa chọn sản phẩm của chúng mình. LIAM rất mong rằng thẻ cá nhân mà bạn đã lựa chọn sẽ đem lại cho bạn một trải nghiệm tuyệt vời cùng người thương, gia đình và bạn bè!\n" +
                "Nếu " + registerRequest.getNickName() + " có bất kỳ thắc mắc hoặc góp ý nào xin đừng ngại ngần liên hệ với LIAM, chúng mình sẽ luôn luôn sẵn sàng hỗ trợ bạn</p>" +
                "\n" +
                "<p>Một lần nữa, LIAM xin chân thành cảm ơn bạn vì đã tin tưởng và ủng hộ sản phẩm của chúng mình. Chúc bạn một ngày tốt lành!</p>" +
                "\n" +
                "<p> Trân trọng </p>" +
                "<p>LIAM</p>" +
                "<p>-------------</p>" +
                "<p>Thông tin liên hệ</p>" +
                "<a href=\"https://www.google.com/\">Facebook</a>" +
                "<a href=\"https://www.google.com/\">Instagram</a>" +
                "<a href=\"https://www.google.com/\">Tiktok</a>" +
                "<p>Hotline: 0364688581</p>";
        String subject = "Cảm ơn bạn đã mua hàng của chúng tôi";
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom("info.liamcompany@gmail.com");
        message.setRecipients(MimeMessage.RecipientType.TO, registerRequest.getEmail());
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");
        javaMailSender.send(message);
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(null)
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
        GlobalUserResponse globalUserResponse = new GlobalUserResponse();
        if (!user.isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Nickname không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        } else {
            BeanUtils.copyProperties(user.get(), globalUserResponse);
            return GlobalResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Thành công")
                    .data(globalUserResponse)
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

    public GlobalResponse<Object> generateUuidAndUrl() {
        String uuid;
        for (int i = 0; i < 201; i++) {
            User user = new User();
            user.setUuid(UUID.randomUUID().toString());
            uuid = user.getUuid();
            user.setUrl("http://liamtap.site/" + uuid);
            userRepository.save(user);
        }
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(null)
                .build();
    }

}
