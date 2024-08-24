package loginProject.controller;

import loginProject.domain.UserRole;
import loginProject.domain.dto.JoinRequest;
import loginProject.domain.dto.LoginRequest;
import loginProject.domain.entity.User;
import loginProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class SessionLoginController {
    private final UserService userService;
    @GetMapping(value = {"/",""})
    public String home(Model model, @SessionAttribute(name = "userId",required = false) Long userId){
        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");

        User loginUser = userService.getLoginUserById(userId);

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

    @GetMapping("/login")
    public String loginPage(Model model){
        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");
        model.addAttribute("loginRequest",new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest req , BindingResult bindingResult, HttpServletRequest httpServletRequest,Model model){

        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");

        User user = userService.login(req);

        if(user==null){
            bindingResult.reject("loginFail","로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if(bindingResult.hasErrors()){
            return "login";
        }

        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("userId",user.getId());
        session.setMaxInactiveInterval(1800);

        sessionList.put(session.getId(),session);

        return "redirect:/session-login";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest req,Model model){
        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");
        HttpSession session = req.getSession(false);
        if(session!=null){
            sessionList.remove(session.getId());
            session.invalidate();
        }
        return "redirect:/session-login";
    }

    @GetMapping("/info")
    public String userInfo(@SessionAttribute(name = "userId",required = false) Long userId,Model model){

        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");

        User loginUser = userService.getLoginUserById(userId);
        if(loginUser==null){
            return "redirect:/session-login/login";
        }
        model.addAttribute("user",loginUser);
        return "info";
    }
    @GetMapping("/admin")
    public String adminPage(@SessionAttribute(name = "userId",required = false) Long userId, Model model){

        model.addAttribute("loginType","session-login");
        model.addAttribute("pageName","세션 로그인");
        User loginUser = userService.getLoginUserById(userId);
        if(loginUser==null){
            return "redirect:/session-login/login";
        }
        if(!loginUser.getRole().equals(UserRole.ADMIN)){
            return "redirect:/session-login/login";
        }
        return "admin";
    }


    //세션리스트확인
    public static Hashtable sessionList = new Hashtable();

    @GetMapping("/session-list")
    @ResponseBody
    public Map<String,String> sessionList(){
        Enumeration elements = sessionList.elements();
        Map<String,String> lists = new HashMap<>();
        while(elements.hasMoreElements()){
            HttpSession session = (HttpSession)elements.nextElement();
            lists.put(session.getId(),String.valueOf(session.getAttribute("userId")));
        }
        return lists;
    }


}
