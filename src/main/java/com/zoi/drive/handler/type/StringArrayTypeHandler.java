package com.zoi.drive.handler.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/20 1:30
 **/
public class StringArrayTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        // 将 List<String> 转换为 varchar[] 并设置到 PostgreSQL 中
        String[] array = parameter.toArray(new String[0]);
        ps.setArray(i, ps.getConnection().createArrayOf("varchar", array));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 将 PostgreSQL 的 varchar[] 转换为 List<String>
        Array array = rs.getArray(columnName);
        if (array == null) {
            return null;
        }
        String[] strArray = (String[]) array.getArray();
        List<String> result = new ArrayList<>();
        for (String s : strArray) {
            result.add(s);
        }
        return result;
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        if (array == null) {
            return null;
        }
        String[] strArray = (String[]) array.getArray();
        List<String> result = new ArrayList<>();
        for (String s : strArray) {
            result.add(s);
        }
        return result;
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        if (array == null) {
            return null;
        }
        String[] strArray = (String[]) array.getArray();
        List<String> result = new ArrayList<>();
        for (String s : strArray) {
            result.add(s);
        }
        return result;
    }
}