package com.example.service.impl;

import com.example.entity.UserAction;
import com.example.mapper.UserActionMapper;
import com.example.service.UserActionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserActionServiceImpl extends ServiceImpl<UserActionMapper, UserAction> implements UserActionService {

}
