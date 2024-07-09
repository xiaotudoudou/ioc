package springioc.entity;


import springioc.stereotype.Autowired;
import springioc.stereotype.Component;

@Component
public class TestController {

    @Autowired
    private UserService userService;


    public void test(){
        userService.addEmp("张三","讲师");
    }
}
