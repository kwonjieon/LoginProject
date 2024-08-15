package loginProject.service;

import loginProject.domain.dto.JoinForm;
import loginProject.domain.dto.LoginForm;
import loginProject.domain.entity.User;
import loginProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;//requiredArgs로 di 생성자 주입


    //id중복체크
    public boolean checkLogInDuplicate(String loginId){
        return userRepository.existsByLoginId(loginId);
    }
    //닉네임중복체크
    public boolean checkNicknameDuplicate(String nickname){
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 회원가입
     * 화면에서 id,password,nickname입력받아 user로 변환 후 저장
     */
    public void join(JoinForm form){
        userRepository.save(form.toEntity());
    }

    /**
     * 로그인기능
     * 아이디와 비밀번호가 일치하면 user return
     * 일치하지않으면 null return
     */
    public User login(LoginForm form){
        Optional<User> optionalUser = userRepository.findByLoginId(form.getLoginId());

        if(optionalUser.isEmpty())
            return null;

        User user = optionalUser.get();
        if(!user.getPassword().equals(form.getPassword()))
            return null;
        return user;
    }

    /**
     * user Id로 user를 return
     * 인증,인가 시 사용
     *
     */
    public User getLoginUser(Long userId){
        if(userId==null) return null;
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }



}
