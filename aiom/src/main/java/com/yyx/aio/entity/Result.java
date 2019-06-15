package com.yyx.aio.entity;

/**
 * @author: zhk
 * @Date :          2019/6/15 10:19
 */
public class Result {

     /*"code": "0",

     "msg": "成功",

     "data": null,

     "success": true*/

     String code;
     String msg;
     Object data;
     boolean success;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
