package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.User;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM sys_user WHERE username = #{username} LIMIT 1")
    User selectByUsernameIncludingDeleted(String username);

    @Select("SELECT * FROM sys_user WHERE email = #{email} LIMIT 1")
    User selectByEmailIncludingDeleted(String email);

    @Select("SELECT * FROM sys_user WHERE id = #{id} AND status = 'ACTIVE' AND deleted = 0 LIMIT 1")
    User selectActiveById(Long id);
}
