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
	 * 解析服务器返回的省级数据
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
	 * 解析服务器返回的市级数据
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
				city.setCityCode(cityMap.get("cityCode"));//城市拼音，这个才是用来获取信息的东西
				//setProvinceCode 省级的拼音
				city.setProvinceCode(provinceCode);
				coolWeatherDB.saveCity(city);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的县级数据
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
				county.setCountyCode(countyMap.get("countyCode"));//城市拼音，这个才是用来获取信息的东西
				//setProvinceCode 市级的拼音
				county.setCityCode(cityCode);
				coolWeatherDB.saveCounty(county);
				return true;
			}
		}
		return false;
	}
	
}
















