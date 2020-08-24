package com.funtl.myshop.api.gateway.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.funtl.myshop.service.login.api.LoginConsumerService;
import com.funtl.myshop.service.permission.api.PermissionConsumerService;
import com.funtl.myshop.service.roles.api.RolesConsumerService;
import com.funtl.myshop.service.user.api.UserConsumerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

@Controller
@RequestMapping(value = "router")
public class RouterController {
    @Reference(version = "${services.versions.user.v1}")
    private UserConsumerService userConsumerService;
    @Reference(version = "${services.versions.login.v1}")
    private LoginConsumerService loginConsumerService;
    @Reference(version = "${services.versions.roles.v1}")
    private RolesConsumerService rolesConsumerService;
    @Reference(version = "${services.versions.permission.v1}")
    private PermissionConsumerService permissionConsumerService;


    @Value("${services.ports.user}")
    private String userPort;
    @Value("${services.ports.roles}")
    private String rolesPort;
    @Value("${services.ports.login}")
    private String loginPort;
    @Value("${services.ports.permission}")
    private String permissionPort;
    @RequestMapping(value = "user",method = RequestMethod.GET)
    public String user(String path){
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
    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String login(String path){
        //远程调用
        loginConsumerService.info();
        return getRequest(loginPort,path);
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
}
