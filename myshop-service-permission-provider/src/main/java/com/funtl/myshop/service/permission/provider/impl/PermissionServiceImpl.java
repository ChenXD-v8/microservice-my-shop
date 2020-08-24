package com.funtl.myshop.service.permission.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.Role;
import com.funtl.myshop.commons.domain.common.JsonResult;
import com.funtl.myshop.commons.mapper.PermissionDao;
import com.funtl.myshop.commons.mapper.RoleDao;
import com.funtl.myshop.service.permission.api.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service(version = "${services.versions.permission.v1}")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;
    @Override
    public List<Map<String,Object>>  list() {
        List<Permission> permissions= permissionDao.findAllByParentIsNull();
        List<Map<String,Object>> list=new ArrayList<>();
        List<Map<String,Object>> resultList=new ArrayList<>();
        list=EntityToList(permissions);
        resultList=bl(list);
        return resultList;
    }

    @Override
    public Permission findById(Long id) {
        Permission permission=permissionDao.findById(id).orElse(null);
        if(permission!=null){
            permission.getChildren().clear();
            Permission parent=new Permission();
            if(permission.getParent()!=null){
                parent.setId(permission.getParent().getId());
                permission.setParent(parent);
            }

        }
        return permission;
    }

    @Override
    public void save(Permission permission) {
        System.out.println("看看有没有parent");
        if(permission.getParent()!=null){
            System.out.println(permission.getParent().getId()+"----"+permission.getParent().getName());
        }
        permissionDao.save(permission);
    }

    @Override
    public JsonResult deleteById(Long id) {
        Permission permission=permissionDao.findById(id).orElse(null);
        if(permission!=null){
            permissionDao.delete(permission);
            return JsonResult.success();
        }
        else{
            return JsonResult.error("数据不存在");
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
            map.put("name",temp.get("name"));
            map.put("permissionKey",temp.get("permissionKey"));
            map.put("type",temp.get("type"));
            map.put("enable",temp.get("enable"));
            map.put("description",temp.get("description"));
            map.put("weight",temp.get("weight"));
            map.put("resource",temp.get("resource"));
            map.put("path",temp.get("path"));

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
            map.put("name",permission.getName());
            map.put("permissionKey",permission.getPermissionKey());
            map.put("type",permission.getType());
            map.put("enable",permission.getEnable());
            map.put("description",permission.getDescription());
            map.put("weight",permission.getWeight());
            map.put("resource",permission.getResource());
            map.put("path",permission.getPath());
            if(permission.getChildren()!=null&&!permission.getChildren().isEmpty()){
                map.put("children",permission.getChildren());
            }
            list.add(map);
        }
        return list;
    }
}
