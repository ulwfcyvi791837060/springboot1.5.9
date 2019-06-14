package com.yyx.aio.common.entity;

/**
 * Created by wuwei on 2017/7/4.
 */
public class FailureResponseEntity extends ResponseEntity{
    public FailureResponseEntity(String msg) {
        super(-1, msg);
    }

    public FailureResponseEntity(Object data) {
        super(-1, "操作失败", data);
    }

    public FailureResponseEntity() {
        super(-1, "操作失败");
    }
}
