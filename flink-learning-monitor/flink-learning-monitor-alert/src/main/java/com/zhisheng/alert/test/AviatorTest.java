package com.zhisheng.alert.test;

import com.google.common.collect.Maps;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.zhisheng.common.model.LogEvent;

import java.util.HashMap;

public class AviatorTest {

    public static void main(String[] args) {
        HashMap<String, Object> map = Maps.newHashMap();
        LogEvent logEvent = new LogEvent();
        logEvent.setLevel("1");
        logEvent.setType("aa");
        map.put("message", logEvent);
        Expression compile = AviatorEvaluator.getInstance().compile("1", "message.type == 'be' ", true);
        Boolean execute = (Boolean) compile.execute(map);
        System.out.println(execute);

    }
}
