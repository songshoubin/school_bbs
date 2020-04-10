package com.example.service.impl;

import com.example.entity.UserCollection;
import com.example.mapper.UserCollectionMapper;
import com.example.service.UserCollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {

}
