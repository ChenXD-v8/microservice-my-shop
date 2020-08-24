package com.funtl.myshop.service.login.api;

import com.funtl.myshop.commons.domain.User;
import com.funtl.myshop.commons.domain.common.Menu;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LoginService {
    User findUserByAccount(String account);

    /**
     * 通过用户id返回该用户的菜单列表
     * @param id  用户id
     * @return
     */
    List<Menu> findUserMenus(Long id);
    List<Menu> findAllMenus();
    Set<String> getKeysBy();

    /**
     * 根据用户id 返回用户菜单，用户权限，
     * @param id
     * @return
     */
    Map<String,Object> getLoginMessage(Long id);
}
