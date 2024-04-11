package cn.colonq.admin.entity;

import java.util.List;

public class PageList<T> {
    private long page;
    private long size;
    private long total;
    private List<T> data;

    public PageList() {
        this.page = 1;
        this.size = 20;
    }

    public PageList(long page, long size) {
        this.page = page;
        this.size = size;
    }

    public PageList(long page, long size, long total, List<T> data) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.data = data;
    }

    public long getPage() {
        return page;
    }

    public long getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
