package cn.colonq.admin.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import cn.colonq.admin.anno.Table;
import cn.colonq.admin.anno.TableField;
import cn.colonq.admin.config.ServiceException;
import cn.colonq.admin.entity.PageList;
import cn.colonq.admin.entity.Result;
import cn.colonq.admin.mapper.BaseMapper;
import cn.colonq.admin.service.BaseService;

public class BaseServiceImpl<T> implements BaseService<T> {
    private final BaseMapper<T> baseMapper;

    public BaseServiceImpl(final BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public PageList<T> selectPage(final T param, final long pageNum, final long pageSize) {
        return baseMapper.selectPage(param, pageNum, pageSize);
    }

    @Override
    public Result insert(final T param) {
        @SuppressWarnings("unchecked")
        final Class<T> cls = (Class<T>) param.getClass();
        final Field[] fields = cls.getDeclaredFields();
        final Class<?>[] parameterTypes = new Class<?>[fields.length];
        final Object[] initargs = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            final Field field = fields[i];
            parameterTypes[i] = field.getType();
            if ("createName".equals(field.getName())) {
                // TODO 获取登录用户名
                initargs[i] = "admin";
                continue;
            }
            final TableField anno = field.getAnnotation(TableField.class);
            if (anno != null && !anno.isInsert()) {
                initargs[i] = null;
                continue;
            }
            final boolean canAccess = field.canAccess(param);
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(param);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new ServiceException(e.getMessage());
            }
            initargs[i] = value;
            field.setAccessible(canAccess);
        }
        T insertParam = null;
        try {
            insertParam = cls.getDeclaredConstructor(parameterTypes).newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new ServiceException(e.getMessage());
        }
        int row = baseMapper.insert(insertParam);
        if (row == 1) {
            return Result.ok("新增成功");
        }
        throw new ServiceException("新增失败, row = " + row);
    }

    @Override
    public Result update(final T param) {
        int row = baseMapper.update(param);
        if (row == 1) {
            return Result.ok("修改成功");
        }
        throw new ServiceException("修改失败, row = " + row);
    }

    @Override
    public Result delete(final Class<? extends T> cls, final Set<String> ids) {
        int row = baseMapper.delete(cls, ids);
        if (row > 0) {
            final Table anno = cls.getAnnotation(Table.class);
            final String idName = anno.idName();
            for (String tableName : anno.linkTable()) {
                baseMapper.deleteLink(tableName, idName, ids);
            }
            return Result.ok("删除成功");
        }
        throw new ServiceException("删除失败, row = " + row);
    }
}