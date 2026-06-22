package com.cybersec.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cybersec.domain.entity.ThreatIntel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 威胁情报 Mapper
 */
@Mapper
public interface ThreatIntelMapper extends BaseMapper<ThreatIntel> {

    /** 根据IOC类型和值查询 */
    @Select("SELECT * FROM t_threat_intel WHERE ioc_type = #{type} AND ioc_value = #{value}")
    ThreatIntel findByIoc(@Param("type") String type, @Param("value") String value);
}
