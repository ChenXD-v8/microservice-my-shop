package com.funtl.myshop.service.roles.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.Role;
import com.funtl.myshop.commons.domain.User;
import com.funtl.myshop.commons.domain.common.JsonResult;
import com.funtl.myshop.commons.mapper.PermissionDao;
import com.funtl.myshop.commons.mapper.RoleDao;
import com.funtl.myshop.commons.mapper.UserDao;
import com.funtl.myshop.service.roles.api.RoleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.StyledEditorKit;
import java.util.*;

@Service(version = "${services.versions.roles.v1}")
public class RolesServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;
    @Override
    public List<Role> getList() {
        System.out.println(1223);
        List<Role> roleList=roleDao.findAll();
        for(Role role:roleList){
            role.setPermissions(null);
        }
        return roleList;
    }

    @Override
    public List<Map<String,Object>> getPermissionTree() {
        List<Permission> permissions= permissionDao.findAllByParentIsNull();
        List<Map<String,Object>> list=new ArrayList<>();
        List<Map<String,Object>> resultList=new ArrayList<>();
        list=EntityToList(permissions);
        resultList=bl(list);
        return resultList;
    }

    @Override
    public List<Map<String, Object>> findPermissionById(Long id) {
        Role role=roleDao.findById(id).orElse(null);
        Set<Permission> permissions= role.getPermissions();
        List<Map<String, Object>> result=new ArrayList<>();
        for(Permission permission:permissions){
            Map<String, Object> map=new HashMap<>();
            map.put("id",permission.getId());
            result.add(map);
        }
        return result;
    }

    @Override
    public Boolean permissionSave(Long roleId, Long[] permissionId) {
            Role role=roleDao.findById(roleId).orElse(null);
            //先清楚已有角色
            role.getPermissions().clear();
            for (Long pid : permissionId) {
                role.getPermissions().add(permissionDao.findById(pid).orElse(null));
            }
            roleDao.save(role);
            return true;
    }

    @Override
    public Role findRoleById(Long id) {
        Role role=roleDao.findById(id).orElse(null);
        role.getPermissions().clear();
        System.out.println("可以返回角色信息");
        return role;
    }

    @Override
    public JsonResult saveRole(Role role) {
        System.out.println(111);
        if(role.getId()==null){
            if(roleDao.findFirstByRoleName(role.getRoleName())!=null){
                return JsonResult.error("该角色已被创建");
            }
        }
        roleDao.save(role);
        System.out.println("看看能不能保存");
        return JsonResult.success();
    }

    @Override
    public JsonResult deleteRoleById(Long id) {
        Role role=roleDao.findById(id).orElse(null);
        if(role!=null){
            roleDao.delete(role);
            return JsonResult.success();
        }
        else{
            return JsonResult.error("校验不通过");
        }
    }

    /**
     * 递归遍历List<Permission>返回list
     * @param list
     * @return
     */
    private static List<Map<String,Object>> bl(List<Map<String,Object>> list){
        List<Map<String,Object>> result=new ArrayList<>();
        for(Map<String,Object> temp:list){
            Map<String,Object> map=new HashMap<>();
            map.put("id",temp.get("id"));
            map.put("text",temp.get("text"));
            if(temp.get("children")!=null){
                List<Map<String,Object>> child=new ArrayList<>();
                List<Permission> permissions=(List<Permission>) temp.get("children");
                child=EntityToList(permissions);
                List<Map<String,Object>> children= bl(child);
                map.put("children",children);
            }
            result.add(map);
        }
        return result;
    }
    private static List<Map<String,Object>> EntityToList(List<Permission> permissions){
        List<Map<String,Object>> list=new ArrayList<>();
        for(Permission permission:permissions){
            Map<String,Object> map=new HashMap<>();
            map.put("id",permission.getId());
            map.put("text",permission.getName());
            if(permission.getChildren()!=null&&!permission.getChildren().isEmpty()){
                map.put("children",permission.getChildren());
            }
            list.add(map);
        }
        return list;
    }
}
