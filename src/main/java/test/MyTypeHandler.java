package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: zhaobowen
 * @create: 2020-07-26 17:34
 * @description:
 **/
//@MappedJdbcTypes(JdbcType.VARCHAR)
//@MappedTypes(JSONObject.class)
public class MyTypeHandler /*extends BaseTypeHandler<JSONObject>*/ {
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType) throws SQLException {
//        ps.setString(i,parameter.toJSONString());
//    }
//
//    @Override
//    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        return JSON.parseObject(rs.getString(columnName));
//    }
//
//    @Override
//    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        return JSON.parseObject(rs.getString(columnIndex));
//    }
//
//    @Override
//    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        return JSON.parseObject(cs.getString(columnIndex));
//    }
}
