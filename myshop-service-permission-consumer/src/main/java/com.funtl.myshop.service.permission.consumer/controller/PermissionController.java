package com.funtl.myshop.service.permission.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.Role;
import com.funtl.myshop.commons.domain.common.JsonResult;
import com.funtl.myshop.service.login.api.LoginService;
import com.funtl.myshop.service.permission.api.PermissionService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = "system/permission")
public class PermissionController {

    @Reference(version = "${services.versions.permission.v1}")
    private PermissionService permissionService;
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
        return "system/permission";
    }
    @PostMapping("/list")
    @ResponseBody
    public List<Map<String,Object>> list(){
        System.out.println("hhh");
        List<Map<String,Object>> list=new ArrayList<>();
        list=permissionService.list();
        return list;
    }


    @RequestMapping({"/form","/load"})
    public String form(Long id, Model model){
        if (id != null) {
            //编辑
            Permission permission=permissionService.findById(id);
            model.addAttribute("permission",permission);
        }
        return "system/permission/form";
    }
    @PostMapping("/combo")
    @ResponseBody
    public List<Map<String,Object>> combo(){
        List<Map<String,Object>> list=new ArrayList<>();
        list=permissionService.list();
        return list;
    }
    @PostMapping({"/save","/update"})
    @ResponseBody
    @org.springframework.transaction.annotation.Transactional
    public JsonResult form(@RequestParam(name = "paentId") Long paentId, @Valid Permission permission, BindingResult br){
        System.out.println("保存权限model--"+paentId);
        System.out.println(br.getAllErrors());
        if(!br.hasErrors()) {
            Permission permission1=new Permission();
            permission1.setId(paentId);
            permission.setParent(permission1);
            System.out.println(1111);
            System.out.println(permission.getParent().getId());
            permissionService.save(permission);
            return JsonResult.success();
        } else{
            return JsonResult.error("校验不通过");
        }
    }

    @GetMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResult delete(Long id){
        System.out.println("删除权限节点");
        return permissionService.deleteById(id);
    }
}
