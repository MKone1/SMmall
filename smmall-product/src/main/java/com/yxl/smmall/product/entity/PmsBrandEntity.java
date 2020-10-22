package com.yxl.smmall.product.entity;

import ch.qos.logback.core.boolex.EvaluationException;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.yxl.common.valid.AddGroup;
import com.yxl.common.valid.ListValue;
import com.yxl.common.valid.UpdataGroup;
import com.yxl.common.valid.UpdateStatusGroup;
import javafx.scene.Group;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Data
@TableName("pms_brand")
public class PmsBrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message ="新增不能指定ID",groups = {AddGroup.class})
	@NotNull(message = "修改必须指定ID",groups = {UpdataGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
//	@NotEmpty//必须不是空的或者empty空串
	@NotBlank(message = "品牌名称必须提交",groups = {AddGroup.class,UpdataGroup.class})
	//必须包含至少一个非空字符
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = {AddGroup.class})
	@URL(message = "logo必须是一个合法的url地址",groups = {AddGroup.class,UpdataGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 * */
	 @NotNull(groups ={AddGroup.class, UpdateStatusGroup.class})
	 @ListValue(vals = {0,1},groups ={AddGroup.class, UpdateStatusGroup.class},message = "必须填指定的数字")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须是一个字母")
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序必须大于等于0")
	@NotNull(groups = {AddGroup.class})
	private Integer sort;

}
