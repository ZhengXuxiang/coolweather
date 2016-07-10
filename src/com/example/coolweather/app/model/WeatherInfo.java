package com.example.coolweather.app.model;

import java.io.Serializable;

public class WeatherInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//"city":"ÂÞÔ´","cityid":"101230104","temp1":"6¡æ",
	//"temp2":"18¡æ","weather":"¶àÔÆ×ªÇç","img1":"n1.gif","img2":"d0.gif","ptime":"18:00"}
	private String cityName; 
	private String cityId;
	private String lowTemp;
	private String hightTemp;
	private String weather;
	private String pTime;
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getLowTemp() {
		return lowTemp;
	}
	public void setLowTemp(String lowTemp) {
		this.lowTemp = lowTemp;
	}
	public String getHightTemp() {
		return hightTemp;
	}
	public void setHightTemp(String hightTemp) {
		this.hightTemp = hightTemp;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getpTime() {
		return pTime;
	}
	public void setpTime(String pTime) {
		this.pTime = pTime;
	}
	
	
}
