package com.cybersec.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页响应封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页码 */
    private long page;
    /** 每页大小 */
    private long size;
    /** 总记录数 */
    private long total;
    /** 总页数 */
    private long pages;
    /** 数据列表 */
    private List<T> records;

    public static <T> PageResult<T> of(long page, long size, long total, List<T> records) {
        long pages = size > 0 ? (total + size - 1) / size : 0;
        return new PageResult<>(page, size, total, pages, records);
    }

    public static <T> PageResult<T> empty(long page, long size) {
        return new PageResult<>(page, size, 0, 0, Collections.emptyList());
    }

    /**
     * 将 PageResult 中的记录类型转换为另一种类型
     */
    public <R> PageResult<R> map(Function<T, R> converter) {
        List<R> converted = records.stream()
                .map(converter)
                .collect(Collectors.toList());
        return new PageResult<>(page, size, total, pages, converted);
    }

    public boolean isEmpty() {
        return records == null || records.isEmpty();
    }
}
