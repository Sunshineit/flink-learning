package com.zhisheng.alert.Test;

import lombok.Data;

@Data
public class Message {
    private Long mid;

    private Integer sex;


    private Integer age ;

    private String name;

    public Message(Long mid, Integer sex, Integer age, String name) {
        this.mid = mid;
        this.sex = sex;
        this.age = age;
        this.name = name;
    }
}
