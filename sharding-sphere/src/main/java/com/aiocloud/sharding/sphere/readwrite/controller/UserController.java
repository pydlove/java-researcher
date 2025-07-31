package com.aiocloud.sharding.sphere.readwrite.controller;

import com.aiocloud.sharding.sphere.readwrite.mapper.SystemUserMapper;
import com.aiocloud.sharding.sphere.readwrite.po.SystemUser;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final SystemUserMapper userMapper;

    /**
     * 写入操作会自动路由到主库
     *
     * @since 1.0.0
     *
     * @param: user
     * @return: com.aiocloud.sharding.sphere.readwrite.po.SystemUser
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-06-03 16:03
     */
    @PostMapping("/save")
    public SystemUser createUser(@RequestBody SystemUser user) {

        userMapper.save(user);
        return user;
    }

    /**
     * 读取操作会自动路由到从库
     *
     * @since 1.0.0
     *
     * @param: id
     * @return: com.aiocloud.sharding.sphere.readwrite.po.SystemUser
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-06-03 16:03
     */
    @GetMapping("/{id}")
    public SystemUser getUser(@PathVariable Long id) {

        return userMapper.selectById(id);
    }

    /**
     * 读取操作会自动路由到从库
     *
     * @since 1.0.0
     *
     * @param: email
     * @return: java.util.List<com.aiocloud.sharding.sphere.readwrite.po.SystemUser>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-06-03 16:04
     */
    @GetMapping("/search")
    public List<SystemUser> searchUsers(@RequestParam String email) {

        return userMapper.selectByEmail(email);
    }

    /**
     * 强制从主库读取
     *
     * @since 1.0.0
     *
     * @param: id
     * @return: com.aiocloud.sharding.sphere.readwrite.po.SystemUser
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-06-03 16:04
     */
    @GetMapping("/master/{id}")
    public SystemUser getFromMaster(@PathVariable Long id) {

        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setWriteRouteOnly();
            return userMapper.selectById(id);
        }
    }
}