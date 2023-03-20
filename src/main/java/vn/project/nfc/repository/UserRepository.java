package vn.project.nfc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.project.nfc.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);
}
