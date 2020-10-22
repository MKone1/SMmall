package com.yxl.smmall.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;

import com.yxl.smmall.coupon.dao.SmsCouponDao;
import com.yxl.smmall.coupon.entity.SmsCouponEntity;
import com.yxl.smmall.coupon.service.SmsCouponService;


@Service("smsCouponService")
public class SmsCouponServiceImpl extends ServiceImpl<SmsCouponDao, SmsCouponEntity> implements SmsCouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsCouponEntity> page = this.page(
                new Query<SmsCouponEntity>().getPage(params),
                new QueryWrapper<SmsCouponEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有的优惠卷
     * @return
     */
    @Override
    public List<SmsCouponEntity> getCoupon() {
        List<SmsCouponEntity> list = this.baseMapper.selectList(new QueryWrapper<SmsCouponEntity>().lambda());
        List<SmsCouponEntity> collect = list.stream().filter(item -> item.getPublish() == 1
                && item.getUseCount()< item.getNum()).collect(Collectors.toList());
        return collect;


    }

}