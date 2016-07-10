package com.example.coolweather.app.util;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.example.coolweather.app.model.City;
import com.example.coolweather.app.model.CoolWeatherDB;
import com.example.coolweather.app.model.County;
import com.example.coolweather.app.model.Province;

public class Utility {
	/**
	 * �������������ص�ʡ������
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,InputStream response){
		List<Map<String, String>> list = ParseResponseUtil.parseXmlWithPull(response);
		for(Map<String, String> provinceMap:list){
			if("province".equals(provinceMap.get("tag"))){
				Province province =new Province();
				province.setProvinceName(provinceMap.get("provinceName"));
				province.setProvinceCode(provinceMap.get("provinceCode"));
				coolWeatherDB.saveProvince(province);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �������������ص��м�����
	 * @param coolWeatherDB
	 * @param response
	 * @param provinceCode
	 * @return
	 */
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,InputStream response,String provinceCode){
		List<Map<String, String>> list = ParseResponseUtil.parseXmlWithPull(response);
		for(Map<String, String> cityMap:list){
			if("city".equals(cityMap.get("tag"))){
				City city =new City();
				city.setCityName(cityMap.get("cityName"));
				city.setCityCode(cityMap.get("cityCode"));//����ƴ�����������������ȡ��Ϣ�Ķ���
				//setProvinceCode ʡ����ƴ��
				city.setProvinceCode(provinceCode);
				coolWeatherDB.saveCity(city);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �������������ص��ؼ�����
	 * @param coolWeatherDB
	 * @param response
	 * @param cityCode
	 * @return
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,InputStream response,String cityCode){
		
		List<Map<String, String>> list = ParseResponseUtil.parseXmlWithPull(response);
		for(Map<String, String> countyMap:list){
			if("city".equals(countyMap.get("tag"))){
				County county =new County();
				county.setCountyName(countyMap.get("countyName"));
				county.setCountyCode(countyMap.get("countyCode"));//����ƴ�����������������ȡ��Ϣ�Ķ���
				//setProvinceCode �м���ƴ��
				county.setCityCode(cityCode);
				coolWeatherDB.saveCounty(county);
				return true;
			}
		}
		return false;
	}
	
}
















