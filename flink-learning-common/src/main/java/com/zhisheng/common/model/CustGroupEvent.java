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
public class CustGroupEvent {

    //活动id
    private Long id;
    //活动名称
    private String name;
    //生效开始时间
    private Long startTime;
    //生效结束时间
    private Long endTime;
    //表达式
    private String expression;

}