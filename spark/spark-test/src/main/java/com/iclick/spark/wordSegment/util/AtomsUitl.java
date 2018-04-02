package com.iclick.spark.wordSegment.util;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.apache.commons.lang.StringUtils;

public class AtomsUitl {
	/**
	 * 输入的字符是否是汉字
	 * 
	 * @param a
	 *            char
	 * @return boolean
	 */

	public final static String stopwords = "的很了么呢是嘛个都也比还这于不与才上用就好在和对挺去后没说";

	public static boolean isChinese(char a) {
		int v = (int) a;
		return (v >= 19968 && v <= 171941);
	}

	public static boolean allChs(String s) {
		if (null == s || "".equals(s.trim()))
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (!isChinese(s.charAt(i)))
				return false;
		}
		return true;
	}

	public static boolean allEn(String s) {
		if (null == s || "".equals(s.trim()))
			return true;
		for (int i = 0; i < s.length(); i++) {
			if (isChinese(s.charAt(i)))
				return false;
		}
		return true;
	}

	/*
	 * 计算一共有多少个词，其中连续的英文字符只算一个词，比如s="我是nnnnn自然语言"
	 * list = getAtomIndex(s)后得到数组[0,1,2,7,8,9,10,11]，则list.size()-1就是词的数量，为7个。
	 */
	public static List<Integer> getAtomIndex(String line) {
		ArrayList<Integer> wIndex = new ArrayList<Integer>();
		wIndex.add(0);
		int n = 1;
		for (int i = 0; i < line.length(); i += n) {

			n = 1;
			if (isChinese(line.charAt(i)) || line.charAt(i) == '$') {
				wIndex.add(i + n);
			} else {
				for (int j = i + 1; j < line.length(); j++) {
					if (isChinese(line.charAt(j)) || line.charAt(j) == '$') {
						break;
					} else {
						n += 1;
					}
				}
				wIndex.add(i + n);
			}
		}
		/*
		 * System.out.println(wIndex); int preIndex = 0; for (Integer curIndex :
		 * wIndex) { System.out.println(line.substring(preIndex, curIndex)); }
		 */
		return wIndex;
	}

	public static String reverse(String line) {
		List<Integer> wIndex = AtomsUitl.getAtomIndex(line);
		StringBuilder sb = new StringBuilder();
		for (int i = wIndex.size() - 1; i > 0; --i) {
			sb.append(line.substring(wIndex.get(i - 1), wIndex.get(i)));
		}
		return sb.toString();
	}

	public static int len(String line) {
		int total = 0;
		if (StringUtils.isEmpty(line)) {
			return 0;
		}
		List<Integer> wIndex = AtomsUitl.getAtomIndex(line);
		return wIndex.size() - 1;
	}

	public static String substring(String line, int beginIndex, int endIndex) {
		List<Integer> wIndex = AtomsUitl.getAtomIndex(line);
		// System.out.println(wIndex);
		// System.out.println("size:" + wIndex.size());
		// System.out.println(wIndex.get(beginIndex));
		// return line.substring(Math.min(beginIndex, wIndex.size() -
		// 1),Math.min(endIndex, wIndex.size() - 1));
		String result = null;
		try {
			result = line.substring(
					wIndex.get(Math.min(beginIndex, wIndex.size() - 1)),
					wIndex.get(Math.min(wIndex.size() - 1, endIndex)));

		} catch (Exception e) {
			result = "";

		}

		return result;

	}

	public static String substring(String line, int beginIndex) {
		List<Integer> wIndex = AtomsUitl.getAtomIndex(line);

		String result = null;
		try {
			result = line.substring(Math.min(
					wIndex.get(Math.min(beginIndex, wIndex.size() - 1)),
					wIndex.size() - 1));
		} catch (Exception e) {
			result = "";

		}
		return result;

	}

	public static void main(String[] args) {
		String s = "我是nnnnn自然语言";
//		System.out.println(substring(s, 3, 10));
//		System.out.println(len(s));
		System.out.println(reverse(s));
//		System.out.println(s.substring(9, 9));
//		List<Integer> wIndex = AtomsUitl.getAtomIndex(s);
//		for (Integer i : wIndex) {
//			System.out.println(i.toString());
//		}
//		System.out.println(wIndex.size() - 1);
//		System.out.println(substring(s, 10));
//		System.out.println(substring(s, 2));
	}
}
