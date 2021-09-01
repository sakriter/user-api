package com.user.model;

import java.sql.Date;

public class UserTest {
    public static User user() {
        User user = new User();
        user.setUsername("test1");
        user.setPassword("test1");
        user.setAddress(null);
        user.setPhone("0849991111");
        user.setSalary(15000);
        return user;
    }

    public static User userSalary10000() {
        User user = new User();
        user.setUsername("test1");
        user.setPassword("test1");
        user.setAddress(null);
        user.setPhone("0849991111");
        user.setSalary(10000);
        return user;
    }

    public static User userSalary15000() {
        User user = new User();
        user.setUsername("test1");
        user.setPassword("test1");
        user.setAddress(null);
        user.setPhone("0849991111");
        user.setSalary(15000);
        return user;
    }

    public static User userSalary25000() {
        User user = new User();
        user.setUsername("test2");
        user.setPassword("test2");
        user.setAddress(null);
        user.setPhone("0849992222");
        user.setSalary(25000);
        return user;
    }

    public static User userSalary35000() {
        User user = new User();
        user.setUsername("test3");
        user.setPassword("test3");
        user.setAddress(null);
        user.setPhone("0849993333");
        user.setSalary(35000);
        return user;
    }

    public static User userSalary45000() {
        User user = new User();
        user.setUsername("test4");
        user.setPassword("test4");
        user.setAddress(null);
        user.setPhone("0849994444");
        user.setSalary(45000);
        return user;
    }

    public static User userSalary55000() {
        User user = new User();
        user.setUsername("test5");
        user.setPassword("test5");
        user.setAddress(null);
        user.setPhone("0849995555");
        user.setSalary(55000);
        return user;
    }

    public static User userInfo() {
        User user = new User();
        user.setId(1);
        user.setRefcode("202108311111");
        user.setUsername("test1");
        user.setPassword("$2a$10$wa6VywajQFSJ.55ICn3ZKOC0ogt5ef7JqDo8g5uEAz/L9el4hw.Eu");
        user.setAddress(null);
        user.setPhone("0849991111");
        user.setSalary(15000);
        user.setMembertype("Silver");
        user.setRegisterdate(null);
        return user;
    }
}
