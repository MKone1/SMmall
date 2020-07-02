package com.yxl.smmall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yxl.smmall.product.entity.PmsBrandEntity;
import com.yxl.smmall.product.service.PmsBrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
class SmmallProductApplicationTests {
    @Autowired
    PmsBrandService pmsBrandService;
        @Test
    public void testUpload() throws FileNotFoundException {
            // Endpoint以杭州为例，其它Region请按实际情况填写。
            String endpoint = "oss-cn-chengdu.aliyuncs.com";
// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
            String accessKeyId = "LTAI4G6QDbR74uMEia33TDjd";
            String accessKeySecret = "8aRa6y2xqVXZ3664QaN6U2n89ao5Xm";

// 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 上传文件流。
            InputStream inputStream = new FileInputStream("E:\\学习\\谷粒商城基础篇\\docs\\pics\\5b5e74d0978360a1.jpg");

            ossClient.putObject("smmall-oos", "5b5e74d0978360a1.jpg", inputStream);

// 关闭OSSClient。
            ossClient.shutdown();
            System.out.println("上传成功");
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

}
