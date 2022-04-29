package com.micropower.basic.netty.syncWrite;

import com.micropower.basic.common.dto.CommonDto;
import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2021/2/19 10:13
 * @Description: TODO →
 * @Author:Kohaku_川
 **/
@Data
public class SyncWriteFuture implements WriteFuture<CommonDto> {

    private CountDownLatch latch = new CountDownLatch(1);

    private final long begin = System.currentTimeMillis();

    private long timeout;

    private CommonDto requestData;

    private final String address;

    private boolean writeResult;

    private Throwable cause;

    private boolean isTimeout = false;


    private Integer value;

    public SyncWriteFuture(String address) {
        this.address = address;
    }

    public SyncWriteFuture(String address, Integer value) {
        this.address = address;
        this.value=value;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public boolean isWriteSuccess() {
        return writeResult;
    }

    @Override
    public void setWriteResult(boolean result) {
        this.writeResult = result;
    }

    @Override
    public String requestId() {
        return address;
    }

    @Override
    public CommonDto response() {
        return requestData;
    }

    @Override
    public void setResponse(CommonDto requestData) {
        this.requestData = requestData;
        latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public CommonDto get() throws InterruptedException {
         latch.wait();
        return requestData;
    }

    @Override
    public CommonDto get(long timeout, TimeUnit unit) throws InterruptedException {
        if (latch.await(timeout, unit)) {
            return requestData;
        }
        return null;
    }

    @Override
    public boolean isTimeout() {
        if (isTimeout) {
            return isTimeout;
        }
        return System.currentTimeMillis() - begin > timeout;
    }
}