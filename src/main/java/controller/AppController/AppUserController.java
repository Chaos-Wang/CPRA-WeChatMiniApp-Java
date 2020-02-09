package main.java.controller.controller.AppController;

/**
 * @author ChaosWong
 * @date 2020/1/14 11:35
 * @title
 */

import main.java.bean.User;
import main.java.controller.EntityController.UserController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/user")

public class AppUserController {

    @GetMapping("/login")
    public String login( String userName, String password ) {
        return UserController.loginWithPwd( password, userName );
    }
    @GetMapping("/info")
    public String info( String userName ) {
        return UserController.getUserInfoWithUserName( userName );
    }
    @GetMapping("/password")
    public String password( String oldPassword, String newPassword, String userName ) {
        User u =UserController.getUserObject( userName );
        if( UserController.changePasswordByUserName( oldPassword, newPassword, userName ) ) {
          return "finished";
        } else {
          return "something wrong";
        }

    }
    @GetMapping("/deviceId")
    public String deviceId( String userName ){
        return UserController.getDeviceIdWithUserName( userName );
    }

}
