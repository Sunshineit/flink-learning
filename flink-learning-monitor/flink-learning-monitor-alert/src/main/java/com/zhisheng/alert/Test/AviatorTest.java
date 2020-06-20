package com.zhisheng.alert.Test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.HashMap;

public class AviatorTest {

    public static void main(String[] args) {
        Message message = new Message(1111l, 0, 20, "zhangs");


        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);

        MarketRule marketRule = new MarketRule(1l, "message.age > 18");
        Expression compile = AviatorEvaluator.getInstance().compile(marketRule.getId().toString(), marketRule.getExprsession(), true);
        Boolean execute = (Boolean)compile.execute(map);
        System.out.println(execute);

    }
}

