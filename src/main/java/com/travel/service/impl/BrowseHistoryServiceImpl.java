package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.BrowseHistory;
import com.travel.mapper.BrowseHistoryMapper;
import com.travel.service.BrowseHistoryService;
import org.springframework.stereotype.Service;

@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> implements BrowseHistoryService {
}
