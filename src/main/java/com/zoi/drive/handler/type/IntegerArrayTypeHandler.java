package com.zoi.drive.handler.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerArrayTypeHandler extends BaseTypeHandler<List<Integer>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
        // 将List<Integer>转换为Integer[]数组
        Integer[] array = parameter.toArray(new Integer[0]);
        // 使用connection.createArrayOf创建SQL数组类型
        ps.setArray(i, ps.getConnection().createArrayOf("integer", array));
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertToList(rs.getArray(columnName));
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertToList(rs.getArray(columnIndex));
    }

    @Override
    public List<Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertToList(cs.getArray(columnIndex));
    }

    private List<Integer> convertToList(java.sql.Array array) throws SQLException {
        if (array == null) {
            return null;
        }
        Integer[] result = (Integer[]) array.getArray();
        return Arrays.asList(result);
    }
}