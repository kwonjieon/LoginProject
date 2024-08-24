package loginProject.controller;

import loginProject.domain.dto.JoinRequest;
import loginProject.domain.dto.LoginRequest;
import loginProject.domain.entity.User;
import loginProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/security-login")
public class SecurityLoginController {

    private final UserService userService;

    @GetMapping({"","/"})
    public String home(Model model, Authentication auth){
        model.addAttribute("loginType","security-login");
        model.addAttribute("pageName","Security 로그인");

        if(auth!=null){
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if(loginUser!=null){
                model.addAttribute("nickname",loginUser.getNickname());
            }
        }
        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model){
        model.addAttribute("loginType","security-login");
        model.addAttribute("pageName","Security 로그인");

        model.addAttribute("joinRequest",new JoinRequest());
        return "join";
    }
    @PostMapping("/join")
    public String join(@Valid@ModelAttribute JoinRequest req, BindingResult bindingResult, Model model){
        model.addAttribute("loginType","security-login");
        model.addAttribute("pageName","Security 로그인");

        if(userService.checkLogInDuplicate(req.getLoginId())){
            bindingResult.addError(new FieldError("joinRequest","loginId","로그인 아이디가 중복됩니다."));
        }
        if(userService.checkNicknameDuplicate(req.getNickname())){
            bindingResult.addError(new FieldError("joinRequest","nickname","닉네임이 중복됩니다."));
        }
        if(!req.getPassword().equals(req.getPasswordCheck())){
            bindingResult.addError(new FieldError("joinRequest","passwordCheck","비밀번호가 일치하지 않습니다."));
        }
        if(bindingResult.hasErrors()){
            return "join";
        }
        userService.bcryptJoin(req);
        return "redirect:/security-login";
    }
    @GetMapping("/login")
    public String loginPage(Model model){
        model.addAttribute("loginType","security-login");
        model.addAttribute("pageName","Security 로그인");

        model.addAttribute("loginRequest",new LoginRequest());
        return "login";
    }

    @GetMapping("/info")
    public String userInfo(Model model,Authentication auth){
        model.addAttribute("loginType","security-login");
        model.addAttribute("pageName","Security 로그인");

        User loginUser = userService.getLoginUserByLoginId(auth.getName());
        model.addAttribute("user",loginUser);
        return "info";

    }
    @GetMapping("/admin")
    public String adminPage(Model model){
        model.addAttribute("loginType","security-login");
        model.addAttribute("pageName","Security 로그인");

        return "admin";
    }
}
