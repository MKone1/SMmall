package com.yxl.smmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.constant.ProductConstant;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.smmall.product.dao.PmsAttrAttrgroupRelationDao;
import com.yxl.smmall.product.dao.PmsAttrDao;
import com.yxl.smmall.product.dao.PmsAttrGroupDao;
import com.yxl.smmall.product.dao.PmsCategoryDao;
import com.yxl.smmall.product.entity.PmsAttrAttrgroupRelationEntity;
import com.yxl.smmall.product.entity.PmsAttrEntity;
import com.yxl.smmall.product.entity.PmsAttrGroupEntity;
import com.yxl.smmall.product.entity.PmsCategoryEntity;
import com.yxl.smmall.product.service.PmsAttrService;
import com.yxl.smmall.product.service.PmsCategoryService;
import com.yxl.smmall.product.vo.AttrGroupRelationVO;
import com.yxl.smmall.product.vo.AttrRespVo;
import com.yxl.smmall.product.vo.AttrVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("pmsAttrService")
public class PmsAttrServiceImpl extends ServiceImpl<PmsAttrDao, PmsAttrEntity> implements PmsAttrService {
    @Autowired
    PmsAttrAttrgroupRelationDao pmsAttrAttrgroupRelationDao;
    @Autowired
    PmsAttrGroupDao pmsAttrGroupDao;
    @Autowired
    PmsCategoryDao pmsCategoryDao;
    @Autowired
    PmsCategoryService pmsCategoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsAttrEntity> page = this.page(
                new Query<PmsAttrEntity>().getPage(params),
                new QueryWrapper<PmsAttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo Attr) {
        PmsAttrEntity pmsAttrEntity = new PmsAttrEntity();
//     pmsAttrEntity.setAttrName(pmsAttr.getAttrName());这样写太麻烦了
//        使用spring中的一个Bean工具类直接将属性进行拷贝，前提是两个实体类之间必须字段名称一致

        BeanUtils.copyProperties(Attr, pmsAttrEntity);
//        1，保存基本数
        this.save(pmsAttrEntity);
//2,保存关联关系
        if (Attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            PmsAttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new PmsAttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(Attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(pmsAttrEntity.getAttrId());
            pmsAttrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
/**
 * "base".equalsIgnoreCase(type)   :无视大小写
 * 三元运算
 */

//     无论是不是条件查询都会进行判断
        QueryWrapper<PmsAttrEntity> wrapper = new QueryWrapper<PmsAttrEntity>().
                eq("attr_type", "base".equalsIgnoreCase(type) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : 0);
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((querywrapper) -> {
                querywrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<PmsAttrEntity> page = this.page(
                new Query<PmsAttrEntity>().getPage(params), wrapper

        );
        PageUtils pageUtils = new PageUtils(page);
        List<PmsAttrEntity> records = page.getRecords();
        List<AttrRespVo> list = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
/** 1,设置分组的名字和分类的名字，通过查询pms_attr表中的attr_id字段，再去
 *  pms_attr_attrgroup_relation中查询到attr_group_id，
 *  根据这个查询到pms_attr_group中attr_group_name
 *  这里并没有使用多表查询，由于两张属性表可能数据巨大，
 *  中间关联表会造成大量的笛卡儿积
 */
//1,设置分组ID，判断存储的类型
            if ("base".equalsIgnoreCase(type)) {
                PmsAttrAttrgroupRelationEntity attr_id = pmsAttrAttrgroupRelationDao.selectOne(
                        new QueryWrapper<PmsAttrAttrgroupRelationEntity>()
                                .eq("attr_id", attrEntity.getAttrId()));
                if (attr_id != null) {
                    PmsAttrGroupEntity pmsAttrGroupEntity =
                            pmsAttrGroupDao.selectById(attr_id.getAttrGroupId());
                    attrRespVo.setGroupName(pmsAttrGroupEntity.getAttrGroupName());
                }
            }


//2，设置分类ID
            PmsCategoryEntity pmsCategoryEntity = pmsCategoryDao.selectById(attrEntity.getCatelogId());
            if (pmsCategoryEntity != null) {
                attrRespVo.setCatelogName(pmsCategoryEntity.getName());
            }


            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(list);
        return pageUtils;

    }

    @Override
    public AttrVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        PmsAttrEntity pmsAttrEntity = this.getById(attrId);
        BeanUtils.copyProperties(pmsAttrEntity, attrRespVo);
        //设置分组信息

        if (pmsAttrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            PmsAttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                    pmsAttrAttrgroupRelationDao.selectOne(new QueryWrapper<PmsAttrAttrgroupRelationEntity>()
                            .eq("attr_id", pmsAttrEntity.getAttrId()));
            if (attrAttrgroupRelationEntity != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                PmsAttrGroupEntity pmsAttrGroupEntity = pmsAttrGroupDao.
                        selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (pmsAttrGroupEntity != null) {
                    attrRespVo.setGroupName(pmsAttrGroupEntity.getAttrGroupName());
                }

            }
        }

//查询分类信息
        Long catelogId = pmsAttrEntity.getCatelogId();
        Long[] catelogPath = pmsCategoryService.findCatelogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        PmsCategoryEntity pmsCategoryEntity = pmsCategoryDao.selectById(catelogId);
        if (pmsCategoryEntity != null) {
            attrRespVo.setCatelogName(pmsCategoryEntity.getName());
        }
        return attrRespVo;
    }

    @Transactional
    @Override
    public void updatAttr(AttrVo pmsAttr) {
        PmsAttrEntity pmsAttrEntity = new PmsAttrEntity();
        BeanUtils.copyProperties(pmsAttr, pmsAttrEntity);
        this.updateById(pmsAttrEntity);
//        通过利用枚举判断是否需要进行修改分组信息
        if (pmsAttrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            PmsAttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new PmsAttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(pmsAttr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(pmsAttr.getAttrId());
            Integer attr_id = pmsAttrAttrgroupRelationDao.selectCount(new QueryWrapper<PmsAttrAttrgroupRelationEntity>().
                    eq("attr_id", pmsAttr.getAttrId()));
            if (attr_id > 0) {
                //修改分组信息

                pmsAttrAttrgroupRelationDao.update(attrAttrgroupRelationEntity
                        , new QueryWrapper<PmsAttrAttrgroupRelationEntity>().eq("attr_id", pmsAttr.getAttrId()));
            } else {
                pmsAttrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }


    }

    /**
     * 根据分组ID查找关联的基本属性信息
     *
     * @param attgroupId
     * @return
     */
    @Override
    public List<PmsAttrEntity> getRelation(Long attgroupId) {
        //1，查找中间表中的attr_id

        List<PmsAttrAttrgroupRelationEntity> attrgroupRelationEntities = pmsAttrAttrgroupRelationDao.selectList
                (new QueryWrapper<PmsAttrAttrgroupRelationEntity>()
                .eq("attr_group_id", attgroupId));
        List<Long> attr_ids = attrgroupRelationEntities.stream().map((list) -> {
            return list.getAttrId();
        }).collect(Collectors.toList());
        if(attr_ids == null || attr_ids.size() == 0){
            return null;
        }
        //        2，根据Attr_id去pms_attr表中查询详细信息
        Collection<PmsAttrEntity> pmsAttrEntities = this.listByIds(attr_ids);
        return (List<PmsAttrEntity>) pmsAttrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
        System.out.println(vos.length);
        List<PmsAttrAttrgroupRelationEntity> collect = Arrays.asList(vos).stream().map((item) -> {
            PmsAttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new PmsAttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        try {
            pmsAttrAttrgroupRelationDao.deleteBatchRelation(collect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("****************");
    }

    /*获取到当前没有关联的所有属性
     * @param params
     * @param attgroupId
     * @return
     */
    @Override
    public PageUtils getNoRalation(Map<String, Object> params, Long attgroupId) {
        //1、当前分组只能关联自己所属的分类里面的所有属性
        PmsAttrGroupEntity attrGroupEntity = pmsAttrGroupDao.selectById(attgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2、当前分组只能关联别的分组没有引用的属性
        //2.1)、当前分类下的其他分组
        List<PmsAttrGroupEntity> group = pmsAttrGroupDao.selectList(new QueryWrapper<PmsAttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> collect = group.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        //2.2)、这些分组关联的属性
        List<PmsAttrAttrgroupRelationEntity> groupId = pmsAttrAttrgroupRelationDao.selectList(new QueryWrapper<PmsAttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = groupId.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        //2.3)、从当前分类的所有属性中移除这些属性；
        QueryWrapper<PmsAttrEntity> wrapper = new QueryWrapper<PmsAttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds!=null && attrIds.size()>0){
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if(!org.springframework.util.StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<PmsAttrEntity> page = this.page(new Query<PmsAttrEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    @Override
    public void saveBatch(List<AttrGroupRelationVO> vos) {
        List<PmsAttrAttrgroupRelationEntity> collect = vos.stream().map(item -> {
            PmsAttrAttrgroupRelationEntity relationEntity = new PmsAttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        for (PmsAttrAttrgroupRelationEntity attrGroupEntity: collect){
            pmsAttrAttrgroupRelationDao.insertAttrGroup(attrGroupEntity.getAttrId(),attrGroupEntity.getAttrGroupId());
        }

    }

    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIds) {
        /**
         * 需要执行的SQL语句：SELECT
         * attr_id
         * FROM
         * pms_attr
         * WHERE
         * attr_id
         * IN
         * (?)
         * AND
         * search_type=1
         */
        for (Long attrId : attrIds) {
            System.out.println("attrId"+attrId);
        }
        List<Long> longs = baseMapper.selectSearchAttrByIds(attrIds);
        System.out.println("longs:"+longs.size());
        return  longs;
    }

}