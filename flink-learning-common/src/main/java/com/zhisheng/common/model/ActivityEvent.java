package com.zhisheng.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityEvent {

    public final static Integer DRAFT = 0;
    public final static Integer COMMITED = 1;
    public final static Integer APPROVAL_FAIL = 3;
    public final static Integer WAIT_RUN = 4;
    public final static Integer RUNNING = 6;
    public final static Integer SUSPEND = 7;
    public final static Integer FINISH = 8;

    //活动id
    private Long id;
    //活动名称
    private String name;
    //生效开始时间
    private Long startTime;
    //生效结束时间
    private Long endTime;
    //状态  "草稿"0; 1, "待审批";3, "审批未通过"; 4, "待运行";6, "运行中"; 7, "已暂停";8, "已完成"
    private Integer status;
    //表达式
    private String expression;


}