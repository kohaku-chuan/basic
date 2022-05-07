package com.micropower.basic.entity;

import com.micropower.basic.forward.common212.FlowForward;
import lombok.Data;

/**
 * @author Kohaku_川
 * @description TODO
 * @date 2022/5/6 14:52
 */
@Data
public class FlowRecordBean {

    public FlowRecordBean() {
    }

    int stationId;

    String time;

    String levelHeight;

    String speed;

    String flow;

    String totalFlow;

    String emissions;

    String range;

    public FlowRecordBean(int stationId, String time, String levelHeight, String speed, String flow, String totalFlow, String emissions, String range) {
        this.stationId = stationId;
        this.time = time;
        this.levelHeight = levelHeight;
        this.speed = speed;
        this.flow = flow;
        this.totalFlow = totalFlow;
        this.emissions = emissions;
        this.range = range;
    }
}
