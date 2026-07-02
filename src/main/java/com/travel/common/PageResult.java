package com.travel.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageResult<T> implements Serializable {

    /** 当前页 */
    private long current;
    /** 每页大小 */
    private long size;
    /** 总记录数 */
    private long total;
    /** 总页数 */
    private long pages;
    /** 数据列表 */
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Page<T> page) {
        this.current = page.getCurrent();
        this.size = page.getSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.records = page.getRecords();
    }

    public static <T> PageResult<T> empty() {
        PageResult<T> r = new PageResult<>();
        r.records = Collections.emptyList();
        return r;
    }
}
