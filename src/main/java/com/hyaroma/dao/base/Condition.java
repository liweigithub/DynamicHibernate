package com.hyaroma.dao.base;


import com.hyaroma.dao.utils.ValidateUtil;
import com.hyaroma.enums.Op;
import com.hyaroma.enums.OpType;
import com.hyaroma.exception.WDaoException;

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
        // 只有 optype 有group的情况会存在多列，如果非group 条件存在多列 则抛异常
        String[] columnsArray = getColumn().split(",");
        OpType type = getType();
        if ((type==OpType.AND || type == OpType.OR)&&columnsArray.length>1){
            throw  new WDaoException("Not Group Condition ，Not Allow Many column ! Please Check Your Condition column ("+getColumn()+") Condition is "+getType());
        }
        Op op = getOp();
        Object value = getValue();
        //单独处理like 逻辑条件
        switch (op){
            case EQ:
                break;
            case NEQ:
                break;
            case GT:
                break;
            case GTE:
                break;
            case LT:
                break;
            case LTE:
                break;
            case  LIKE:
                value = "%"+value+"%";
                break;
            case  LIKE_LEFT:
                    value = "%"+value;
                break;
            case LIKE_RIGHT:
                    value = value+"%";
                break;
            case IN:
                    if (ValidateUtil.isNull(String.valueOf(value))){
                        value = "('"+value+"')";
                    }else{
                        value = buildConditionByInValue(value);
                    }
                break;
            case NOT_IN:
                if (ValidateUtil.isNull(String.valueOf(value))){
                    value = "('"+value+"')";
                }else{
                    value = buildConditionByInValue(value);
                }
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
                if (op == Op.IN || op == Op.NOT_IN){
                    builder.append(getColumn()+" "+op.getCode()+value);
                }else{
                    builder.append(getColumn()+" "+op.getCode()+" '"+value+"'");
                }
                break;
            case OR:
                builder.append(" or ");
                if (op == Op.IN || op == Op.NOT_IN){
                    builder.append(getColumn()+" "+op.getCode()+value);
                }else{
                    builder.append(getColumn()+" "+op.getCode()+" '"+value+"' ");
                }
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

    /**
     * 由于 in 条件的比较特殊 有些情况下 会涉及到 比如： '111','222','','' 涉及到 值为 null的时候
     * 一般情况数据库查询的值为：111,222,333,444,555,666 类似这样
     * 需要 拼接 ()  类似这样(111,222,333,444,555,666)
     * 但是考虑到需要把 空字符串的数据也查询出来的时候 类似这样 (111,222,333,444,555,666,'','')
     * 就需在传递参数之前将 ''空串的值替换为："null" 然后用以下下方法来做特殊处理操作
     * wstv
     * @param value
     * @return
     */
    private String buildConditionByInValue(Object value){
        StringBuilder builder =new StringBuilder();
        builder.append("(");
        String[] split = String.valueOf(value).split(",");
        for(String v:split){
            //此处将 "null" 转换为 '' 空串
            if(ValidateUtil.isNull(v)) v="";
            builder.append("'"+v+"'").append(",");
        }
        String b  = builder.toString();
        b  = b.substring(0,b.length()-1);
        b+=")";
        value = b;
        return b;
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
