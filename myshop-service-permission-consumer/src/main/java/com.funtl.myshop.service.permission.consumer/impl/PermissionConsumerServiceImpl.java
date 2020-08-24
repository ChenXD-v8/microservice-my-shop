package com.funtl.myshop.service.permission.consumer.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.funtl.myshop.service.permission.api.PermissionConsumerService;

@Service(version = "${services.versions.permission.v1}")
public class PermissionConsumerServiceImpl implements PermissionConsumerService {
    @Override
    public void info() {

    }
}
