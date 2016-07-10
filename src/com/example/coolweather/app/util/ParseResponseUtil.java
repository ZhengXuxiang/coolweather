package com.example.coolweather.app.util;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.coolweather.app.model.WeatherInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.util.Log;


public class ParseResponseUtil {
	
	/**
	 * �������ֻ����Xml�������͵ķ��������ص�����
	 * @param response
	 * @return
	 */
	public static final List<Map<String, String>> parseXmlWithPull(InputStream response){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {	
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(response,"utf-8");
			int eventType = xmlPullParser.getEventType();
			while (eventType!=xmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				Map map =null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					map = new HashMap<String, String>();
					break;
				case XmlPullParser.START_TAG:
					if("china".equals(nodeName)){
						map.put("tag", "province");
					}
					if(map.get("tag").equals("province")){
						//��ȡ������ʡ���xml
						if ("city".equals(nodeName)) {
							map.put("provinceName", xmlPullParser.getAttributeValue(0));
							map.put("provinceCode", xmlPullParser.getAttributeValue(1));
						}
					}else{
						//��ȡ�������м���xml
						if(xmlPullParser.getAttributeValue(5)!=null||!"".equals(xmlPullParser.getAttributeValue(5))){
							map.put("tag","city");
							map.put("cityName", xmlPullParser.getAttributeType(3));
							map.put("cityCode", xmlPullParser.getAttributeValue(5));
							map.put("cityId", xmlPullParser.getAttributeValue(17));
						}else{
							//��ȡ�������ؼ����� û��ƴ������
							map.put("tag", "county");
							map.put("countyName", xmlPullParser.getAttributeValue(2));
							map.put("countyCode", xmlPullParser.getAttributeValue(17));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					//��ɽ���ĳ���ڵ���߼�������д
					break;
				default:
					break;
				}
				list.add(map);
			}
		} catch (Exception e) {
			Log.e("ParseResponseUtil",e.getMessage());
		}
		return list;
	}
	
	public static final WeatherInfo parseJSONWithGSON(String jsonData){
		Gson gson = new Gson();
		WeatherInfo weatherInfo = new WeatherInfo();
		List<WeatherInfo> weatherInfoList = gson.fromJson(jsonData, new TypeToken<List<WeatherInfo>>(){}.getType());
		for(WeatherInfo w:weatherInfoList){
			weatherInfo=w;
		}
		return weatherInfo;
	}
	
}
