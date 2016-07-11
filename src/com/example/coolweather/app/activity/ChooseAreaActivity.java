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
	 * ʡ���б�
	 */
	private List<Province> provinceList;
	/**
	 * �м��б�
	 */
	private List<City> cityList;
	/**
	 * �ؼ��б�
	 */
	private List<County> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectProvince;
	/**
	 * ѡ�е��м�
	 */
	private City selectCity;
	/**
	 * ѡ�е��ؼ�
	 */
	private County selectCounty;
	/**
	 * ѡ�е�ǰ�ļ���
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
		queryProvince();//����ʡ������
	}
	
	/**
	 * ����ȫ�����ؼ����֣����ȴ����ݿ��л�ȡ��û�еĻ��ӷ������ϲ�ѯ
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
	 * ����ȫ�����м����֣����ȴ����ݿ��л�ȡ��û�еĻ��ӷ������ϲ�ѯ
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
	 * ����ȫ����ʡ�����֣����ȴ����ݿ��л�ȡ��û�еĻ��ӷ������ϲ�ѯ
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
			titleView.setText("�й�");
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
					//ͨ��runOnUiThread()�����ص����̴߳����߼�
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
				//ͨ��runOnUiThread()�ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		
		
		//��ʾ���ȶԻ���
		showProgressDialog();
	}
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if(progressDialog==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ������׽���ذ�ť�����ݵ�ǰ�ļ������жϣ���ʱ�����м����ؼ��𣬻���ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_COUNTY){//Ԥ����  �������Ҫ�õ� ����ǵ�������Ϊ�ؼ���
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




















