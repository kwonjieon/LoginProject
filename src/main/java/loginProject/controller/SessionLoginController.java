package loginProject.controller;

import loginProject.domain.dto.JoinRequest;
import loginProject.domain.entity.User;
import loginProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class SessionLoginController {
    private final UserService userService;
    @GetMapping(value = {"/",""})
    public String home(Model model, @SessionAttribute(name = "userId",required = false) Long userId){
        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");

        User loginUser = userService.getLoginUser(userId);

        if(loginUser!=null){
            model.addAttribute("nickname",loginUser.getNickname());
        }

        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model){
        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");

        model.addAttribute("joinRequest",new JoinRequest());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest req, BindingResult bindingResult, Model model){

        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");

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
        userService.join(req);
        return "redirect:/session-login";

    }
}
