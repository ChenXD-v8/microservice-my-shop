package com.funtl.myshop.service.login.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.User;
import com.funtl.myshop.commons.domain.common.Menu;
import com.funtl.myshop.service.login.api.LoginConsumerService;
import com.funtl.myshop.service.login.api.LoginService;
import com.funtl.myshop.service.permission.api.PermissionConsumerService;
import com.funtl.myshop.service.roles.api.RolesConsumerService;
import com.funtl.myshop.service.user.api.UserConsumerService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.HttpCookie;
import java.util.*;

@Controller
public class LoginController {


    @Reference(version = "${services.versions.login.v1}")
    private LoginService loginService;
    @Reference(version = "${services.versions.user.v1}")
    private UserConsumerService userConsumerService;
    @Reference(version = "${services.versions.roles.v1}")
    private RolesConsumerService rolesConsumerService;
    @Reference(version = "${services.versions.permission.v1}")
    private PermissionConsumerService permissionConsumerService;

    @Value("${services.ports.user}")
    private String userPort;
    @Value("${services.ports.roles}")
    private String rolesPort;
    @Value("${services.ports.permission}")
    private String permissionPort;
    @RequestMapping(value = "user",method = RequestMethod.GET)
    public String user(String path){
        System.out.println(123);
        RpcContext.getContext().setAttachment("userKey", "userValue");
        //远程调用
        userConsumerService.info();
        return getRequest(userPort,path);
    }

    @RequestMapping(value = "role",method = RequestMethod.GET)
    public String roles(String path){
        //远程调用
        rolesConsumerService.info();
        //本端是否为消费端
        return getRequest(rolesPort,path);
    }

    @RequestMapping(value = "permission",method = RequestMethod.GET)
    public String permission(String path){
        //远程调用
        permissionConsumerService.info();
        //本端是否为消费端
        return getRequest(permissionPort,path);
    }
    /**
     * 获取请求地址
     * @param port  服务器端口
     * @param path 请求路径
     * @return
     */
    private String getRequest(String port,String path){
        boolean isConsumerSide= RpcContext.getContext().isConsumerSide();
        if(isConsumerSide){
            //获取最后一次调用的提供方ip
            String serverIP=RpcContext.getContext().getRemoteHost();
            System.out.println(serverIP);
            String url= String.format("redirect:http://%s:%s%s",serverIP,port,path);
            System.out.println(url);
            return url;
        }
        return null;
    }
    @RequestMapping("index")
    public String Index(@SessionAttribute(value = "user",required = false) User user){
        //此处表示未登录
        if(user==null){
            return "login";
        }
        //已登录
        return "Index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String account, @RequestParam String password, HttpSession session, HttpServletResponse response, RedirectAttributes rda){
        User user=loginService.findUserByAccount(account);
        //判断账号是否可用

        if(user!=null&&user.getEnable()) {
            //判断密码是否匹配
            if (user.getPassword().equals(DigestUtils.md5Hex(password))) {
                Map<String,Object> map= loginService.getLoginMessage(user.getId());
                System.out.println(222);
                Map<String,Object> result=loginService.getLoginMessage(user.getId());
                List<Menu> menuList=(List<Menu>) result.get("menus");
                Set<String> keys=(Set<String>) result.get("keys");
                Set<String> urls=(Set<String>) result.get("urls");
                session.setAttribute("user",user);
                session.setAttribute("menus",menuList);
                session.setAttribute("keys",keys);
                session.setAttribute("urls",urls);
                //RpcContext.getContext().setAttachment("test","隐式传参");
                System.out.println(333);
                Cookie cookie = new Cookie("userID",user.getId().toString());
                Cookie cookie1 = new Cookie("username","hhh");
                cookie.setDomain("");
                cookie.setPath("/");
                response.addCookie(cookie);
                response.addCookie(cookie1);

            }else{
                rda.addFlashAttribute("error","账号和密码不匹配");
            }
        }else{
            rda.addFlashAttribute("error","账号不可用");
        }

        return "redirect:/index";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/index";
    }

    @RequestMapping("/menus")
    @ResponseBody
    public List<Menu> menu(HttpSession session){
        List<Menu> menuList;
        menuList=(List<Menu>)session.getAttribute("menus");
        List<Menu> menus=menuList;
        getChildren(menuList,menus);
        List<Menu> resultMenu=new ArrayList<>();
        menus.forEach(menu->resultMenu.add(menu));
        //防止界面刷新 菜单显示不全
        resultMenu.removeIf(menu->menu.getParentId()!=null);
        return resultMenu;
    }

    /**
     * 将list递归生成父子节点的形式
     * @param menuList
     */
    private void getChildren(@SessionAttribute("menus")List<Menu> menuList,List<Menu> menuList2) {
        menuList2.forEach(menu -> {
            List<Menu> children = new ArrayList<>();
            menuList.forEach(node -> {
                if (node.getParentId() != null && node.getParentId().equals(menu.getId())) {
                    children.add(node);
                }
            });
            if (!children.isEmpty()) {
                getChildren(menuList, children);
                menu.setChildren(children);
            } else {
                menu.setChildren(null);
            }
        });
    }
    @RequestMapping("/reject")
    public String reject(){
        return "reject";
    }
}
