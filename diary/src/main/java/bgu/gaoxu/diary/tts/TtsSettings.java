package bgu.gaoxu.diary.tts;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import bgu.gaoxu.diary.R;


/**
 * 合成设置界面
 */
public class TtsSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	
	public static final String PREFER_NAME = "com.iflytek.setting";
	private EditTextPreference mSpeedPreference;
	private EditTextPreference mPitchPreference;
	private EditTextPreference mVolumePreference;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		init_actionBar();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// 指定保存文件名字
		getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
		addPreferencesFromResource(R.xml.tts_setting);
		mSpeedPreference = (EditTextPreference)findPreference("speed_preference");
		mSpeedPreference.getEditText().addTextChangedListener(new SettingTextWatcher(TtsSettings.this,mSpeedPreference,0,200));
		
		mPitchPreference = (EditTextPreference)findPreference("pitch_preference");
		mPitchPreference.getEditText().addTextChangedListener(new SettingTextWatcher(TtsSettings.this,mPitchPreference,0,100));
		
		mVolumePreference = (EditTextPreference)findPreference("volume_preference");
		mVolumePreference.getEditText().addTextChangedListener(new SettingTextWatcher(TtsSettings.this,mVolumePreference,0,100));
		
	}
	private void init_actionBar() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bottom_bgcolor));
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}		
	
	
}