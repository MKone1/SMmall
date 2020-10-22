package com.yxl.smmall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttrRespVo extends AttrVo {
    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * 			"groupName": "主体", //所属分组名字
     *
     * 	"catelogPath": [2, 34, 225] //分类完整路径
     */
    private  String catelogName;
    private  String groupName;
    private  Long[] catelogPath;
}
