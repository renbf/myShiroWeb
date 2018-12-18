package com.yl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.yl.modle.AdminUser;

public class Test {
	public static void main(String[] args) {
		AdminUser a1 = new AdminUser();
		AdminUser a2 = new AdminUser();
		Object obj1 = new Object();
		Object obj2 = new Object();
		System.out.println(a1.hashCode());
		System.out.println(a2.hashCode());
		System.out.println(obj1.hashCode());
		System.out.println(obj2.hashCode());
		System.out.println("重地".hashCode());
		System.out.println("通话".hashCode());
		Map<String,String> map =new HashMap<String,String>();
		map.put("重地", "1");
		map.put("通话", "2");
		System.out.println(1179395 & 15);
		for(Entry<String, String> entry:map.entrySet()) {
			System.out.println(entry.hashCode());
		}
		
	}
}
