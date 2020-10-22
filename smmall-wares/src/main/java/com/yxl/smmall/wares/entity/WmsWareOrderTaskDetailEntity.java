package com.yxl.smmall.wares.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存工作单
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("wms_ware_order_task_detail")
public class WmsWareOrderTaskDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * sku_id
	 */
	private Long skuId;
	/**
	 * sku_name
	 */
	private String skuName;
	/**
	 * 购买个数
	 */
	private Integer skuNum;
	/**
	 * 工作单id
	 */
	private Long taskId;
	/**
	 * 仓库ID
	 */
	private Long wareId;
	/**
	 * 锁定状态
	 * 1,表示锁定库存
	 * 2，表示解锁库存
	 * 3，扣减库存
	 */
	private Integer lockStatus;
}
