package com.zhisheng.alert.Test;

import lombok.Data;

@Data
public class MarketRule {
    private Long id;
    private String exprsession;

    public MarketRule(Long id, String exprsession) {
        this.id = id;
        this.exprsession = exprsession;
    }

    @Override
    public String toString() {
        return "MarketRule{" +
                "id=" + id +
                ", exprsession='" + exprsession + '\'' +
                '}';
    }
}
