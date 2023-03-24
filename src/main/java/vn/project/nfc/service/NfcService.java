package vn.project.nfc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.project.nfc.model.User;
import vn.project.nfc.repository.UserRepository;
import vn.project.nfc.request.UserRequest;
import vn.project.nfc.response.GlobalResponse;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NfcService {

    private final UserRepository userRepository;

    public GlobalResponse<Object> create(UserRequest userRequest) {
        if (userRepository.findByUuid(userRequest.getUuid()).isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Uuid đã tồn tại trên hệ thống")
                    .data(null)
                    .build();
        }
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        user.setCreateAt(new Date());
        user.setDomain("cash-profile.surge.sh?uuid=" + user.getUuid());
        userRepository.save(user);
        return GlobalResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Thành công")
                .data(user)
                .build();
    }

    public GlobalResponse<Object> getUserFromUUID(String uuid) {
        Optional<User> optionalUser = userRepository.findByUuid(uuid);
        if (optionalUser.isPresent()) {
            return GlobalResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Thành công")
                    .data(optionalUser.get())
                    .build();
        } else {
            return GlobalResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Uuid không tồn tại trên hệ thống")
                    .data(null)
                    .build();
        }
    }

    public String testAPI() {
        return "Thành công";
    }
}
