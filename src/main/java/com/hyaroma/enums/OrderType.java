package com.hyaroma.enums;

/**
 * @author  wstv
 */
public enum OrderType{
        DESC("desc","降序"),ASC("asc","升序");
        private String msg;
        private String code;

        private OrderType(String code, String msg) {
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