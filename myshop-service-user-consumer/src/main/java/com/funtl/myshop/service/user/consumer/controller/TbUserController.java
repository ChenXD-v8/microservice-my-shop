package com.funtl.myshop.service.user.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.funtl.myshop.commons.domain.Role;
import com.funtl.myshop.commons.domain.User;
import com.funtl.myshop.commons.domain.common.JsonResult;
import com.funtl.myshop.commons.domain.common.Menu;
import com.funtl.myshop.service.login.api.LoginService;
import com.funtl.myshop.service.user.api.TbUserService;
import com.funtl.myshop.service.user.api.UserConsumerService;
import com.funtl.myshop.service.user.consumer.api.impl.UserConsumerServiceImpl;
import com.sun.deploy.net.HttpRequest;
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
import java.util.*;

@Controller
@RequestMapping(value = "system/user")
public class TbUserController {

    @Reference(version = "${services.versions.user.v1}")
    private TbUserService tbUserService;

    @Reference(version = "${services.versions.user.v1}")
    private UserConsumerService userConsumerService;

    /**
     * 跳转到列表页
     *
     * @return
     */
    @RequestMapping
    public String index(HttpServletRequest request, HttpSession session) {
        request.getCookies();
        Cookie[] cookie = request.getCookies();//获取的是请求里的所有cookie组成的数组
        System.out.println("cookie");
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
        Map<String,Object> result=tbUserService.getLoginMessage(id);
        Set<String> keys=(Set<String>) result.get("keys");
        Set<String> urls=(Set<String>) result.get("urls");
        session.setAttribute("keys",keys);
        session.setAttribute("urls",urls);
        return "system/user";
    }
    @ResponseBody
    @RequestMapping(value = "list")
    public List<Map<String, Object>> list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int rows) {
        List<Map<String, Object>> rbacUsers=tbUserService.list(page,rows);
        return rbacUsers;
    }


    @RequestMapping({"/form", "/load"})
    public String form(Long id, Model model) {
        if (id != null) {
            Map<String,Object> map=tbUserService.findById(id);
            User user=(User) map.get("user");
            List<Long> roleIds=(List<Long>) map.get("roleIds");
            model.addAttribute("user", user);
            model.addAttribute("roleIds",roleIds);
            System.out.println(roleIds);
        }
        System.out.println(11111);
        List<Map<String,Object>> roleList=tbUserService.findRolesByenable(true);
        System.out.println(121221);
        model.addAttribute("roless",roleList);
        return "system/user/form";
    }

    /**
     * 注册或修改用户
     * @param user 表单用户
     * @param roles  角色
     * @param br
     * @return
     */
    @RequestMapping({"/save", "/update"})
    @ResponseBody
    @Transactional
    public JsonResult form(@Valid User user,@RequestParam(name = "roless",required = false) String[] roles,  BindingResult br) {
        System.out.println(123);
        for(ObjectError aa:br.getAllErrors()){
            System.out.println(aa.toString());
        }
        Set<Role> roleList=new HashSet<>();
        if(roles!=null){
            for(String ss:roles){
                Role role=new Role();
                role.setId(Long.parseLong(ss));
                roleList.add(role);
            }
        }
        user.setRoles(roleList);
        if (!br.hasErrors()) {
            if (user.getId() == null) {
                //md5加密密码
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            }else{
                User org=(User) tbUserService.findById(user.getId()).get("user");
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                } else {
                    user.setPassword(org.getPassword());
                }
            }
            System.out.println(678);
            tbUserService.insert(user);
            System.out.println(3345);
            return JsonResult.success();
        } else {
            return JsonResult.error("校验不通过");
        }
    }

    /**
     * 核验账号是否已被注册
     * @param account  用户名
     * @return
     */
    @PostMapping("/check")
    @ResponseBody
    public String check(String account) {
        System.out.println("账号校验");
      return  tbUserService.checkAccount(account);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    @Transactional
    public JsonResult delete(Long id) {
        if (tbUserService.deleteById(id)) {
            return JsonResult.success();
        } else {
            return JsonResult.error("校验不通过");
        }
    }


}
