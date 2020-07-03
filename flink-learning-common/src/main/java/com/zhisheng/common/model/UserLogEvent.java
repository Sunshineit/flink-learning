package com.zhisheng.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLogEvent {

    private Long mid;
    //随机数
    private String randNum;
    //用户名
    private String userName;
    //手机号
    private String mbPhone;
    //姓名
    private String realName;
    // 是否首贷	1是，0否
    private String reloan;
    //是否信息流用户
    private String isInformationFlow;
    //审批结果PASS,STOP,REFUSE
    private String auditResult;
    //核批金额
    private String approveAmount;
    //即核批出结果时间
    private String approveTime;
    //消息通知时间
    private Long sendTime;
    //事件名称
    private String businessType;

    //活动ID
    private Long activity_id;

}