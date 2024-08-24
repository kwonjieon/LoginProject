package loginProject;

import loginProject.domain.UserRole;
import loginProject.domain.entity.User;
import loginProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class MakeInitData {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    @PostConstruct
    public void makeAdminAndUser(){
        User admin = User.builder()
                .loginId("admin")
                .password(encoder.encode("1234"))
                .nickname("관리자")
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .loginId("user")
                .password(encoder.encode("1234"))
                .nickname("유저1")
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

    }
}
