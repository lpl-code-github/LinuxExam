package com.lpl.stu.utils;

/**
 * 封装结果集
 */
public class Result {
  private Boolean success;
  private String massage;
  private Object data;

  public Result(Boolean success, String massage, Object data) {
    this.success = success;
    this.massage = massage;
    this.data = data;
  }
}
