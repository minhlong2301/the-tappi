package vn.project.nfc.sercurity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.project.nfc.model.User;
import vn.project.nfc.repository.UserRepository;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("Tên đăng nhập không tìm thấy " + userName));
        log.info("User " + user);
        return new UserDetailsImpl(user);
    }
}
