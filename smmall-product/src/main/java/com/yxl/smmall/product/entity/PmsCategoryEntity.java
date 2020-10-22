package com.yxl.smmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 商品三级分类
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Data
@TableName("pms_category")
public class PmsCategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId
	private Long catId;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父分类id
	 */
	private Long parentCid;
	/**
	 * 层级
	 */
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 * 	@TableLogic 由于数据库中的字段定义规则与配置文件中的规则不一致
	 * 	这里可以通过设值来改变
	 * public @interface TableLogic {
	 * 		默认逻辑未删除（该值可无，会自动获取全局配置）
	 *     String value() default "";
	 * 默认逻辑删除值（该值可无，会自动获取全局配置）
	 *     String delval() default "";
	 * }
	 */
	@TableLogic(value = "1",delval = "0")
	private Integer showStatus;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;
	/**
	 *包含所有的子分类
	 * @TableField(exist = false)
	 * 表中不存在的
	 * public @interface TableField {
	 *     String value() default "";
	 *
	 *     boolean exist() default true;
	 *
	 *     @JsonInclude
	 *      public static enum Include {
	 *         ALWAYS,
	 *         NON_NULL,
	 *         NON_ABSENT,
	 *         NON_EMPTY,
	 *         NON_DEFAULT,
	 *         CUSTOM,
	 *         USE_DEFAULTS;
	 *
	 *         private Include() {
	 *         }
	 *     }
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<PmsCategoryEntity> children;
}
