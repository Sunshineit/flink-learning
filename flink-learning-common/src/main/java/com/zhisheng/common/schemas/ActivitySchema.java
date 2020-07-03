package com.zhisheng.common.schemas;

import com.google.gson.Gson;
import com.zhisheng.common.model.ActivityEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Metric Schema ，支持序列化和反序列化
 *
 * blog：http://www.54tianzhisheng.cn/
 * 微信公众号：zhisheng
 *
 */
public class ActivitySchema implements DeserializationSchema<ActivityEvent>, SerializationSchema<ActivityEvent> {

    private static final Gson gson = new Gson();

    @Override
    public ActivityEvent deserialize(byte[] bytes) throws IOException {
        return gson.fromJson(new String(bytes), ActivityEvent.class);
    }

    @Override
    public boolean isEndOfStream(ActivityEvent metricEvent) {
        return false;
    }

    @Override
    public byte[] serialize(ActivityEvent metricEvent) {
        return gson.toJson(metricEvent).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public TypeInformation<ActivityEvent> getProducedType() {
        return TypeInformation.of(ActivityEvent.class);
    }
}
