package com.yxl.smmall.coupon.service.impl;

import com.yxl.common.to.MemberPrice;
import com.yxl.common.to.SkuReductionTO;
import com.yxl.smmall.coupon.entity.SmsMemberPriceEntity;
import com.yxl.smmall.coupon.entity.SmsSkuLadderEntity;
import com.yxl.smmall.coupon.service.SmsMemberPriceService;
import com.yxl.smmall.coupon.service.SmsSkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;

import com.yxl.smmall.coupon.dao.SmsSkuFullReductionDao;
import com.yxl.smmall.coupon.entity.SmsSkuFullReductionEntity;
import com.yxl.smmall.coupon.service.SmsSkuFullReductionService;


@Service("smsSkuFullReductionService")
public class SmsSkuFullReductionServiceImpl extends ServiceImpl<SmsSkuFullReductionDao, SmsSkuFullReductionEntity> implements SmsSkuFullReductionService {
    @Autowired
    SmsSkuLadderService smsSkuLadderService;
//    @Autowired 由于上面有继承关系所以在这里就不进行注入了
//    SmsSkuFullReductionService smsSkuFullReductionService;

    @Autowired
    SmsMemberPriceService smsMemberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsSkuFullReductionEntity> page = this.page(
                new Query<SmsSkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SmsSkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTO skuReductionTO) {

        //1.保存满减打折，会员价格
        SmsSkuLadderEntity smsSkuLadderEntity = new SmsSkuLadderEntity();
        smsSkuLadderEntity.setSkuId(skuReductionTO.getSkuId());
        smsSkuLadderEntity.setFullCount(skuReductionTO.getFullCount());
        smsSkuLadderEntity.setDiscount(skuReductionTO.getDiscount());
        smsSkuLadderEntity.setAddOther(skuReductionTO.getCountStatus());
        if (skuReductionTO.getFullCount() > 0 ){
            smsSkuLadderService.save(smsSkuLadderEntity);
        }

        //2,保存满减的信息
        SmsSkuFullReductionEntity smsSkuFullReductionEntity = new SmsSkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTO,smsSkuFullReductionEntity);
        if (smsSkuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1){
            this.save(smsSkuFullReductionEntity);
        }

        //3,保存会员价格
        List<MemberPrice> memberPrices = skuReductionTO.getMemberPrices();
        List<SmsMemberPriceEntity> collect = memberPrices.stream().map(item -> {
            SmsMemberPriceEntity memberPriceEntity = new SmsMemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTO.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            return memberPriceEntity;
        }).filter(item->{
            return item.getMemberPrice().compareTo(new BigDecimal("0"))==1;
        }).collect(Collectors.toList());
        smsMemberPriceService.saveBatch(collect);
    }

}