package com.zhisheng.alert.test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.zhisheng.common.model.LogEvent;
import com.zhisheng.common.model.MarketRuleEvent;
import com.zhisheng.common.schemas.LogSchema;
import com.zhisheng.common.schemas.MarketRuleSchema;
import com.zhisheng.common.utils.ExecutionEnvUtil;
import com.zhisheng.common.utils.KafkaConfigUtil;
import lombok.extern.slf4j.Slf4j;
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
public class BroadcastUpdateAlertRule {
    private final static MapStateDescriptor<Long, MarketRuleEvent> ALERT_RULE = new MapStateDescriptor<>(
            "alert_rule",
            BasicTypeInfo.LONG_TYPE_INFO,
            TypeInformation.of(MarketRuleEvent.class));

    public static void main(String[] args) throws Exception {
        final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(args);
        StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

        Properties properties = KafkaConfigUtil.buildKafkaProps(parameterTool);
        FlinkKafkaConsumer011<LogEvent> consumer = new FlinkKafkaConsumer011<>(
                "test-message",
                new LogSchema(),
                properties);


        SingleOutputStreamOperator<LogEvent> machineData = env.addSource(consumer);

        FlinkKafkaConsumer011<MarketRuleEvent> marketRuleData = new FlinkKafkaConsumer011<>(
                "market-config",
                new MarketRuleSchema(),
                properties);

        BroadcastStream<MarketRuleEvent> broadcastStream = env.addSource(marketRuleData).setParallelism(1).broadcast(ALERT_RULE);

        machineData.connect(broadcastStream)
                .process(new BroadcastProcessFunction<LogEvent, MarketRuleEvent, LogEvent>() {

                    private final Map<Long, Expression> mapRule = Maps.newHashMap();

                    private final Map<String, Object> mapMessage = Maps.newHashMap();

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                    }

                    @Override
                    public void processElement(LogEvent value, ReadOnlyContext ctx, Collector<LogEvent> out) throws Exception {
                        ReadOnlyBroadcastState<Long, MarketRuleEvent> broadcastState = ctx.getBroadcastState(ALERT_RULE);
                        Iterable<Map.Entry<Long, MarketRuleEvent>> entries = broadcastState.immutableEntries();
                        for (Map.Entry<Long, MarketRuleEvent> entry : entries) {
                            mapMessage.put("message", value);
                            Expression expression = mapRule.get(entry.getKey());
                            Boolean execute = (Boolean) expression.execute(mapMessage);
                            if (execute) {
                                out.collect(value);
                            }
                        }
                    }

                    @Override
                    public void processBroadcastElement(MarketRuleEvent value, Context ctx, Collector<LogEvent> out) throws Exception {
                        if (value == null) {
                            return;
                        }
                        BroadcastState<Long, MarketRuleEvent> broadcastState = ctx.getBroadcastState(ALERT_RULE);
                        Expression compile = AviatorEvaluator.getInstance().compile(value.getId().toString(), value.getExprsession(), false);
                        mapRule.put(value.getId(), compile);
                        broadcastState.put(value.getId(), value);

                    }
                }).print();

        env.execute();
    }
}
