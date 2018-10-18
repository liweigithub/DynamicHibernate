package com.hyaroma.dao.utils;

/**
 * @author  wstv
 * 字符串校验工具类
 */
public class ValidateUtil {

	public static boolean isNull(String str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return true;
		} else {
			return false;
		}

	}
}
