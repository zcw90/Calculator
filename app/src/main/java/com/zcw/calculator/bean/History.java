package com.zcw.calculator.bean;

/**
 * 历史记录对象<br />
 * 包含历史记录的内容，结果，时间
 */
public class History {
    /** 数据库中id */
    private String id;

    /** 表达式 */
    private String expression;

    /** 结果 */
    private String result;

    /** 时间 */
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "id:" + id + " " + expression + " = " + result + " " + date;
    }
}
