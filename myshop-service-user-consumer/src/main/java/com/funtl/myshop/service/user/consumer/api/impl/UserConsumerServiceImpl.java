package com.funtl.myshop.service.user.consumer.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.funtl.myshop.service.user.api.UserConsumerService;
import sun.misc.Contended;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

@Service(version = "${services.versions.user.v1}")
public class UserConsumerServiceImpl implements UserConsumerService {

    public String value;
    @Override
    public void info() {
        System.out.println("我在这里");
        value = RpcContext.getContext().getAttachment("userKey");
        System.out.println("value=111"+RpcContext.getContext().getAttachment("userKey"));
    }
    @Override
    public String getContext(){
        System.out.println("value="+value);
        return value;
    }
}
