package com.zhisheng.alert.test;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class DateFun extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {

        Long startTime = FunctionUtils.getNumberValue(arg1, env).longValue();
        Long endTime = FunctionUtils.getNumberValue(arg2, env).longValue();
        Long date = FunctionUtils.getNumberValue(arg3, env).longValue();


        return super.call(env, arg1, arg2, arg3);
    }

    @Override
    public String getName() {
        return null;
    }
}
