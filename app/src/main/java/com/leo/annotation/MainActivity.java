package com.leo.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;



//@Hello(value = "NIU BAO")//这样就会生成文件了
@IActivity
public class MainActivity extends AppCompatActivity {

    @IView( value = R.id.center)
    public TextView mCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ManagerFindByMainActivity.findById(this);
        mCenter.setText("中");
    }
}
