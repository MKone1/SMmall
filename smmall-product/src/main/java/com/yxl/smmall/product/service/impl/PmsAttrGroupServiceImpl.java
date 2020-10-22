package com.yxl.smmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.smmall.product.dao.PmsAttrGroupDao;
import com.yxl.smmall.product.entity.PmsAttrEntity;
import com.yxl.smmall.product.entity.PmsAttrGroupEntity;
import com.yxl.smmall.product.service.PmsAttrGroupService;
import com.yxl.smmall.product.service.PmsAttrService;
import com.yxl.smmall.product.vo.AttrGroupWithAttrsVO;
import com.yxl.smmall.product.vo.SkuInfoVo;
import com.yxl.smmall.product.vo.SpuItemSaleAttrGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("pmsAttrGroupService")
public class PmsAttrGroupServiceImpl extends ServiceImpl<PmsAttrGroupDao, PmsAttrGroupEntity> implements PmsAttrGroupService {


    @Autowired
    PmsAttrService pmsAttrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsAttrGroupEntity> page = this.page(
                new Query<PmsAttrGroupEntity>().getPage(params),
                new QueryWrapper<PmsAttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        //select * from pms_attr_group where catelog_id=?
        // and (attr_group_id = key or attr_group_name like %key%)
        QueryWrapper<PmsAttrGroupEntity> wrapper = new QueryWrapper<PmsAttrGroupEntity>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((object) -> {
                object.eq("attr_group_id", key)
                        .or().like("attr_group_name", key);
            });

        }
        if (catelogId == 0) {
            IPage<PmsAttrGroupEntity> page = this.page(new Query<PmsAttrGroupEntity>().getPage(params)
                    , wrapper);
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", catelogId);
            IPage<PmsAttrGroupEntity> page = this.page(new Query<PmsAttrGroupEntity>().getPage(params)
                    , wrapper);
            return new PageUtils(page);
        }


    }

    /**
     * 根据分类ID查出所有分组以及这些分组的所有属性
     *
     * @param catLogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithByCatelogId(Long catLogId) {
        //1、查询分组信息
        List<PmsAttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<PmsAttrGroupEntity>().eq("catelog_id", catLogId));

        //2、查询所有属性
        List<AttrGroupWithAttrsVO> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVO attrsVo = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(group,attrsVo);
            List<PmsAttrEntity> attrs = pmsAttrService.getRelation(attrsVo.getAttrGroupId());
            attrsVo.setAttrsList(attrs);
            return attrsVo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public   List<SpuItemSaleAttrGroupVo>getAttrGroupWithBySpuId(Long spuId, Long catalogId) {
        /** 联合查询的SQL语句
         SELECT pav.spu_id,ag.attr_group_name,ag.attr_group_id ,aar.attr_id,attr.attr_name,pav.attr_value
         FROM pms_attr_group ag
         LEFT JOIN pms_attr_attrgroup_relation aar ON aar.attr_group_id = ag.attr_group_id
         LEFT JOIN pms_attr attr ON attr.attr_id = aar.attr_id
         LEFT JOIN
         pms_product_attr_value pav ON attr.attr_id = pav.attr_id
         WHERE ag.catelog_id = 225 AND pav.spu_id =10
         */


        //1,所有属性的分组信息以及当前分组下的所有属性对应的值
        PmsAttrGroupDao baseMapper = this.getBaseMapper();
        List<SpuItemSaleAttrGroupVo> vos  = baseMapper.getAttrGroupWithBySpuId(spuId,catalogId);

return vos;
    }

}