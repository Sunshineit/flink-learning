package com.zhisheng.common.schemas;

import com.google.gson.Gson;
import com.zhisheng.common.model.LogEvent;
import com.zhisheng.common.model.UserLogEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Log Schema ，支持序列化和反序列化
 *
 * blog：http://www.54tianzhisheng.cn/
 * 微信公众号：zhisheng
 *
 */
public class UserLogSchema implements DeserializationSchema<UserLogEvent>, SerializationSchema<UserLogEvent> {

    private static final Gson gson = new Gson();

    @Override
    public UserLogEvent deserialize(byte[] bytes) throws IOException {
        return gson.fromJson(new String(bytes), UserLogEvent.class);
    }

    @Override
    public boolean isEndOfStream(UserLogEvent logEvent) {
        return false;
    }

    @Override
    public byte[] serialize(UserLogEvent logEvent) {
        return gson.toJson(logEvent).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public TypeInformation<UserLogEvent> getProducedType() {
        return TypeInformation.of(UserLogEvent.class);
    }
}
