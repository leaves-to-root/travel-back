package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int physicalDelete(@Param("userId") Long userId, @Param("productId") Long productId);
}