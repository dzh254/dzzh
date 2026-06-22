package com.cybersec.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cybersec.domain.entity.Alert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警 Mapper
 */
@Mapper
public interface AlertMapper extends BaseMapper<Alert> {

    /** 统计自某个时间以来的告警数量 */
    @Select("SELECT COUNT(*) FROM t_alert WHERE created_at >= #{since}")
    long countSince(@Param("since") LocalDateTime since);

    /** 按严重级别分组统计 */
    @Select("SELECT severity, COUNT(*) as cnt FROM t_alert WHERE created_at >= #{since} GROUP BY severity")
    List<Map<String, Object>> countBySeverity(@Param("since") LocalDateTime since);

    /** 按来源分组统计 */
    @Select("SELECT source, COUNT(*) as cnt FROM t_alert WHERE created_at >= #{since} GROUP BY source")
    List<Map<String, Object>> countBySource(@Param("since") LocalDateTime since);
}
