package com.yxl.smmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.smmall.product.dao.PmsCategoryDao;
import com.yxl.smmall.product.entity.PmsCategoryEntity;
import com.yxl.smmall.product.service.PmsCategoryBrandRelationService;
import com.yxl.smmall.product.service.PmsCategoryService;
import com.yxl.smmall.product.vo.Catelog2VO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 这里必须声明一点：
 * StringRedisTemplate与RedisTemplate的区别：
 * 1，两者的数据不共通
 * 2，RedisTemplate使用的是JdkSerializationRedisSerializer    存入数据会将数据先序列化成字节数组然后在存入Redis数据库。 
 *   StringRedisTemplate使用的是StringRedisSerializer
 *
 * @author SADSADSD
 */
@Service("pmsCategoryService")
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryDao, PmsCategoryEntity> implements PmsCategoryService {
    @Autowired
    PmsCategoryBrandRelationService pmsCategoryBrandRelationService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;

    //详情见注释
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
        }).map((menu) -> {
            menu.setChildren(getChildren(menu, list));
            //通过stream的map映射将子分类赋值给children，最终把当前菜单返回回去
            return menu;
        }).sorted((menu1, menu2) -> {
            //          return menu1.getSort() - menu2.getSort();
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
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
     * 找到catelogId的完整路径
     * {父/子/。。。。}
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> list = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, list);
//eg: 255 ,25 ,2 逆序的
        Collections.reverse(parentPath);
        System.out.println(parentPath);
        //通过将list转换成Long类型的数组
        return parentPath.toArray(new Long[parentPath.size()]);

    }

    /**
     * 级联更新所有关联的数据
     *
     * @param pmsCategory
     */
