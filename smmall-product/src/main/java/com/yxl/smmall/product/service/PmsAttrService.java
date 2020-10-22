package com.yxl.smmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.product.entity.PmsAttrEntity;
import com.yxl.smmall.product.vo.AttrGroupRelationVO;
import com.yxl.smmall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
public interface PmsAttrService extends IService<PmsAttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo pmsAttr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrVo getAttrInfo(Long attrId);

    void updatAttr(AttrVo pmsAttr);

    List<PmsAttrEntity> getRelation(Long attgroupId);

    void deleteRelation(AttrGroupRelationVO[] vos);

    PageUtils getNoRalation(Map<String, Object> params, Long attgroupId);

    void saveBatch(List<AttrGroupRelationVO> vos);

    /**
     * 在指定的所有的属性集合里面，跳出检索属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrs(List<Long> attrIds);
}

