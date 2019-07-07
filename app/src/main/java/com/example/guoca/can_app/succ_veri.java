package com.example.guoca.can_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class succ_veri extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_succ_veri);
        Bundle extras = getIntent().getExtras();
        String userName = extras.getString("userName");
        Toast.makeText(this, "尊敬的用户"+userName+",您的账号已验证成功！！！！！！！！！！！！！！！", Toast.LENGTH_SHORT).show();
    }
}
