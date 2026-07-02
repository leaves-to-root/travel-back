package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.ProductSchedule;
import com.travel.mapper.ProductScheduleMapper;
import com.travel.service.ProductScheduleService;
import org.springframework.stereotype.Service;

@Service
public class ProductScheduleServiceImpl extends ServiceImpl<ProductScheduleMapper, ProductSchedule> implements ProductScheduleService {
}
