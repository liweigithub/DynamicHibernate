package com.hyaroma.enums;

/**
 * @author  wstv
 */
public enum Op {
        EQ("=", "等于"),
        NEQ("<>", "不等于"),
        GT(">", "大于"),
        GTE(">=", "大于等于"),
        LT("<", "小于"),
        LTE("<=", "小于等于"),
        LIKE("like", "包含"),
        LIKE_LEFT("like", "左包含"),
        LIKE_RIGHT("like", "右包含");

         private String msg;
         private String code;

         private Op(String code, String msg) {
             this.code = code;
             this.msg = msg;
         }

         public String getMsg() {
             return this.msg;
         }

         public String getCode() {
             return this.code;
         }
     }
