package com.bowen.mybatis.entity;

import com.bowen.mybatis.enums.SqlType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public final class MappedStatement {

    /** mapper文件的namespace */
    private String namespace;

    /** sql的id属性 */
    private String sqlId;

    /** sql语句，对应源码的sqlSource */
    private String sql;

    private List<String> parameters=new ArrayList<>();

    /** 返回类型 */
    private String resultType;

    /** sqlCommandType对应select/update/insert等 */
    private SqlType sqlCommandType;


}
