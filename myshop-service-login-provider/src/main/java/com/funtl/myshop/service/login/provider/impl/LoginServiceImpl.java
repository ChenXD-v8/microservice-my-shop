package com.funtl.myshop.service.login.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.Role;
import com.funtl.myshop.commons.domain.User;
import com.funtl.myshop.commons.domain.common.JsonResult;
import com.funtl.myshop.commons.domain.common.Menu;
import com.funtl.myshop.commons.mapper.PermissionDao;
import com.funtl.myshop.commons.mapper.RoleDao;
import com.funtl.myshop.commons.mapper.UserDao;
import com.funtl.myshop.service.login.api.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service(version = "${services.versions.login.v1}")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private RoleDao roleDao;

    @Value("${system.super.user.id}")
    private Long superId;

    @Override
    public User findUserByAccount(String account) {
        User user=userDao.findFirstByAccount(account);
        if(user!=null){
            user.getRoles().clear();
            System.out.println("可以返回用户信息");
        }

        return user;
    }

    @Override
    public List<Menu> findUserMenus(Long id) {
        return null;
    }

    @Override
    public List<Menu> findAllMenus() {
        return null;
    }

    @Override
    public Set<String> getKeysBy() {
        return null;
    }

    @Override
    public Map<String, Object> getLoginMessage(Long id) {
     User user=userDao.findFirstById(id);
     Map<String,Object> result=new HashMap<>();
     Set<Permission> permissions;
        //判断是不是超级用户
        if(Objects.equals(id,superId)){
            permissions=permissionDao.findAllByEnableOrderByWeightDesc(true);
        }else{
            //获取用户菜单
            Set<Role> roles=user.getRoles();
            permissions=new HashSet<>();
            roles.forEach(role -> permissions.addAll(role.getPermissions()));
        }
        //存储菜单
        TreeSet<Permission> menus=new TreeSet<>((o1, o2) -> {
            if(Objects.equals(o1.getWeight(),o2.getWeight())){
                return -1;
            }
            return o1.getWeight()-o2.getWeight();
        });

        //存储权限key
        Set<String> keys=new HashSet<>();
        //所有有权限访问的请求
        Set<String> urls=new HashSet<>();
        permissions.forEach(permission -> {
            if(permission.getEnable()){
                if(permission.getType().equals(Permission.Type.MENU.name())){
                    //是菜单
                    menus.add(permission);
                    urls.add(permission.getPath());
                }
                keys.add(permission.getPermissionKey());
                urls.addAll(Arrays.asList(permission.getResource().split(",")));
            }
        });
        //树形数据转换
        List<Menu> menuList=new ArrayList<>();
        menus.forEach(permission -> {
            Menu m=new Menu();
            m.setId(permission.getId());
            m.setText(permission.getName());
            m.setParentId(permission.getParent()==null?null:permission.getParent().getId());
            m.setHref(permission.getPath());
            menuList.add(m);
        });
        result.put("menus",menuList);
        result.put("keys",keys);
        result.put("urls",urls);
        System.out.println("看来是到这里了");
        return result;
    }
}
