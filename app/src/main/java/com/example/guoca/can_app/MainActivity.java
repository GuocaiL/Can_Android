package com.example.guoca.can_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.guoca.can_app.adapter.GuideAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private TextView zhuce,forget_pass;
    private ViewPager login_viewpager;
    private ImageButton methods[];
    private Button login_button;
    private List<View> views;
    private GuideAdapter guideAdapter;

    private void initView(){
        login_viewpager=(ViewPager) findViewById(R.id.login_viewpager);
        views =new ArrayList<View>();
        views.add(LayoutInflater.from(this).inflate(R.layout.login_guide,null));
        views.add(LayoutInflater.from(this).inflate(R.layout.tel_login_guide,null));
        guideAdapter=new GuideAdapter(this,views);
        login_viewpager.setAdapter(guideAdapter);
        methods=new ImageButton[]{
                (ImageButton) findViewById(R.id.count_pass_method_white),
                (ImageButton) findViewById(R.id.count_pass_method_blue),
                (ImageButton) findViewById(R.id.veri_login_btn_white),
                (ImageButton) findViewById(R.id.veri_login_btn_blue)
        };
        zhuce= (TextView)views.get(0).findViewById(R.id.zhuce);
        forget_pass= (TextView) views.get(0).findViewById(R.id.forget_pass);
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
        forget_pass.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent start_alter_pass=new Intent(MainActivity.this,AlterPassActivity.class);
                startActivity(start_alter_pass);
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
        if(position==0){
            methods[0].setVisibility(View.VISIBLE);
            methods[1].setVisibility(View.GONE);
            methods[2].setVisibility(View.GONE);
            methods[3].setVisibility(View.VISIBLE);
        }
        else {
            methods[0].setVisibility(View.GONE);
            methods[1].setVisibility(View.VISIBLE);
            methods[2].setVisibility(View.VISIBLE);
            methods[3].setVisibility(View.GONE);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
