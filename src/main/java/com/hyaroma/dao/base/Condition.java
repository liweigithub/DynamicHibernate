package com.hyaroma.dao.base;

import com.hyaroma.exception.FDaoException;
import com.hyaroma.enums.Op;
import com.hyaroma.enums.OpType;

/**
 * 条件封装
 * @author  wstv
 */
public class Condition {
  private String column;
  private Object value;
  private Op op;
  private OpType type;

    public Condition(String column){
        this.column = column;
    }
    public Condition(String column,Object value, Op op,OpType type){
        this.column = column;
        this.value = value;
        this.op = op;
        this.type = type;
    }

    public String buildHql(){

        //是否有多列值并行筛选的情况 比如：（id,code） where 1=1 and (id=1 or code=1)
        // 只有 optype 有group的情况会存在多列，如过非group 条件存在多列 则抛异常
        String[] columnsArray = getColumn().split(",");
        OpType type = getType();
        if ((type==OpType.AND || type == OpType.OR)&&columnsArray.length>1){
            throw  new FDaoException("Not Group Condition ，Not Allow Many column ! Please Check Your Condition column ("+getColumn()+") Condition is "+getType());
        }
        Op op = getOp();
        Object value = getValue();
        //单独处理like 逻辑条件
        switch (op){
            case  LIKE:
                value = "%"+value+"%";
                break;
            case  LIKE_LEFT:
                    value = "%"+value;
                break;
            case LIKE_RIGHT:
                    value = value+"%";
                break;

            default:
                    value =value;
                break;
        }
        //拼装 group 类型的查询条件
        //GROUP_AND_AND("group_and_and","例如个都是and：where (EmpCode='111' AND EmpID='111')  AND  EmpName ='李巍'"),
        //GROUP_OR_OR("group_and_and","例如两个都是or：where (EmpCode='111' OR EmpID='111')  OR  EmpName ='李巍'"),
        //GROUP_AND_OR("group_and_or","例如组合为and 后续为or：where (EmpCode='111' AND EmpID='111')  OR  EmpName ='李巍'"),
        //GROUP_OR_AND("group_and_and","例如组合为：or 后续为 and：where (EmpCode='111' OR EmpID='111')  AND  EmpName ='李巍'");
        StringBuilder builder = new StringBuilder();
        switch (type){
            case AND:
                builder.append(" and ");
                builder.append(getColumn()+" "+op.getCode()+" '"+value+"'");
                break;
            case OR:
                builder.append(" or ");
                builder.append(getColumn()+" "+op.getCode()+" '"+value+"' ");
                break;
            case GROUP_AND_AND:
                //where 1=1 and (id=1 and code=1 )
                builder.append(" and ");
                String sqlAndAnd = "(";
                for(String column:columnsArray){
                    sqlAndAnd+=column+" "+op.getCode()+" '"+value+"'"+" and ";
                }
                //去掉末尾的"and "加上 ) 结束标记括号
                builder.append(sqlAndAnd.substring(0,sqlAndAnd.length()-4)+(") "));
                break;
            case GROUP_OR_OR:
                builder.append(" or ");
                String sqlOrOr = "(";
                for(String column:columnsArray){
                    sqlOrOr+=column+" "+op.getCode()+" '"+value+"'"+" or ";
                }
                //去掉末尾的"and "加上 ) 结束标记括号
                builder.append(sqlOrOr.substring(0,sqlOrOr.length()-4)+(") "));
                break;
            case GROUP_AND_OR:
                builder.append(" or ");
                String sqlAndOr = "(";
                for(String column:columnsArray){
                    sqlAndOr+=column+" "+op.getCode()+" '"+value+"'"+" and ";
                }
                //去掉末尾的"and "加上 ) 结束标记括号
                builder.append(sqlAndOr.substring(0,sqlAndOr.length()-4)+(") "));
                break;
            case GROUP_OR_AND:
                builder.append(" and ");
                String sqlOrAnd = "(";
                for(String column:columnsArray){
                    sqlOrAnd+=column+" "+op.getCode()+" '"+value+"'"+" or ";
                }
                //去掉末尾的"and "加上 ) 结束标记括号
                builder.append(sqlOrAnd.substring(0,sqlOrAnd.length()-4)+(") "));
                break;
            default:
                break;
        }
        return builder.toString();
    }


    public Object getValue() {
        return value;
    }

    public Op getOp() {
        return op;
    }

    public void setOp(Op op) {
        this.op = op;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

}
