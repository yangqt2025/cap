package com.yupi.springbootinit.repository;
import com.yupi.springbootinit.model.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    // 自定义查询方法，根据 userId 查询并按时间从远到近排序
    @Query("SELECT f FROM FileEntity f WHERE f.userId = :userId ORDER BY f.timestamp DESC")
    List<FileEntity> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
}