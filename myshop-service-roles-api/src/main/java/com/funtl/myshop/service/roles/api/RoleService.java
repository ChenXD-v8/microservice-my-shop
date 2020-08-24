package com.funtl.myshop.service.roles.api;

import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.Role;
import com.funtl.myshop.commons.domain.common.JsonResult;

import java.util.List;
import java.util.Map;

public interface RoleService {

    /**
     * 获取所有角色列表
     * @return
     */
    List<Role> getList();

    /**
     * 获取权限树形列表
     * @return
     */
    List<Map<String,Object>> getPermissionTree();

    /**
     * 通过角色查询该角色下所有权限
     * @param id  角色id
     * @return
     */
    List<Map<String,Object>> findPermissionById(Long id);

    /**
     * 保存已勾选的角色权限
     * @param roleId 角色id
     * @param permissionId 权限 id集合
     * @return
     */
     Boolean permissionSave(Long roleId,Long[] permissionId);

    /**
     * 根据roleid返回角色信息
     * @param id  角色id
     * @return
     */
     Role findRoleById(Long id);

    /**
     * 创建或保存角色信息
     * @param role
     * @return
     */
    JsonResult saveRole(Role role);

    /**
     * 通过角色id删除角色
     * @param id 角色id
     * @return
     */
    JsonResult deleteRoleById(Long id);
}
