package com.cold73.campuserrand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体（对应 t_user 表）
 * 统一存储普通用户 / 跑腿员 / 管理员，并承载微信登录相关字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
public class User {

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（可选，后台登录用） */
    private String username;

    /** 密码（BCrypt 哈希） */
    private String password;

    /** 手机号 */
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 角色：0-普通用户，1-跑腿员，2-管理员 */
    private Integer role;

    /** 微信 openid（小程序内唯一标识） */
    private String openid;

    /** 微信 unionid（跨应用统一标识） */
    private String unionid;

    /** 微信 session_key（解密用户数据用） */
    private String sessionKey;

    /** 状态：0-禁用，1-正常 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
