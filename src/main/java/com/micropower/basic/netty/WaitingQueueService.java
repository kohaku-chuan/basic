package com.micropower.basic.netty;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.common.dto.receive.RunningStateDto;

/**
 * @Date: 2021/3/16 14:39
 * @Description: TODO → 等待发送的指令队列调用接口
 * @Author:Kohaku_川
 **/
public interface WaitingQueueService {
    void readySendQueue(String communicationMode, String areaCode, Integer address);

    void feedbackCycleUpload(String communicationMode, String areaCode, Integer address);

}
