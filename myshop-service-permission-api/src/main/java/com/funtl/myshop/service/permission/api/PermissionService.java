package com.funtl.myshop.service.permission.api;

import com.funtl.myshop.commons.domain.Permission;
import com.funtl.myshop.commons.domain.common.JsonResult;
import org.hibernate.validator.constraints.EAN;

import java.util.List;
import java.util.Map;

public interface PermissionService {

    List<Map<String,Object>>  list();
    Permission findById(Long id);
    void save(Permission permission);
    JsonResult deleteById(Long id);
}
