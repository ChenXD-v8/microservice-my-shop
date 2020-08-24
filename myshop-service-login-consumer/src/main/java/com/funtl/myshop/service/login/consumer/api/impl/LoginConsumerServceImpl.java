package com.funtl.myshop.service.login.consumer.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.funtl.myshop.service.login.api.LoginConsumerService;

@Service(version = "${services.versions.login.v1}")
public class LoginConsumerServceImpl implements LoginConsumerService {
    @Override
    public void info() {
    }
}
