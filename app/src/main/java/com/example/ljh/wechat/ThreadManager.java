package com.example.ljh.wechat;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ljh on 2017/11/2.
 */

public class ThreadManager {
     static ExecutorService executorService;

    static ExecutorService startThread(){
        executorService = Executors.newCachedThreadPool();
        return executorService;
    }
}
