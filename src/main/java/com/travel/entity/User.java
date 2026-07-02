package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "用户")
public class User extends BaseEntity {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @JsonIgnore
    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "状态：0禁用 1正常")
    private Integer status;

    @Schema(description = "积分")
    private Integer points;

    @Schema(description = "会员等级")
    private Integer memberLevel;
}
