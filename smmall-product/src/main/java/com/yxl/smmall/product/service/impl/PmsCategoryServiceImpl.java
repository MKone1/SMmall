package com.yxl.smmall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;

import com.yxl.smmall.product.dao.PmsCategoryDao;
import com.yxl.smmall.product.entity.PmsCategoryEntity;
import com.yxl.smmall.product.service.PmsCategoryService;


@Service("pmsCategoryService")
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryDao, PmsCategoryEntity> implements PmsCategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsCategoryEntity> page = this.page(
                new Query<PmsCategoryEntity>().getPage(params),
                new QueryWrapper<PmsCategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<PmsCategoryEntity> listWith() {
        /**
         * 1,查出全部分类
         * 2，组装成父子的树形结构
         * 原始调用Dao层：
         * @Autowired
         *     PmsCategoryDao pmsCategoryDao;
         * 由于当前类继承了serviceImpl，泛型将Dao层传递给了ServiceImple
         * public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {
         *     protected Log log = LogFactory.getLog(this.getClass());
         *     @Autowired
         *     protected M baseMapper；
         *   所以basemapper就是    PmsCategoryDao pmsCategoryDao;
         *
         *   利用了一个SQL语句查询出全部信息，利用递归分类显示0
         */
        //查出所有
        List<PmsCategoryEntity> list = baseMapper.selectList(null);
        //一级分类
//        List<PmsCategoryEntity> listtree = list.stream().filter((entity) -> {
//            return entity.getParentCid() == 0;
//        }).collect(Collectors.toList());
        List<PmsCategoryEntity> listtree = list.stream().filter((entity) -> {
            return entity.getParentCid() == 0;
        }).map((menu)->{
            menu.setChildren(getChildren(menu,list));
            //通过stream的map映射将子分类赋值给children，最终把当前菜单返回回去
            return  menu;
        }).sorted((menu1,menu2)->{
  //          return menu1.getSort() - menu2.getSort();
           return (menu1.getSort() == null ? 0: menu1.getSort()) - (menu2.getSort() == null ? 0:menu2.getSort());
        }).collect(Collectors.toList());
        //.sorted进行排序
        return listtree;

    }


    /**
     *  这里是用于删除菜单分类
     * @param asList
     */
    @Override
    public void removeMebuById(List<Long> asList) {
        //TODO ,检查当前删除的菜单是否被其它地方引用
        //采用逻辑删除，即采用数据库表中的某一字段表示被删除
//        物理删除，即采用直接删除数据，在数据库就再也无法找到
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 获取某一个菜单的子菜单
     * 递归查找所有菜单的子菜单
     */
    private  List<PmsCategoryEntity> getChildren(PmsCategoryEntity root,List<PmsCategoryEntity> all){

        List<PmsCategoryEntity> children = all.stream().filter(categoryEntity -> {
            //返回父分类等于分类ID的
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            //2、菜单的排序
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }
}