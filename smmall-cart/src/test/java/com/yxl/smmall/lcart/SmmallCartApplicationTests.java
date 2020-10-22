package com.yxl.smmall.lcart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


class SmmallCartApplicationTests {

    @Test
    void contextLoads() {
        int i =0;
        String s = "asdasdasdasjhkjhjkhjkvref";
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            i++;
        }
        System.out.println(i);
    }

}
