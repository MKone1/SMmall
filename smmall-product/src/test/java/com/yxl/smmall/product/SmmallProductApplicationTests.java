package com.yxl.smmall.product;


import com.yxl.smmall.product.dao.PmsAttrGroupDao;
import com.yxl.smmall.product.dao.PmsSkuSaleAttrValueDao;
import com.yxl.smmall.product.service.PmsCategoryService;
import com.yxl.smmall.product.vo.SkuInfoVo;
import com.yxl.smmall.product.vo.SkuItemSaleAttrVo;
import com.yxl.smmall.product.vo.SpuItemSaleAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * 1,引入oss-starter
 * 2,配置key ，端口
 */
@Slf4j
@SpringBootTest
class SmmallProductApplicationTests {
    //    @Autowired
//    PmsBrandService pmsBrandService;
//    @Autowired
//    OSSClient ossClient;
    @Test
    public void testUpload() throws FileNotFoundException {
//            // Endpoint以杭州为例，其它Region请按实际情况填写。
//            String endpoint = "oss-cn-chengdu.aliyuncs.com";
//// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//            String accessKeyId = "LTAI4G6QDbR74uMEia33TDjd";
//            String accessKeySecret = "8aRa6y2xqVXZ3664QaN6U2n89ao5Xm";

// 创建OSSClient实例。
//            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//// 上传文件流。
        //       InputStream inputStream = new FileInputStream("E:\\图片\\热门翻唱好听的BGM_109951164753261722.jpg");
//
//            ossClient.putObject("smmall-oos", "热门翻唱好听的BGM_109951164753261722.jpg", inputStream);
//
//// 关闭OSSClient。
//            ossClient.shutdown();
        //      System.out.println("上传成功");
    }


    //    @Test
//    void contextLoads() {
////        PmsBrandEntity pmsBrandEntity = new PmsBrandEntity();
////        pmsBrandEntity.setBrandId(1L);
////        pmsBrandEntity.setDescript("华为加油！雄起");
//////        pmsBrandEntity.setName("华为");
//////        pmsBrandService.save(pmsBrandEntity);
//////        System.out.println("保存成功");
////        pmsBrandService.updateById(pmsBrandEntity);
//        List<PmsBrandEntity> brand_id = pmsBrandService.list(new QueryWrapper<PmsBrandEntity>().eq("brand_id", "1"));
//        brand_id.forEach((list)->{
//            System.out.println(list);
//        });
//    }
    @Autowired
    PmsCategoryService pmsCategoryService;

    @Test
    public void testFindPath() {
        Long[] catelogPath = pmsCategoryService.findCatelogPath(225L);
    }





    @Autowired
    PmsAttrGroupDao pmsAttrGroupDao;
    @Autowired
    PmsSkuSaleAttrValueDao skuSaleAttrValueDao;
    @Test
    public void test() {
        List<SpuItemSaleAttrGroupVo> attrGroupWithBySpuId = pmsAttrGroupDao.getAttrGroupWithBySpuId(100L, 225L);
        System.out.println(attrGroupWithBySpuId.toString());
    }
    @Test
    public void test01(){
        List<SkuItemSaleAttrVo> saleAttrSpuId = skuSaleAttrValueDao.getSaleAttrSpuId(10L);
        System.out.println(saleAttrSpuId.toString());
    }

}
