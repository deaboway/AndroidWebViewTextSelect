package com.example.webtextselect;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class WebViewActivity extends Activity {

	
	CustomWebView  web;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.web_url);
		
		web = (CustomWebView) findViewById(R.id.web);
		
		web.loadUrl("file:///android_asset/content.html");
		
		
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
	       
		Log.e("menu", menu.getItem(0).getItemId()+"");
		
		
		super.onContextMenuClosed(menu);
	}

	
	
	
}
