package com.cxy.hans;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.cxy.hans.Bean.HansBean;
import com.cxy.hans.Util.FileUtils;
import com.cxy.hans.widget.LineChartView;

import java.util.List;

/**
 * Created by hasee on 2017/11/7.
 */
public class DetailActivity extends AppCompatActivity {
    LineChartView detailLineChartView;
    String path;
    MyTask mTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initview();
    }

    private void initview(){
        if(getIntent() != null){
            path = getIntent().getStringExtra("path");
        }else {
            finish();
        }
        if(TextUtils.isEmpty(path)){
            Toast.makeText(this,"当天即路文件无数据", Toast.LENGTH_LONG).show();
            finish();
        }
        detailLineChartView = (LineChartView) findViewById(R.id.detail_LineChartView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mTask = new MyTask();
        mTask.execute(path);
    }

    private class MyTask extends AsyncTask<String, Integer, List<HansBean>> {

        @Override
        protected List<HansBean> doInBackground(String... params) {
            List<HansBean> list = null;
            try {
                list = FileUtils.readLog(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HansBean> hansBeen) {
            super.onPostExecute(hansBeen);
            detailLineChartView.setMlist(hansBeen);
        }
    }
    }
