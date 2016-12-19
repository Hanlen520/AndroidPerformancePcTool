package com.xdja.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 *  �����Ĺ���
 * @author zlw
 *
 */
public class CommonUtil {
	
	/**
	 *  ������λС��
	 * @param number
	 * @return
	 */
	public static float getTwoDots(float number){
		DecimalFormat df = new DecimalFormat("######0.00");
		return Float.parseFloat(df.format(number));
	}
	
	/**
	 *  ���ַ����еĶ���ո��滻��һ���ո�
	 * @param contents
	 * @return
	 */
	public static String formatBlanksToBlank(String contents) {
		Pattern p = Pattern.compile("\\s+");
		Matcher m = p.matcher(contents);
		contents = m.replaceAll(" ");
		return contents;
	}
}
