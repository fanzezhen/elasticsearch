package cn.fzz.elasticsearch.web;

import cn.fzz.elasticsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/count")
    public String count(){
        return String.valueOf(userService.count());
    }
}
