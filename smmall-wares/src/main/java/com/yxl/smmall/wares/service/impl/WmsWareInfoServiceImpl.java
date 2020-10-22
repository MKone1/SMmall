package com.yxl.smmall.wares.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.common.utils.R;
import com.yxl.common.vo.FareVO;
import com.yxl.common.vo.MemberRespVo;
import com.yxl.common.vo.MenberAddVO;
import com.yxl.smmall.wares.dao.WmsWareInfoDao;
import com.yxl.smmall.wares.entity.WmsWareInfoEntity;
import com.yxl.smmall.wares.feign.MemberAddrFeign;
import com.yxl.smmall.wares.service.WmsWareInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;


@Service("wmsWareInfoService")
public class WmsWareInfoServiceImpl extends ServiceImpl<WmsWareInfoDao, WmsWareInfoEntity> implements WmsWareInfoService {

    @Resource
    private MemberAddrFeign memberAddrFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsWareInfoEntity> page = this.page(
                new Query<WmsWareInfoEntity>().getPage(params),
                new QueryWrapper<WmsWareInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 对仓库数据进行数据检索，实现模糊查询，
     *
     * @param params 前端传递过来的参数
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<WmsWareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("name", key).or().like("address", key);

            });
        }


        IPage<WmsWareInfoEntity> page = this.page(
                new Query<WmsWareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

    @Override
    public FareVO getFare(Long addrId) {
        R addrInfo = memberAddrFeign.Addrinfo(addrId);
        FareVO fareVO = new FareVO();
        MenberAddVO umsMemberReceiveAddress = addrInfo.getData("umsMemberReceiveAddress", new TypeReference<MenberAddVO>() {
        });
        if (umsMemberReceiveAddress != null) {
            String mobile = umsMemberReceiveAddress.getPhone();
            //将电话号码的后几位做为运费
            String s = mobile.substring(mobile.length() - 1, mobile.length());
            fareVO.setFare( new BigDecimal(s));
            fareVO.setMemberAddressVO(umsMemberReceiveAddress);

            return fareVO;
        }
     return null;
    }

}