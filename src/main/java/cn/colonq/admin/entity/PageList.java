package cn.colonq.admin.entity;

import java.util.List;

public record PageList<T>(
        long page,
        long size,
        long total,
        List<T> data) {
}
