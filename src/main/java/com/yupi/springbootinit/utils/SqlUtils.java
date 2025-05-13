package com.yupi.springbootinit.utils;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * SQL 工具
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Component
public class SqlUtils {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 执行SQL文件
     * @param sqlFilePath SQL文件路径
     */
    public void executeSqlFile(String sqlFilePath) {
        try {
            Path path = Paths.get(sqlFilePath);
            if (!Files.exists(path)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "SQL文件不存在");
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))) {
                String line;
                StringBuilder sql = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    // 跳过注释和空行
                    if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                        continue;
                    }
                    sql.append(line);
                    // 如果行以分号结尾，执行SQL
                    if (line.trim().endsWith(";")) {
                        jdbcTemplate.execute(sql.toString());
                        sql = new StringBuilder();
                    }
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行SQL文件失败: " + e.getMessage());
        }
    }

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
