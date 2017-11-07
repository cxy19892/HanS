package com.cxy.hans;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cxy.hans.Util.FileUtils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/11/6.
 */
public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Toolbar toolbar;
    ListView mListView;
    private List<String> mFileName;
    public final String PATH_SAVE_AUDIO = Environment.getExternalStorageDirectory().getPath() + "/Hans/Audio";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his);
        ButterKnife.bind(this);
        initview();
    }


    private void initview(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mListView = (ListView) findViewById(R.id.my_list_view);
        setSupportActionBar(toolbar);
        setTitle("历史记录");
        toolbar.setNavigationIcon(android.R.drawable.ic_media_next);
        mFileName = FileUtils.getFileList(PATH_SAVE_AUDIO);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,mFileName);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("chen", "onItemClick: "+mFileName.get(position));
        Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
        intent.putExtra("path", PATH_SAVE_AUDIO+"/"+mFileName.get(position));
        startActivity(intent);
    }


}
