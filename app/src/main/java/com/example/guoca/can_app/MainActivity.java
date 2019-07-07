package com.example.guoca.can_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.guoca.can_app.adapter.GuideAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private TextView zhuce,forget_pass;
    private ViewPager login_viewpager;
    private Button login_button;
    private List<View> views;
    private GuideAdapter guideAdapter;
    private TextView methods[];

    private void initView(){
        login_viewpager=(ViewPager) findViewById(R.id.login_viewpager);
        views =new ArrayList<View>();
        views.add(LayoutInflater.from(this).inflate(R.layout.login_guide,null));
        views.add(LayoutInflater.from(this).inflate(R.layout.tel_login_guide,null));
        guideAdapter=new GuideAdapter(this,views);
        login_viewpager.setAdapter(guideAdapter);
        methods=new TextView[]{
                (TextView) findViewById(R.id.count_pass_method),
                (TextView) findViewById(R.id.tel_veri_method)
        };
        zhuce= (TextView)views.get(0).findViewById(R.id.zhuce);

    }

    private void setLisenter(){
        login_viewpager.setOnPageChangeListener(this);
        zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_zhuce = new Intent(MainActivity.this,
                        RegisterActivity.class);
                startActivity(start_zhuce);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setLisenter();



    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        methods[position].setTextColor(Color.parseColor("#FFE4C4"));
        methods[Math.abs(position-1)].setTextColor(Color.parseColor("#A9A9A9"));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
