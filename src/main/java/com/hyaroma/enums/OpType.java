package com.hyaroma.enums;

public enum OpType {
    //枚举解释：GROUP_AND_AND(1:GEOUP  2:AND  3:AND) 分割标记标识 "_"
    //1：group 单个值作为多个字段的查询条件标识
    //2：and | or  作为组合条件单字段多个值的中间判断条件
    // 3: and | or 作为整合查询后续判断条件
    AND("and", "并且"), OR("or", "或者"),
    GROUP_AND_AND("group_and_and","例如个都是and：where (EmpCode='111' AND EmpID='111')  AND  EmpName ='李巍'"),
    GROUP_OR_OR("group_or_or","例如两个都是or：where (EmpCode='111' OR EmpID='111')  OR  EmpName ='李巍'"),
    GROUP_AND_OR("group_and_or","例如组合为and 后续为or：where (EmpCode='111' AND EmpID='111')  OR  EmpName ='李巍'"),
    GROUP_OR_AND("group_or_and","例如组合为：or 后续为 and：where (EmpCode='111' OR EmpID='111')  AND  EmpName ='李巍'");

    private String msg;
    private String code;

    private OpType(String code, String msg) {
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