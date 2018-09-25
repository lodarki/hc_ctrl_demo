package com.ts.hc_ctrl_demo.common.entity;

/**
 * 多线程调用处理结果
 * @author pant
 * @date 2018/3/30 15:47
 */
public class AsyncExcResult {

    private AsyncExcResult(boolean success, String msg, Object data) {
        this.success = success;
        this.msg = msg;
        this.returnData = data;
    }

    /**
     * 任务执行是否成功
     */
    private boolean success;

    /**
     * 处理信息
     */
    private String msg;

    /**
     * 处理结果的返回值，如果有需要的话
     */
    private Object returnData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getReturnData() {
        return returnData;
    }

    public void setReturnData(Object returnData) {
        this.returnData = returnData;
    }

    /**
     * 成功
     * @return
     */
    public static AsyncExcResult success() {
        return success("", null);
    }

    /**
     * 成功
     * @param msg
     * @return
     */
    public static AsyncExcResult success(String msg) {
        return success(msg, null);
    }

    /**
     * 成功
     *
     * @param msg
     * @param data
     * @return
     */
    public static AsyncExcResult success(String msg, Object data) {
        return new AsyncExcResult(true, msg, data);
    }

    /**
     * 失败
     *
     * @return
     */
    public static AsyncExcResult fail() {
        return fail("", null);
    }

    /**
     * 失败
     *
     * @param msg
     * @return
     */
    public static AsyncExcResult fail(String msg) {
        return fail(msg, null);
    }

    /**
     * 失败
     *
     * @param msg
     * @param data
     * @return
     */
    public static AsyncExcResult fail(String msg, Object data) {
        return new AsyncExcResult(false, msg, data);
    }
}
