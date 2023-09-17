package vn.project.nfc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.project.nfc.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrNickName(String userName, String nickName);

    Optional<User> findByUuid(String uuid);

    Optional<User> findByNickName(String nickName);

    List<User> findByEmailIsNull();

}
