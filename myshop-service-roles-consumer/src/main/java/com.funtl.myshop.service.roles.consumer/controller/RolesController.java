package com.funtl.myshop.service.roles.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.Role;
import com.funtl.myshop.commons.domain.User;
import com.funtl.myshop.commons.domain.common.JsonResult;
import com.funtl.myshop.commons.domain.common.Menu;
import com.funtl.myshop.service.login.api.LoginService;
import com.funtl.myshop.service.roles.api.RoleService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@CrossOrigin(origins = "http://localhost:8603")
@RequestMapping(value = "system/role")
public class RolesController {

    @Reference(version = "${services.versions.roles.v1}")
    private RoleService roleService;

    @Reference(version = "${services.versions.login.v1}")
    private LoginService loginService;
    /**
     * 跳转到列表页
     *
     * @return
     */
    @RequestMapping
    public String index(HttpServletRequest request, HttpSession session) {
        request.getCookies();
        Cookie[] cookie=request.getCookies();
        Long id=null;
        //获取登录用户的id
        for(int i=0;i<cookie.length;i++){
            System.out.println(cookie[i].getValue());
            System.out.println(cookie[i].getName());
            if("userID".equals(cookie[i].getName())){
                id=Long.parseLong(cookie[i].getValue());
                System.out.println("id"+id);//得到登录用户的id
                break;
            }
        }
        Map<String,Object> result=loginService.getLoginMessage(id);
        Set<String> keys=(Set<String>) result.get("keys");
        Set<String> urls=(Set<String>) result.get("urls");
        session.setAttribute("keys",keys);
        session.setAttribute("urls",urls);
        return "system/role";
    }
    @RequestMapping("/list")
    @ResponseBody
    public List<Role> list(){
        return roleService.getList();
    }

    /**
     * 获取权限树形列表
     * @return
     */
    @RequestMapping("/permissionTree")
    @ResponseBody
    public List<Map<String,Object>> permissionTree() {
        System.out.println(121221);
        return roleService.getPermissionTree();
    }

    /**
     * 获取角色对应的权限列表
     * @param id
     * @return
     */
    @RequestMapping("permission/{id}")
    @ResponseBody
    public List<Map<String,Object>> permission(@PathVariable("id")Long id){
        System.out.println(111);
        return roleService.findPermissionById(id);
    }

    @RequestMapping("/permissionSave")
    @Transactional
    @ResponseBody
    public JsonResult permissionSave(Long roleId,Long[] permissionId) {
        if(roleService.permissionSave(roleId,permissionId)){
            return JsonResult.success("授权成功");
        }else {
            return JsonResult.error("授权失败");
        }
    }

    /**
     * 创建或编辑角色
     * @param id  角色 id
     * @param model  角色 model
     * @return
     */
    @RequestMapping({"/form","/load"})
    public String form(Long id, Model model){
        if(id!=null){
            Role role=roleService.findRoleById(id);
            model.addAttribute("role",role);
        }
        return "system/role/form";
    }
    @RequestMapping({"/save","/update"})
    @ResponseBody
    @Transactional
    public JsonResult form(@Valid Role role, BindingResult br){
        System.out.println("保存成功");
        if(!br.hasErrors()) {
            return roleService.saveRole(role);
        }else{
            return JsonResult.error("校验不通过");
        }
    }
    @GetMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResult delete(Long id) {
        System.out.println("看看能不能删除");
        return roleService.deleteRoleById(id);
    }
}
