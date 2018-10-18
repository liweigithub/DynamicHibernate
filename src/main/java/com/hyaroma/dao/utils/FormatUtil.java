package com.hyaroma.dao.utils;

/**
 * @author wstv
 */

public class FormatUtil {




	/**
	 * 长字符串截断
	 * @param formatstr
	 * @param length
	 * @return
	 */
	public static String formatString(String formatstr, int length){
		return limitLength(formatstr,length,"...");
	}
	


	/**
	 * 限制字符串长度，中文字符按2个字节
	 * @param str
	 * @param bytelength
	 * @param symbol
	 * @return
	 */
	public static String limitLength(String str,int bytelength,String symbol){
		return getSubString(str,bytelength*2,symbol);
	}
	
	public static String getSubString(String str, int length, String more){
        int len = 0;
        StringBuffer sb = new StringBuffer();
        int k = 0;
        while(len < length && k < str.length()){
            char c = str.charAt(k++);
            if(c>255){
                len += 2;
                if(len > length){
                    break;
				}
            } else{
                len += 1;
            }
            sb.append(c);
        }
        if(k == str.length()){
            return sb.toString() ;
        }
        return sb.toString() + more;
    }
	public static String formatNo(int no,int length){
		 return String.format("%0"+length+"d", no);
	}

	/**
	 * 完整替换字符串，不会替换fromType  只会替换from 替换后的结果 ：select fromType,name,code  FROM aaaa
	 * @param str 字符串（select fromType,name,code  from aaaa）
	 * @param old 要替换单词
	 * @param newWord 替换后单词
	 * @return 替换后的语句
	 */
	public static String completeReplace(String str,String old,String newWord){
		str = str.replaceAll("(?<![a-zA-Z])"+old+"(?![a-zA-Z])", newWord);
		return str;
	}

}
