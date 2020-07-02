package com.yxl.smmall.member;

import com.yxl.smmall.member.entity.UmsMemberEntity;
import com.yxl.smmall.member.service.UmsMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmmallMemberApplicationTests {
    @Autowired
    UmsMemberService umsMemberService;
    @Test
    void contextLoads() {
        UmsMemberEntity memberEntity = new UmsMemberEntity();
        memberEntity.setNickname("张三");
        umsMemberService.save(memberEntity);
        System.out.println("保存成功");

    }

}
