package com.travel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin")
@Schema(description = "管理员")
public class Admin extends BaseEntity {

    @Schema(description = "账号")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @JsonIgnore
    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "状态：0禁用 1正常")
    private Integer status;
}
