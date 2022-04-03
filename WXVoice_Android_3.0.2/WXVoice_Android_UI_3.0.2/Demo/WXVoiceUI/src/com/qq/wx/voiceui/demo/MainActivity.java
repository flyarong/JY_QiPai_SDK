package com.qq.wx.voiceui.demo;


import com.qq.wx.voice.ui.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
	
	String TAG = "MainActivity";

	/**
	 * 主页的组件变量
	 * The following is components of the page
	 */
	private Button recoBtn;
	private Button ttsBtn;
	private Button listBtn;
	private Button graBtn;
	
	/**
	 * 官网申请的KEY值
	 * The APPID from official website
	 */
	public static final String screKey = "wx74c6b15da4c23673";

	/**
	 * 语法识别变量
	 * Grammar type:ABNF
	 */
	public static final int ABNF = 0;

	/**
	 * 关键词识别变量
	 * Grammar type:keywords list
	 */
	public static final int WORDLIST = 1;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initMainUI();
	}
	
	private void initMainUI() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
		
		recoBtn = (Button) findViewById(R.id.reco);
		recoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnToReco();
			}
		});

		ttsBtn = (Button) findViewById(R.id.tts);
		ttsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnToTTS();
			}
		});

		listBtn = (Button) findViewById(R.id.list);
		listBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnToGrammar(WORDLIST);
			}
		});

		graBtn = (Button) findViewById(R.id.abnf);
		graBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnToGrammar(ABNF);
			}
		});
	}
	
	private void turnToReco() {
		Intent intent = new Intent();  
        intent.setClass(this, RecoActivityUI.class);  
        startActivity(intent);  
	}

	private void turnToTTS() {
		Intent intent = new Intent();  
        intent.setClass(this, TTSActivity.class);  
        startActivity(intent);  
	}

	private void turnToGrammar(int Type) {
		Bundle bundle = new Bundle();
		bundle.putInt("TYPE", Type);
		Intent intent = new Intent();  
        intent.setClass(this, GrammarActivityUI.class);  
        intent.putExtras(bundle);
        startActivity(intent);  
	}
	
}