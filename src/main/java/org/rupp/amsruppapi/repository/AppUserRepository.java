package org.rupp.amsruppapi.repository;

import org.apache.ibatis.annotations.*;
import org.rupp.amsruppapi.model.entity.AppUser;
import org.rupp.amsruppapi.model.request.AppUserRequest;

import java.util.List;

@Mapper
public interface AppUserRepository {
    @Results(id = "appUserMapper", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "roles", column = "user_id", many = @Many(
                    select = "getAllRolesByUserId"
            ))
    })
    @Select("""
                SELECT * FROM app_users
                WHERE email = #{email};
            """)
    AppUser getUserByEmail(String email);

    @Select("""
                SELECT name FROM app_roles ar
                INNER JOIN user_role ur
                ON ar.role_id = ur.role_id
                WHERE user_id = #{userId};
            """)
    List<String> getAllRolesByUserId(Long userId);

    @Select("""
                INSERT INTO app_users
                VALUES (default, #{request.username}, #{request.email}, #{request.password})
                RETURNING *
            """)
    @ResultMap("appUserMapper")
    AppUser register(@Param("request") AppUserRequest request);

    @Insert("""
                INSERT INTO user_role
                VALUES (#{userId}, #{roleId})
            """)
    void insertUserIdAndRoleId(Long roleId, Long userId);

    @Select("""
                SELECT * FROM app_users
                WHERE user_id = #{userId}
            """)
    @ResultMap("appUserMapper")
    AppUser getUserById(Long userId);
}
