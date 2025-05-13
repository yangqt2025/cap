package com.yupi.springbootinit.config;

import com.yupi.springbootinit.model.enums.EducationEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 学历枚举类型处理器
 */
@MappedTypes(EducationEnum.class)
public class EducationEnumTypeHandler extends BaseTypeHandler<EducationEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EducationEnum parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, jdbcType.TYPE_CODE);
        } else {
            ps.setString(i, parameter.getValue());
        }
    }

    @Override
    public EducationEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToEnum(value);
    }

    @Override
    public EducationEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToEnum(value);
    }

    @Override
    public EducationEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToEnum(value);
    }

    /**
     * 将字符串转换为枚举值
     */
    private EducationEnum convertToEnum(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        // 尝试通过值获取枚举
        EducationEnum enumValue = EducationEnum.getEnumByValue(value);
        if (enumValue != null) {
            return enumValue;
        }
        // 尝试通过文本获取枚举
        return EducationEnum.getEnumByText(value);
    }
} 