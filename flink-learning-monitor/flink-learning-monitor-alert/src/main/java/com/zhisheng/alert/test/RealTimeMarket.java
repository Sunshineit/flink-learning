package com.zhisheng.alert.test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.zhisheng.common.model.ActivityEvent;
import com.zhisheng.common.model.UserLogEvent;
import com.zhisheng.common.schemas.ActivitySchema;
import com.zhisheng.common.schemas.UserLogSchema;
import com.zhisheng.common.utils.ExecutionEnvUtil;
import com.zhisheng.common.utils.KafkaConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Maps;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;

import java.util.Map;
import java.util.Properties;

/**
 * Desc: 利用广播变量动态更新客群规则
 * 实时营销Demo
 */
@Slf4j
public class RealTimeMarket {
    private final static MapStateDescriptor<Long, ActivityEvent> ACTIVITY_RULE = new MapStateDescriptor<>(
            "activity_rule",
            BasicTypeInfo.LONG_TYPE_INFO,
            TypeInformation.of(ActivityEvent.class));

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(args);
        StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

        Properties properties = KafkaConfigUtil.buildKafkaProps(parameterTool);
        FlinkKafkaConsumer011<UserLogEvent> userLongData = new FlinkKafkaConsumer011<>(
                "test-message",
                new UserLogSchema(),
                properties);


        SingleOutputStreamOperator<UserLogEvent> userLogStream = env.addSource(userLongData);

        FlinkKafkaConsumer011<ActivityEvent> activityData = new FlinkKafkaConsumer011<>(
                "market-config",
                new ActivitySchema(),
                properties);


        BroadcastStream<ActivityEvent> broadcastStream = env.addSource(activityData).map(new RichMapFunction<ActivityEvent, ActivityEvent>() {
            @Override
            public ActivityEvent map(ActivityEvent custGroupEvent) {
                String str = " && message.sendTime > " + custGroupEvent.getStartTime() + " && message.sendTime < " + custGroupEvent.getEndTime();
                custGroupEvent.setExpression(custGroupEvent.getExpression() + str);
                System.out.println(custGroupEvent.getExpression());
                return custGroupEvent;
            }
        }).setParallelism(1).broadcast(ACTIVITY_RULE);

        userLogStream.connect(broadcastStream)
                .process(new BroadcastProcessFunction<UserLogEvent, ActivityEvent, UserLogEvent>() {

                    private final Map<String, Object> mapMessage = Maps.newHashMap();

                    @Override
                    public void open(Configuration cfg) throws Exception {
                        super.open(cfg);

//                        MapState<Long, ActivityEvent> mapState = getRuntimeContext().getMapState(ACTIVITY_RULE);

//                        ActivityEvent activityEvent = new ActivityEvent();
//                        activityEvent.setId(1L);
//                        activityEvent.setExpression("初始化规则");
//                        activityEvent.setStartTime(1593759534000L);
//                        activityEvent.setEndTime(1594018734000L);
//                        activityEvent.setExpression("message.businessType == '你我贷核批' && message.reloan == '1' && message.auditResult == 'PASS'");
//                        mapState.put(activityEvent.getId(), activityEvent);
                    }

                    @Override
                    public void processElement(UserLogEvent event, ReadOnlyContext ctx, Collector<UserLogEvent> out) throws Exception {
                        ReadOnlyBroadcastState<Long, ActivityEvent> broadcastState = ctx.getBroadcastState(ACTIVITY_RULE);
                        Iterable<Map.Entry<Long, ActivityEvent>> entries = broadcastState.immutableEntries();
                        for (Map.Entry<Long, ActivityEvent> entry : entries) {
                            mapMessage.put("message", event);
                            Expression expression = AviatorEvaluator.getInstance().getCachedExpressionByKey(entry.getKey().toString());
                            Boolean execute = (Boolean) expression.execute(mapMessage);
                            if (execute) {
                                event.setActivity_id(entry.getKey());
                                out.collect(event);
                            }
                        }
                    }

                    @Override
                    public void processBroadcastElement(ActivityEvent event, Context ctx, Collector<UserLogEvent> out) throws Exception {
                        if (event == null) {
                            return;
                        }
                        BroadcastState<Long, ActivityEvent> broadcastState = ctx.getBroadcastState(ACTIVITY_RULE);
                        if (ActivityEvent.RUNNING.equals(event.getStatus())) {
                            broadcastState.remove(event.getId());
                            AviatorEvaluator.getInstance().invalidateCacheByKey(event.getId().toString());
                            AviatorEvaluator.getInstance().compile(event.getId().toString(), event.getExpression(), true);
                            broadcastState.put(event.getId(), event);
                        } else {
                            broadcastState.remove(event.getId());
                            AviatorEvaluator.getInstance().invalidateCacheByKey(event.getId().toString());
                        }
                    }
                }).print();

        env.execute();
    }
}
