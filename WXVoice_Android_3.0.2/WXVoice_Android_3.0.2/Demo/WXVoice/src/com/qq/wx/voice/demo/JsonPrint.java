package com.qq.wx.voice.demo;

public class JsonPrint {
	public static String print(String json) {
		if (null == json) {
			return "";
		}

		StringBuilder res = new StringBuilder();
		int level = 0;
		int i = 0;
		int len = json.length();
		while (i < len) {
			char ele = json.charAt(i);
			if (ele == '{') {
				level += 1;
				res.append(ele);
				res.append("\r\n");
				res.append(printTab(level));
			}
			else if (ele == '}') {
				level -= 1;
				res.append("\r\n");
				res.append(printTab(level));
				res.append(ele);
			}
			else if (ele == ',') {
				res.append(ele);
				res.append("\r\n");
				res.append(printTab(level));
			}
			else {
				res.append(ele);
			}
			
			i++;
		}
		return res.toString();
	}
	
	
	public static String printTab(int num) {
		String tab = "    ";
		String res = "";
		for (int i = 0; i < num; i++) {
			res += tab;
		}
		return res;
	}
}
