package loginProject.controller;

import loginProject.domain.entity.User;
import loginProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class SessionLoginController {
    private final UserService userService;

    public String home(Model model, @SessionAttribute(name = "userId",required = false) Long userId){
        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");

        User loginUser = userService.getLoginUser(userId);

        if(loginUser!=null){
            model.addAttribute("nickname",loginUser.getNickname());
        }

        return "home";
    }
}
