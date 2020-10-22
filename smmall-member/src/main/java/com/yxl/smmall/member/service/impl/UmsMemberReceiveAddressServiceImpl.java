package com.yxl.smmall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;

import com.yxl.smmall.member.dao.UmsMemberReceiveAddressDao;
import com.yxl.smmall.member.entity.UmsMemberReceiveAddressEntity;
import com.yxl.smmall.member.service.UmsMemberReceiveAddressService;


@Service("umsMemberReceiveAddressService")
public class UmsMemberReceiveAddressServiceImpl extends ServiceImpl<UmsMemberReceiveAddressDao, UmsMemberReceiveAddressEntity> implements UmsMemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UmsMemberReceiveAddressEntity> page = this.page(
                new Query<UmsMemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<UmsMemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<UmsMemberReceiveAddressEntity> getByUserId(Long id) {
        List<UmsMemberReceiveAddressEntity> memberAdd = this.list(new QueryWrapper<UmsMemberReceiveAddressEntity>().eq("member_id", id));

        return memberAdd;
    }

}