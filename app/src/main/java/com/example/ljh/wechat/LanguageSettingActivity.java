package com.example.ljh.wechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by ljh on 2017/11/16.
 */

public class LanguageSettingActivity extends AppCompatActivity{
    private TextView tvLanguage;
    private TextView tvSelectLanguage;
    private RadioButton rbChinese,rbEnglish,rbDefualt;
    private RadioGroup radioGroup;
    private View view;

    private SharedPreferences sharedPreferences = LoginActivity.sharedPreferences;
    private SharedPreferences.Editor editor = LoginActivity.editor;
    private int currentLanguage = 0;
    private final int reduceId = 2131689831;
    private final Locale Language[] = {Locale.getDefault(),Locale.CHINESE,Locale.ENGLISH};
    private final String LanguageString[] = {"跟随系统","简体中文","english"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);
        initView();
        tvSelectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });
    }


    public void changeLanguage(){
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(Language[currentLanguage]);
        resources.updateConfiguration(config,dm);

    }

    public void showLanguageDialog(){
        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                currentLanguage = checkedId - reduceId;
                Log.i("aa","checkedId = " + checkedId);
                editor.putString("currentLanguage",LanguageString[currentLanguage]);
                editor.commit();
            }
        });*/

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeLanguage();
                Intent intent = new Intent(LanguageSettingActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                /*android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);*/
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.rbDefault:
                        currentLanguage = 0;
                        break;
                    case R.id.rbChinese:
                        currentLanguage = 1;
                        break;
                    case R.id.rbEnglish:
                        currentLanguage = 2;
                        break;
                }
                editor.putString("currentLanguage",LanguageString[currentLanguage]);
                editor.commit();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String getCurrentLanguage(){
        String currentLanguage = sharedPreferences.getString("currentLanguage","");
        if(currentLanguage == null || currentLanguage == ""){
            editor.putString("currentLanguage","跟随系统");
            editor.commit();
            currentLanguage = "跟随系统";
        }
        return currentLanguage;
    }

    public void initView(){
        view = LayoutInflater.from(this).inflate(R.layout.radiobutton_language,null);
        tvLanguage = (TextView) findViewById(R.id.tvLanguage);
        tvLanguage.setText(getCurrentLanguage());

        tvSelectLanguage = (TextView) findViewById(R.id.tvSelectLanguage);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        rbChinese = (RadioButton) view.findViewById(R.id.rbChinese);
        rbEnglish = (RadioButton) view.findViewById(R.id.rbEnglish);
        rbDefualt = (RadioButton) view.findViewById(R.id.rbDefault);

        if(getCurrentLanguage().equals("跟随系统")){
            rbDefualt.setChecked(true);
        }else if(getCurrentLanguage().equals("简体中文")){
            rbChinese.setChecked(true);
        }else if(getCurrentLanguage().equals("english")){
            rbEnglish.setChecked(true);
        }
    }

}
