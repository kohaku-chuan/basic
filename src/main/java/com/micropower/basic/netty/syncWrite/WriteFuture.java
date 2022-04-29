package com.micropower.basic.netty.syncWrite;

import com.micropower.basic.common.dto.CommonDto;

import java.util.concurrent.Future;

public interface WriteFuture<T> extends Future<T> {

    Throwable cause();

    void setCause(Throwable cause);

    boolean isWriteSuccess();

    void setWriteResult(boolean result);

    String requestId();

    T response();

    void setResponse(CommonDto response);

    boolean isTimeout();
}
