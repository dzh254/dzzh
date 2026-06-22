package com.cybersec.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cybersec.domain.entity.AlertAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 告警分析结果 Mapper
 */
@Mapper
public interface AlertAnalysisMapper extends BaseMapper<AlertAnalysis> {

    /** 获取某告警的最新分析结果 */
    @Select("SELECT * FROM t_alert_analysis WHERE alert_id = #{alertId} ORDER BY created_at DESC LIMIT 1")
    AlertAnalysis findLatestByAlertId(@Param("alertId") Long alertId);
}
