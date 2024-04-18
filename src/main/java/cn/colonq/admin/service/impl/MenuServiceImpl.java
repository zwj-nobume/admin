package cn.colonq.admin.service.impl;

import org.springframework.stereotype.Service;

import cn.colonq.admin.entity.MenuInfo;
import cn.colonq.admin.mapper.MenuMapper;
import cn.colonq.admin.service.IMenuService;

@Service
public class MenuServiceImpl extends BaseServiceImpl<MenuInfo> implements IMenuService {

    public MenuServiceImpl(MenuMapper menuMapper) {
        super(menuMapper);
    }
}
