package springioc.entity;

import springioc.stereotype.Component;

@Component
public class UserService {

    public void addEmp(String name, String position){
        System.out.println(name);
        System.out.println(position);
    }
}
