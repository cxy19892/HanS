package com.cxy.hans;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.cxy.hans.Bean.HansBean;
import com.cxy.hans.Util.AudioRecordDemo;
import com.cxy.hans.Util.FileUtils;
import com.cxy.hans.Util.FormatDateUtils;
import com.cxy.hans.intface.AudioRecordListenner;
import com.cxy.hans.widget.DialView;
import com.cxy.hans.widget.LineChartView;

public class MainActivity extends AppCompatActivity {

    TextView value;
    DialView mDialView;
    LineChartView mLineChartView;
    Toolbar toolbar;
    String TAG = "MainActivity";
    private AudioRecordDemo mAudioRecord;
    public final String PATH_SAVE_AUDIO = Environment.getExternalStorageDirectory().getPath() + "/Hans/Audio";
    private String audioname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        value = (TextView) findViewById(R.id.value);
        mDialView = (DialView) findViewById(R.id.mdial_view);
        mLineChartView = (LineChartView) findViewById(R.id.mLineChartView);
        mDialView.setIsNight(true);
        initView();

    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
            case R.id.start:
                init();
                break;
            case R.id.stop:
                if (mAudioRecord != null) {
                    mAudioRecord.stopRecord();
                }
                break;
        }
        return true;
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("鼾声");
    }

    private void init() {
        audioname = FormatDateUtils.getCurrentDate("yyyy-MM-dd") + ".txt";
        FileUtils.InitFilePath(PATH_SAVE_AUDIO + "/" + audioname);
        mAudioRecord = AudioRecordDemo.getInstance();
        mAudioRecord.setRecordListenner(new AudioRecordListenner() {
            @Override
            public void AudioRecordCallBack(int value) {
                StringBuilder sb = new StringBuilder();
                sb.append(value).append(";").append(System.currentTimeMillis()).append("\n");
                FileUtils.writeFile(PATH_SAVE_AUDIO + "/" + audioname, sb.toString(), true);
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = value;
                handler.sendMessage(msg);
            }
        });
        mAudioRecord.getNoiseLevel();
    }

    @Override
    protected void onDestroy() {
        if (mAudioRecord != null) {
            mAudioRecord.stopRecord();
        }
        super.onDestroy();

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(value != null)
                        value.setText("" + msg.arg1);
                    mDialView.setValue(msg.arg1);
                    HansBean bean = new HansBean(msg.arg1, FormatDateUtils.getCurrentDate("HH:mm"));
                    mLineChartView.addData(bean);
                    break;
            }
        }
    };
}
