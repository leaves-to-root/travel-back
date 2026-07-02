package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.TravelNote;
import com.travel.mapper.TravelNoteMapper;
import com.travel.service.TravelNoteService;
import org.springframework.stereotype.Service;

@Service
public class TravelNoteServiceImpl extends ServiceImpl<TravelNoteMapper, TravelNote> implements TravelNoteService {
}
