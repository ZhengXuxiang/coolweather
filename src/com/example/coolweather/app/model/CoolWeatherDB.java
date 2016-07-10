package com.example.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.app.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	/**
	 *数据库名
	 */
	public static final String DB_NAME="cool_weather";
	/**
	 * 数据库本版号
	 */
	public static final int DB_VERSION=1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * 构造方法私有化
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
		db=dbHelper.getWritableDatabase();
	}
	/**
	 * 获取CoolWeatherDB实例
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB==null){
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	/**
	 * 将Province实例保存到数据库
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/**
	 * 从数据库读取全国所有的省份信息
	 * @return
	 */
	public List<Province> loadProvinces(){
		List<Province> list =new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do {
				Province province = new Province();
				province.setId(cursor.getColumnIndex("id"));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		
		if(cursor !=null){
			cursor.close();
		}
		return list;
	}
	
	
	
	
	/**
	 * 将City实例保存到数据库
	 * @param City
	 */
	public void saveCity(City city){
		if(city!=null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_code", city.getProvinceCode());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * 从数据库读取全国所有的城市信息
	 * @return
	 */
	public List<City> loadCities(String provinceCode){
		List<City> list =new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_code = ?",new String[]{String.valueOf(provinceCode)},null,null,null);
		if(cursor.moveToFirst()){
			do {
				City city = new City();
				city.setId(cursor.getColumnIndex("id"));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		
		if(cursor !=null){
			cursor.close();
		}
		return list;
	}
	
	
	/**
	 * 将County实例保存到数据库
	 * @param City
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_code", county.getCityCode());
			db.insert("County", null, values);
		}
	}
	
	/**
	 * 从数据库读取全国所有的县信息
	 * @return
	 */
	public List<County> loadCounties(String cityCode){
		List<County> list =new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id =?", new String[]{String.valueOf(cityCode)}, null, null, null);
		if(cursor.moveToFirst()){
			do {
				County county = new County();
				county.setId(cursor.getColumnIndex("id"));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		
		if(cursor !=null){
			cursor.close();
		}
		return list;
	}
	
	
	
	
	
	
	
}
