package com.micropower.basic.netty.syncWrite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date: 2021/2/19 10:34
 * @Description: TODO →
 * @Author:Kohaku_川
 **/
public class SyncWriteMap {
    public static Map<String, WriteFuture> writeRecords = new ConcurrentHashMap<String, WriteFuture>();
}
