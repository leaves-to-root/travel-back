package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {

    int physicalDelete(Long userId, Long productId);
}