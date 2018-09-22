package com.ts.hc_ctrl_demo.common.entity;

public class AlarmDesc {

    private int typeCode;

    private String codeDesc;

    public int getTypeCode() {
        return typeCode;
    }

    private String message;

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
