package com.smec.springboothello.web;

import com.smec.springboothello.entity.User;
import com.smec.springboothello.service.UserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Controller
public class UserController {

    public static final String KEY_USER = "__user__";

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleUnknowException(Exception ex){
        return new ModelAndView("500.html", Map.of("error",ex.getClass().getSimpleName(),"message",ex.getMessage()));
    }

    @GetMapping("/")
    public ModelAndView index(HttpSession session){
        User user = (User) session.getAttribute(KEY_USER);
        Map<String,Object> model = new HashMap<>();
        if(null != user){
            model.put("user",user);
        }
        return new ModelAndView("index.html",model);
    }

    @GetMapping("/register")
    public ModelAndView register(){return  new ModelAndView("register.html");}

    @PostMapping("/register")
    public ModelAndView doRegister(@RequestParam("email") String email,@RequestParam("password") String password,
                                   @RequestParam("name") String name,@RequestParam("imageUrl") String imageUrl){
        try {
            User user = userService.register(email,password,name,imageUrl);
            logger.info("user registered: {}",user.getEmail());
        }catch (RuntimeException e){
            logger.info("user register failed : {}",e);
            return new ModelAndView("register.html",Map.of("email,",email,"error","Register failed"));
        }
        return new ModelAndView("redirect:/signin");
    }

    @GetMapping("/signin")
    public ModelAndView signin(HttpSession session){
        User user = (User) session.getAttribute(KEY_USER);
        if(user!=null){
            return new ModelAndView("redirect:/profile");
        }
        return new ModelAndView("signin.html");
    }

    @PostMapping("/signin")
    public ModelAndView doSignin(@RequestParam("email") String email,@RequestParam("password") String password,
                                 HttpSession session){
        try {
            User user= userService.signin(email,password);
            session.setAttribute(KEY_USER,user);
        }catch (RuntimeException e){
            logger.info(e.getClass().getSimpleName()+"  "+e.getMessage());
            return new ModelAndView("signin.html",Map.of("email",email,"error","Signin failed"));
        }
        return new ModelAndView("redirect:/profile");
    }

    @GetMapping("/profile")
    public ModelAndView profile(HttpSession session){
        User user = (User) session.getAttribute(KEY_USER);
        if(user == null){
            return  new ModelAndView("redirect:/signin");
        }
        return new ModelAndView("profile.html",Map.of("user",user));
    }

    @GetMapping("/signout")
    public String signout(HttpSession session){
        session.removeAttribute(KEY_USER);
        return "redirect:/signin";
    }

    @GetMapping("/resetPassword")
    public ModelAndView resetPassword(HttpSession session){
        User user = (User) session.getAttribute(KEY_USER);
        if(user == null){
            return  new ModelAndView("redirect:/signin");
        }
        return new ModelAndView("resetPassword.html",Map.of("user",user));
    }

    @PostMapping("/resetPassword")
    public ModelAndView doResetPassword(@RequestParam("password") String password,HttpSession session){
        User user = (User) session.getAttribute(KEY_USER);
        try {
            user.setPassword(password);
            userService.updateUser(user);
        }catch( RuntimeException e){
            return new ModelAndView("resetPassword.html",Map.of("user",user,"error","ResetPassword failed"));
        }


        session.removeAttribute(KEY_USER);
        session.setAttribute(KEY_USER,user);
        return new ModelAndView("profile.html",Map.of("user",user));
    }
}
