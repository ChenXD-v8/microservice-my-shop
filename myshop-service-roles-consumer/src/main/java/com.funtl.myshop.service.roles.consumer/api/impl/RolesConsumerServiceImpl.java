package com.funtl.myshop.service.roles.consumer.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.funtl.myshop.service.roles.api.RolesConsumerService;
@Service(version = "${services.versions.roles.v1}")
public class RolesConsumerServiceImpl implements RolesConsumerService {
    @Override
    public void info() {

    }
}