//    @CacheEvict(value = {"catalog"} ,key = "'getLavelCategory'")
    @CacheEvict(value = "catalog",allEntries = true)
    @CachePut
    @Transactional
    @Override
    public void updateCascade(PmsCategoryEntity pmsCategory) {
        this.updateById(pmsCategory);
        pmsCategoryBrandRelationService.updataCategory(pmsCategory.getCatId(), pmsCategory.getName());

    }
    @Cacheable(value = {"catalog"} ,key = "#root.method.name") //触发将数据保存到缓存的操作，key = "'levelcatalog'"
    @Override
    public List<PmsCategoryEntity> getLavelCategory() {
        System.out.println("getcatalog.......");
//        List<PmsCategoryEntity> list = baseMapper.selectByLavel1();
        List<PmsCategoryEntity> list = baseMapper.selectList(new QueryWrapper<PmsCategoryEntity>().
                eq("cat_level", 1).eq("show_status", 1));
        return list;

    }

    /**     "1":[
     *             {
     *             "catalog1Id":"1",
     *             "catalog3List":Array[4],
     *             "id":"1",
     *             "name":"电子书刊"
     *         },
     *  "catalog3List":[
     *                 {
     *                     "catalog2Id":"1",
     *                     "id":"1",
     *                     "name":"电子书"
     *                 },
     *                 Object{...},
     *                 Object{...},
     *                 Object{...}
     *             ],
     * @return
     */


    /**
     * 代码优化：
     * 1,优化前的代码过于冗余，对数据库操作多次，导致吞吐量变小速度变慢；
     * 将全部的分类信息一次查询出来，封装再通过对该数据进行检索查询，减少了对数据库的操作
     * 2,通过将目录分类保存在Redis缓存中
     * 即时性，数据一致性要求不高；
     * 访问量大且更新频率不高的数据（读多，写少）
     * 的数据保存在缓存中，可以加快程序速度；
     */
    //TODO 这里记录一个很重要的异常：OutOfDirectMenoryError
    //1，Spring boot2.0以后使用lettuce作为操作Redis的客户端，使用netty进行网络通信
    //2.lettuce的bug导致netty堆外内存溢出，如果不指定堆外内存大小，就会根据设置的进程的内存大小作为堆外内存
    //解决方案：
    //1，升级lettuce的客户端
    //2.排除lettuce依赖，使用jedis

    public Map<String, List<Catelog2VO>> getCategoryJson2() {
        /*
            为了应对突发情况，应对redis缓存所可能出现的异常，针对不同的情况添加不同的方式；
            1，缓存穿透：空结果缓存
            2，缓存雪崩：设置过期时间（添加随机值）
            3，缓存击穿：1，可以设置缓存永不过时
                        2，添加互斥锁（重点）

         */

        //1,添加缓存逻辑，先判断缓存中是否有所需要的数据，如果有直接返回，如果没有先去数据库中查询，再写入缓存
        System.out.println("进来了");
        String catalogJSONs = stringRedisTemplate.opsForValue().get("catalogJSON");

        if (StringUtils.isEmpty(catalogJSONs)) {
            System.out.println("catalogJSON" + catalogJSONs);
            Map<String, List<Catelog2VO>> categoryJsonFromDB = getCategoryJsonFromDBWithRedissonLock();
            //从数据库中查出数据
            return categoryJsonFromDB;
        }
        //从缓存中获取数据，将JSON数据转换成所需要的数据类型  Map<String,List<Catelog2VO>>
        Map<String, List<Catelog2VO>> resultsMap = JSON.parseObject(catalogJSONs,
                new TypeReference<Map<String, List<Catelog2VO>>>() {
                });
        //    protected TypeReference() { 由于TypeReference是受保护的（protected)所以需要匿名内部类
        return resultsMap;

    }

    @Cacheable(value = {"catalog"} ,key = "#root.method.name")
    @Override
    public Map<String, List<Catelog2VO>> getCategoryJson() {

        List<PmsCategoryEntity> selectList = baseMapper.selectList(null);
        List<PmsCategoryEntity> lavelCategory = getParentCid(selectList, 0L);
        //将返回的结果封装成map<k,v>，将catid作为key值，将二级以及三级菜单属性作为value
        Map<String, List<Catelog2VO>> collect = lavelCategory.stream().collect(Collectors.toMap(k -> k.getCatId().
                toString(), v -> {
            //这里得到二级菜单，并将三级菜单集合赋值为null
            List<PmsCategoryEntity> entities = getParentCid(selectList, v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (entities != null) {
                catelog2VOS = entities.stream().map(item -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), item.getCatId().toString(),
                            item.getName().toString(), null);
                    //这里的catalogList先赋值为null
                    //通过二级分类的父节点id得到三级分类，并封装成集合
                    List<PmsCategoryEntity> entities1 = getParentCid(selectList, item.getCatId());
                    if (entities1 != null) {
                        List<Catelog2VO.Catelog3VO> catelog3VOList = entities1.stream().map(m -> {
                            Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO(item.getCatId().toString(),
                                    m.getCatId().toString(), m.getName().toString());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        //在得到了Catelog3VO之后设置里面的集合
                        catelog2VO.setCatalog3List(catelog3VOList);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());
            }

            return catelog2VOS;

        }));

        return collect;
    }

    /**
     * 通过Redis实现分布式锁
     *
     * @return
     */
//    TODO：Redis分布式锁
    public Map<String, List<Catelog2VO>> getCategoryJsonFromDBWithRedisLock() {
        //占分布式锁，简单的锁就是去redis占位
        //setIfAbsent就是Redis中的SetNX 语句,利用语句设置过期时间，遵循原子性
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,
                300, TimeUnit.SECONDS);
        //对Lock进行判断如果Lock的值是true，那么代表这个服务成功占锁
        if (lock) {
            System.out.println("获取分布式锁成功......");
            //加锁成功，执行查询数据库的操作
            Map<String, List<Catelog2VO>> dataFromDB = null;
            try {
                dataFromDB = getDataFromDB();
            } finally {
                //对数据库操作完，应该及时将锁释放
                // String lockValue = stringRedisTemplate.opsForValue().get("lock");
                //z合理实现了UUID的匹配如果是当前的UUID就进行删除
                //if (uuid.equals(lockValue)){
                //    stringRedisTemplate.delete("lock");
                // }
                /**
                 * 使用Redis+Lua脚本实现删除锁操作的原子性
                 */
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class)
                        , Arrays.asList("lock"), uuid);
                return dataFromDB;
            }

        } else {
            //代表占锁失败，，，，重试，
            //也可以休眠100MS在重试
//            设置等待时间
            System.out.println("获取失败等待重试");
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCategoryJsonFromDBWithRedisLock();
        }


    }

    /**
     * 通过Redisson实现分布式锁
     * 缓存里面的数据如何让和数据库中保持一致
     * 缓存一致性问题
     *
     * @return
     */
    public Map<String, List<Catelog2VO>> getCategoryJsonFromDBWithRedissonLock() {
        //1,锁的名字，锁的粒度，越细越块
        //锁的粒度：具体缓存的某个数据，11号商品；product-11-lock
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2VO>> dataFromDB;
        try {
            dataFromDB = getDataFromDB();
        } finally {
            lock.unlock();

        }
        return dataFromDB;


    }


    private Map<String, List<Catelog2VO>> getDataFromDB() {
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        //如果缓存不是Null直接返回
        if (!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2VO>> resultsMap = JSON.parseObject(catalogJSON,
                    new TypeReference<Map<String, List<Catelog2VO>>>() {
                    });
            //    protected TypeReference() { 由于TypeReference是受保护的（protected)所以需要匿名内部类
            return resultsMap;
        }
        List<PmsCategoryEntity> selectList = baseMapper.selectList(null);
        List<PmsCategoryEntity> lavelCategory = getParentCid(selectList, 0L);
        //将返回的结果封装成map<k,v>，将catid作为key值，将二级以及三级菜单属性作为value
        Map<String, List<Catelog2VO>> collect = lavelCategory.stream().collect(Collectors.toMap(k -> k.getCatId().
                toString(), v -> {
            //这里得到二级菜单，并将三级菜单集合赋值为null
            List<PmsCategoryEntity> entities = getParentCid(selectList, v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (entities != null) {
                catelog2VOS = entities.stream().map(item -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), item.getCatId().toString(),
                            item.getName().toString(), null);
                    //这里的catalogList先赋值为null
                    //通过二级分类的父节点id得到三级分类，并封装成集合
                    List<PmsCategoryEntity> entities1 = getParentCid(selectList, item.getCatId());
                    if (entities1 != null) {
                        List<Catelog2VO.Catelog3VO> catelog3VOList = entities1.stream().map(m -> {
                            Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO(item.getCatId().toString(),
                                    m.getCatId().toString(), m.getName().toString());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        //在得到了Catelog3VO之后设置里面的集合
                        catelog2VO.setCatalog3List(catelog3VOList);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());
            }

            return catelog2VOS;

        }));
        System.out.println("collect" + collect.size());
        System.out.println("查询了数据库");
        String jsonString = JSON.toJSONString(collect);
        //转换成JSON数据，缓存中存储的是JSON数据，JSON数据是跨平台，跨语言
        stringRedisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);
        //直接返回数据
        return collect;
    }

    public Map<String, List<Catelog2VO>> getCategoryJsonFromDBWithLocalLock() {
        /**
         * 代码优化：
         * 1,优化前的代码过于冗余，对数据库操作多次，导致吞吐量变小速度变慢；
         * 将全部的分类信息一次查询出来，封装再通过对该数据进行检索查询，减少了对数据库的操作
         * 2,通过将目录分类
         * 即时性，数据一致性要求不高；
         * 访问量大且更新频率不高的数据（读多，写少）
         * 的数据保存在缓存中，可以加快程序速度；
         *
         */
        //只要是同一把锁，就能锁住这个锁的所有进程
        //1，synchronized（this)：spring boot中的所有组件都是单例的
        // TODO ：不管是互斥锁还是JUC当中的各种锁，都是本地锁，而在分布式系统中，我们需要的是分布式锁
        System.out.println("synchronized");
        //这个this就是当前对象的锁
        synchronized (this) {
            System.out.println("into");
            String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
            //如果缓存不是Null直接返回

            if (!StringUtils.isEmpty(catalogJSON)) {
                Map<String, List<Catelog2VO>> resultsMap = JSON.parseObject(catalogJSON,
                        new TypeReference<Map<String, List<Catelog2VO>>>() {
                        });
                System.out.println("缓存命中");
                //    protected TypeReference() { 由于TypeReference是受保护的（protected)所以需要匿名内部类
                return resultsMap;
            }
            System.out.println("缓存没有命中");
            List<PmsCategoryEntity> selectList = baseMapper.selectList(null);
            List<PmsCategoryEntity> lavelCategory = getParentCid(selectList, 0L);
            //将返回的结果封装成map<k,v>，将catid作为key值，将二级以及三级菜单属性作为value
            Map<String, List<Catelog2VO>> collect = lavelCategory.stream().collect(Collectors.toMap(k -> k.getCatId().
                    toString(), v -> {
                //这里得到二级菜单，并将三级菜单集合赋值为null
                List<PmsCategoryEntity> entities = getParentCid(selectList, v.getCatId());
                List<Catelog2VO> catelog2VOS = null;
                if (entities != null) {
                    catelog2VOS = entities.stream().map(item -> {
                        Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), item.getCatId().toString(),
                                item.getName().toString(), null);
                        //这里的catalogList先赋值为null
                        //通过二级分类的父节点id得到三级分类，并封装成集合
                        List<PmsCategoryEntity> entities1 = getParentCid(selectList, item.getCatId());
                        if (entities1 != null) {
                            List<Catelog2VO.Catelog3VO> catelog3VOList = entities1.stream().map(m -> {
                                Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO(item.getCatId().toString(),
                                        m.getCatId().toString(), m.getName().toString());
                                return catelog3VO;
                            }).collect(Collectors.toList());
                            //在得到了Catelog3VO之后设置里面的集合
                            catelog2VO.setCatalog3List(catelog3VOList);
                        }
                        return catelog2VO;
                    }).collect(Collectors.toList());
                }

                return catelog2VOS;

            }));
            System.out.println("collect" + collect.size());
            System.out.println("查询了数据库");
            String jsonString = JSON.toJSONString(collect);
            //转换成JSON数据，缓存中存储的是JSON数据，JSON数据是跨平台，跨语言
            stringRedisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);
            //直接返回数据
            return collect;
        }


    }

    private List<PmsCategoryEntity> getParentCid(List<PmsCategoryEntity> selectList, Long paren_id) {
        List<PmsCategoryEntity> collect = selectList.stream().filter(
                item -> item.getParentCid() == paren_id).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findParentPath(Long catelogId, List list) {
        //1,收集当前节点的ID
        list.add(catelogId);
        PmsCategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            //判断当前节点的父ID不为０　继续递归查找父id
            findParentPath(byId.getParentCid(), list);
        }
        //当查找到父id不为0，返回当前的list
        return list;
    }

    /**
     * 获取某一个菜单的子菜单
     * 递归查找所有菜单的子菜单
     */
    private List<PmsCategoryEntity> getChildren(PmsCategoryEntity root, List<PmsCategoryEntity> all) {

        List<PmsCategoryEntity> children = all.stream().filter(categoryEntity -> {
            //返回父分类等于分类ID的
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1、找到子菜单\
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }
}