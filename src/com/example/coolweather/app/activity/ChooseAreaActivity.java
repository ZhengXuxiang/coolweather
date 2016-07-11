package com.example.coolweather.app.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.app.model.City;
import com.example.coolweather.app.model.CoolWeatherDB;
import com.example.coolweather.app.model.County;
import com.example.coolweather.app.model.Province;
import com.example.coolweather.app.util.HttpCallbackListener;
import com.example.coolweather.app.util.HttpUtil;
import com.example.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE =0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省级列表
	 */
	private List<Province> provinceList;
	/**
	 * 市级列表
	 */
	private List<City> cityList;
	/**
	 * 县级列表
	 */
	private List<County> countyList;
	/**
	 * 选中的省级
	 */
	private Province selectProvince;
	/**
	 * 选中的市级
	 */
	private City selectCity;
	/**
	 * 选中的县级
	 */
	private County selectCounty;
	/**
	 * 选中当前的级别
	 */
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView =(ListView)findViewById(R.id.list_view);
		titleView=(TextView)findViewById(R.id.title_text);
		adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel == LEVEL_PROVINCE){
					selectProvince=provinceList.get(position);
					queryCities();
				}else if (currentLevel==LEVEL_CITY) {
					selectCity = cityList.get(position);
					queryCountie();
				}
			}
			
		});
		queryProvince();//加载省级数据
	}
	
	/**
	 * 加载全国的县级名字，优先从数据库中获取，没有的话从服务器上查询
	 */
	private void queryCountie() {
		countyList = coolWeatherDB.loadCounties(selectCity.getCityCode());
		if (countyList.size()>0) {
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectCounty.getCountyName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectCity.getCityCode(), "county");
		}
	}
	/**
	 * 加载全国的市级名字，优先从数据库中获取，没有的话从服务器上查询
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectProvince.getProvinceCode());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectCity.getCityName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectProvince.getProvinceCode(), "city");
		}
	}
	/**
	 * 加载全国的省级名字，优先从数据库中获取，没有的话从服务器上查询
	 */
	private void queryProvince() {
		provinceList=coolWeatherDB.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	private void queryFromServer(final String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address ="http://flash.weather.com.cn/wmaps/xml/"+code+".xml";
		}else{
			address="http://flash.weather.com.cn/wmaps/xml/china.xml";
		}
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(InputStream response) {
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
				}else if ("city".equals(type)) {
					result = Utility.handleCityResponse(coolWeatherDB, response, selectProvince.getProvinceCode());
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(coolWeatherDB, response, selectCity.getCityCode());
				}
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCountie();
							}
						}
					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				//通过runOnUiThread()回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		
		
		//显示进度对话框
		showProgressDialog();
	}
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if(progressDialog==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 用来捕捉返回按钮，根据当前的级别来判断，此时返回市级别，县级别，还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_COUNTY){//预留着  后面可能要用到 这个是的先设置为县级别
			queryCountie();
		}else if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvince();
		}else{
			finish();
		}
	}
	
	
	
}




















