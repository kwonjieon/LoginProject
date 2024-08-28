package loginProject.controller;

import loginProject.auth.JwtTokenUtil;
import loginProject.domain.dto.JoinRequest;
import loginProject.domain.dto.LoginRequest;
import loginProject.domain.entity.User;
import loginProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt-api-login")
public class JwtLoginApiController {
    private final UserService userService;

    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest){

        //loginId중복체크
        if(userService.checkLogInDuplicate(joinRequest.getLoginId())){
            return "로그인 아이디가 중복됩니다.";
        }
        //닉네임 중복체크
        if(userService.checkNicknameDuplicate(joinRequest.getNickname())){
            return "닉네임이 중복됩니다.";
        }
        //패스워드 일치확인
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())){
            return "비밀번호가 일치하지 않습니다.";
        }
        userService.bcryptJoin(joinRequest);
        return "회원가입성공";
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){
        User user = userService.login(loginRequest);
        //로그인 실패시
        if(user==null){
            return "로그인 아이디 또는 비밀번호가 틀렸습니다.";
        }
        //로그인성공 => jwt token발급

        String secretKey = "my-secret-key-123123";
        long expiredTimeMs = 1000*60*60;//토큰유효기간 60분

        String jwtToken = JwtTokenUtil.createToken(user.getLoginId(),secretKey,expiredTimeMs);

        return jwtToken;

    }
    @GetMapping("/info")
    public String userInfo(Authentication auth){
        User loginUser = userService.getLoginUserByLoginId(auth.getName());
        return String.format("loginId: %s\nnickname : %s\nrole : %s",
                loginUser.getLoginId(),loginUser.getNickname(),loginUser.getRole().name());
    }
    @GetMapping("/admin")
    public String adminPage(){
        return "관리자 페이지 접근 성공";
    }
}
