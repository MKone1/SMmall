/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yxl.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 * 泛型化设计
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	/** 这里的getData是为了通过R返回想要得到的数据类型无需强制转换；
	 * 利用fast JSON逆转类型
	 * typeReference是一个Alibaba的一个泛型工具类
	 */
	public <T> T getData(TypeReference<T> typeReference) {
		Object data = get("data");//这里默认是map
		String s = JSON.toJSONString(data);//将data的数据转换成JSON字符串
		T t = JSON.parseObject(s, typeReference);
		//将转换后的JSON字符串转换成想要得到的类型,
		// 复杂的类型需要通过TypeReference这个泛型工具类

		return t;
	}
	public <T> T getData(String key, TypeReference<T> typeReference) {
		Object data = get(key);
		String s = JSON.toJSONString(data);
		T t = JSON.parseObject(s, typeReference);
		return t;
	}

	public R setData(Object o) {
		put("data", o);
		return this;
	}


	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public Integer getCode() {
		return (Integer) this.get("code");
	}
}
