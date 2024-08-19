package loginProject.controller;

import loginProject.domain.UserRole;
import loginProject.domain.dto.JoinRequest;
import loginProject.domain.dto.LoginRequest;
import loginProject.domain.entity.User;
import loginProject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cookie-login")
public class CookieLoginController {
    private final UserService userService;
    @GetMapping(value = {"","/"})
    public String home(@CookieValue(name="userId",required = false) Long userId, Model model){
        User loginUser = userService.getLoginUser(userId);

        if(loginUser !=null){
            model.addAttribute("nickname", loginUser.getNickname());
            log.info("[HOME] Login User Nickname : {}", loginUser.getNickname());
        }
        else{
            log.info("[HOME] Not Login User");
        }

        return "home";
    }
    @GetMapping("/join")
    public String joinPage(Model model){
        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest req, BindingResult bindingResult){
        //loginid중복체크
        if(userService.checkLogInDuplicate(req.getLoginId())){
            bindingResult.addError(new FieldError("joinRequest","loginId","아이디가 중복됩니다."));
        }
        //닉네임중복체크
        if(userService.checkNicknameDuplicate(req.getNickname())){
            bindingResult.addError(new FieldError("joinRequest","nickname","닉네임이 중복됩니다."));
        }
        //비밀번호 일치확인체크
        if(!req.getPassword().equals(req.getPasswordCheck())){
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck","비밀번호가 일치하지 않습니다."));
        }

        if(bindingResult.hasErrors()){
            log.info("[JOIN] 회원가입 실패");
            return "join";
        }

        userService.join(req);
        log.info("[JOIN] 회원가입 성공");
        return "redirect:/cookie-login";
    }
    @GetMapping("/login")
    public String loginPage(Model model){
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest req, BindingResult bindingResult, HttpServletResponse response){
        User user = userService.login(req);

        if(user==null){
            bindingResult.reject("loginFail","로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if(bindingResult.hasErrors()){
            log.info("[LOGIN] 로그인 실패");
            System.out.println(bindingResult.getAllErrors());
            return "login";
        }
        Cookie cookie = new Cookie("userId",String.valueOf(user.getId()));
        cookie.setMaxAge(60*60);
        response.addCookie(cookie);

        log.info("[LOGIN] 로그인 성공 : {}",user.getNickname());
        return "redirect:/cookie-login";

    }
    @GetMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("userId",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info("[LOGOUT] 로그아웃 성공");
        return "redirect:/cookie-login";
    }
    @GetMapping("/info")
    public String userInfo(@CookieValue(name="userId",required = false) Long userId, Model model){
        User loginUser = userService.getLoginUser(userId);

        if(loginUser==null){
            return "redirect:/cookie-login/login";
        }
        model.addAttribute("user",loginUser);
        return "info";
    }
    @GetMapping("/admin")
    public String adminPage(@CookieValue(name="userId",required = false) Long userId){
        User loginUser = userService.getLoginUser(userId);
        if(loginUser==null){
            return "redirect:/cookie-login/login";
        }
        if(!loginUser.getRole().equals(UserRole.ADMIN)){
            return "redirect:/cookie-login";
        }
        return "admin";
    }


}

