package com.example.login;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.os.Bundle;


public class ImgActivity extends Activity {
	private GridView imggridView;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imglayout);
		imggridView=(GridView) this.findViewById(R.id.imggridView);
		
		List<Map<String, Integer>> data=new ArrayList<Map<String,Integer>>();
		Field[] fields=R.drawable.class.getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Map<String,Integer> map=new HashMap<String, Integer>();
			try {
				map.put("img", fields[i].getInt(R.drawable.class));
			} catch (Exception e) {				
				Log.e("Exception", e.getMessage());
			} 
			data.add(map);
		}
		
		//ºÚµ•  ≈‰∆˜£¨Ω‚ Õº˚øŒ±æ124“≥
		SimpleAdapter adapter=new SimpleAdapter(this, data, R.layout.imgitem, new String[]{"img"}, new int[]{R.id.imgchooseitem});
		
		imggridView.setAdapter(adapter);//º”‘ÿ  ≈‰∆˜

		imggridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,int arg2,long arg3){
				Map<String, Integer> map=(Map<String, Integer>) arg0.getItemAtPosition(arg2);
				Integer photoid=map.get("img");
				Intent intent=new Intent();
				intent.setClass(ImgActivity.this, MainActivity.class);
				intent.putExtra("photo", photoid);
				setResult(200,intent);
				ImgActivity.this.finish();
				
			}
		});
		
	}

}
