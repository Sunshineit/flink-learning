package com.zhisheng.alert.test;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.zhisheng.common.model.ActivityEvent;
import com.zhisheng.common.model.LogEvent;
import com.zhisheng.common.model.UserLogEvent;

import java.sql.SQLOutput;
import java.util.HashMap;

public class AviatorTest {

    public static void main(String[] args) {
        HashMap<String, Object> map = Maps.newHashMap();
        LogEvent logEvent = new LogEvent();
        logEvent.setLevel("32525235254525255245245");
        logEvent.setType("aa");
        logEvent.setType("2020-07-02 00:00:00");
        map.put("message", logEvent);
        Expression compile = AviatorEvaluator.getInstance().compile("1", " message.type > '2020-07-02 00:00:00'", true);
        Boolean execute = (Boolean) compile.execute(map);
        System.out.println(execute);
        String aa = "/(?:\\d+[^\\d\\r\\n]+){3}(\\d+).*\\b/";

        Expression compile2 = AviatorEvaluator.getInstance().compile("2", "message.level =~ " + aa, true);
        Boolean execute1 = (Boolean) compile2.execute(map);
        System.out.println(execute1);

        UserLogEvent userLogEvent = new UserLogEvent();
        userLogEvent.setMid(12332323877909L);
        userLogEvent.setApproveAmount("1231.232");
        userLogEvent.setApproveTime("2020-07-03 12:21:09");
        userLogEvent.setBusinessType("你我贷核批");
        userLogEvent.setMbPhone("Essdfffsefsfe");
        userLogEvent.setRandNum("122323289669623916312396369283691696391969");
        userLogEvent.setReloan("0");
        userLogEvent.setIsInformationFlow("0");
        userLogEvent.setRealName("张三");
        userLogEvent.setSendTime(1593937710000L);

        System.out.println(JSONObject.toJSON(userLogEvent));

        ActivityEvent activityEvent = new ActivityEvent();
        activityEvent.setName("测试规则1");
        activityEvent.setEndTime(1594369374000L);
        activityEvent.setStartTime(1593937374000L);
        activityEvent.setExpression("message.businessType == '你我贷核批' && message.reloan == '0' ");
        activityEvent.setId(1L);
        activityEvent.setStatus(6);
        System.out.println(JSONObject.toJSON(activityEvent));


    }
}
