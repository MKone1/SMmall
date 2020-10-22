package com.yxl.smmall.smmallthreeparty;

import com.aliyun.oss.OSSClient;

import com.yxl.smmall.smmallthreeparty.component.SmmallComponent;
import com.yxl.smmall.smmallthreeparty.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SmmallThreePartyApplicationTests {
        @Autowired
        OSSClient ossClient;
        @Autowired
        SmmallComponent smmallComponent;
    @Test
    void contextLoads() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("E:\\图片\\热门翻唱好听的BGM_109951164753261722.jpg");

            ossClient.putObject("smmall-oos", "热门翻唱好听的BGM_109951164753261722.jpg", inputStream);

// 关闭OSSClient。
            ossClient.shutdown();
              System.out.println("上传成功");
    }
    @Test
    void testduanxinURL(){
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "fa751ba6a2dc4aef9d02e34536facffb";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "19180697714");
        querys.put("param", "88879");
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    void testsendcode(){
        smmallComponent.sendSmmallCode("19180697714","8069");
    }

}
