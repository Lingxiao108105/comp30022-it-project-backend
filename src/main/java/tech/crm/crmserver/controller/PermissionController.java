package tech.crm.crmserver.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.crm.crmserver.common.enums.PermissionLevel;
import tech.crm.crmserver.common.response.ResponseResult;
import tech.crm.crmserver.dao.Permission;
import tech.crm.crmserver.service.PermissionService;
import tech.crm.crmserver.service.UserService;

/**
 * <p>
 *  controller
 * </p>
 *
 * @author Lingxiao
 * @since 2021-08-23
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    /**
     * delete a member from a department
     * @return
     */
    @DeleteMapping
    public ResponseResult<Object> deleteMember(@RequestParam("user_id") Integer user_id,
                                               @RequestParam("department_id") Integer department_id){
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        //executor for delete action
        wrapper.eq("user_id",userService.getId());
        wrapper.eq("department_id",department_id);
        Permission executor = permissionService.getOne(wrapper);
        //executed person for delete action
        wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",user_id);
        wrapper.eq("department_id",department_id);
        Permission executed = permissionService.getOne(wrapper);

        //check the authority
        if(executor.getAuthorityLevel().getLevel() >= PermissionLevel.MANAGE.getLevel() &&
                executor.getAuthorityLevel().getLevel() > executed.getAuthorityLevel().getLevel()){
            permissionService.removeById(executed.getId());
        }
        return ResponseResult.suc("Successfully delete the member!");
    }


}

