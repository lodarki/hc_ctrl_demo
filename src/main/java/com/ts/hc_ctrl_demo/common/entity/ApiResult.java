package com.ts.hc_ctrl_demo.common.entity;

import com.alibaba.fastjson.JSON;

public class ApiResult {

    /**
     * 处理结果码
     */
    private int resultCode;

    /**
     * 数据
     */
    private Object data;

    /**
     * 消息
     */
    private String message;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResult(int resultCode, Object data, String message) {
        this.resultCode = resultCode;
        this.data = data;
        this.message = message;
    }

    public static ApiResult Ok() {
        return Ok("success");
    }

    public static ApiResult Ok(String message) {
        return Ok(null, message);
    }

    public static ApiResult Ok(Object data, String message) {
        return new ApiResult(200, data, message);
    }

    public static ApiResult Error() {
        return Error(201);
    }

    public static ApiResult Error(int errorCode) {
        return Error(errorCode, "failed !");
    }

    public static ApiResult Error(int errorCode, String errorMessage) {
        return Error(errorCode, null, errorMessage);
    }

    public static ApiResult Error(int errorCode, Object data, String errorMessage) {
        return new ApiResult(errorCode, data, errorMessage);
    }

    public String toJSon() {
        return JSON.toJSONString(this);
    }
}
