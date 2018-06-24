package org.eocencle.winger.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * json转化类
 * @author huanStephen
 *
 */
public class JsonUtil {
	/** 
	 * 获取JsonObject 
	 * @param json 
	 * @return 
	 */
	public static JsonObject parseJson(String json) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = parser.parse(json).getAsJsonObject();
		return jsonObj;
	}

	/** 
	 * 根据json字符串返回Map对象 
	 * @param json 
	 * @return 
	 */
	public static StrictMap<Object> toMap(String json) {
		return JsonUtil.toMap(JsonUtil.parseJson(json));
	}

	/** 
	 * 将JSONObjec对象转换成Map-List集合 
	 * @param json 
	 * @return 
	 */
	public static StrictMap<Object> toMap(JsonObject json) {

		Entry<String, JsonElement> entry = null;
		String key = null;
		Object value = null;
		Integer intVal = null;

		StrictMap<Object> map = new StrictMap<Object>("json collection");
		Set<Entry<String, JsonElement>> entrySet = json.entrySet();

		for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext();) {
			entry = iter.next();
			key = entry.getKey();
			value = entry.getValue();
			if (value instanceof JsonArray) {
				map.put((String) key, toList((JsonArray) value));
			} else if(value instanceof JsonObject) {
				map.put((String) key, toMap((JsonObject) value));
			} else {
				value = exString(value);
				intVal = parseInt(value);
				if (null == intVal) {
					map.put((String) key, value);
				} else {
					map.put((String) key, intVal);
				}
			}
		}
		return map;
	}

	/** 
	 * 将JSONArray对象转换成List集合 
	 * @param json 
	 * @return 
	 */
	public static List<Object> toList(JsonArray json){
		List<Object> list = new ArrayList<Object>();
		Integer intVal = null;
		for (int i = 0; i < json.size(); i ++) {
			Object value = json.get(i);
			if (value instanceof JsonArray) {
				list.add(toList((JsonArray) value));
			}
			else if (value instanceof JsonObject) {
				list.add(toMap((JsonObject) value));
			}
			else {
				value = exString(value);
				intVal = parseInt(value);
				if (null == intVal) {
					list.add(value);
				} else {
					list.add(intVal);
				}
			}
		}
		return list;
	}

	/**
	 * 将Object的String类型去掉双引号
	 * @param obj
	 * @return
	 */
	private static Object exString(Object obj) {
		String str = obj.toString();
		Object value = obj;
		if (str.startsWith("\"",0) && str.startsWith("\"",str.length() - 1)) {
			value = str.substring(1,str.length() - 1);
		}
		return value;
	}
	
	/**
	 * 转化int，避免小数
	 * @param obj
	 * @return
	 */
	private static Integer parseInt(Object obj) {
		String str = obj.toString();
		Integer value = null;
		if (-1 == str.indexOf(".")) {
			try {
				value = new Integer(str);
			} catch (NumberFormatException e) {
				//转换失败返回null
			}
		}
		return value;
	}
	
	/**
	 * 转化为string
	 * @param obj
	 * @return
	 */
	public static String parseString(Object obj) {
		String json = null;
		if (obj instanceof String) {
			json = obj.toString();
		} else {
			json = new Gson().toJson(obj);
		}
		return json;
	}
}
