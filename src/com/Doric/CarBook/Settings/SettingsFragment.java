package com.Doric.CarBook.settings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.Doric.CarBook.R;

import java.io.File;
import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        rootView = getView();
        ArrayList<String> a = new ArrayList<String>();
        super.onActivityCreated(savedInstanceState);

        String[] systemSettingsTitles = new String[] {
                getResources().getString(R.string.clear_cache),
                getResources().getString(R.string.push_settings)

        };
        String[] baseSettingsTitles = new String[] {
                getResources().getString(R.string.feed_back),
                getResources().getString(R.string.new_version),
                getResources().getString(R.string.about),
                getResources().getString(R.string.exit)
        };
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, systemSettingsTitles);
        ListView systemSettingsList = (ListView) rootView.findViewById(R.id.system_settings_list);
        systemSettingsList.setAdapter(adapter);
        systemSettingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: //「清除缓存」
                        File sdDir = null;
                        boolean sdCardExist = Environment.getExternalStorageState()
                                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
                        if (sdCardExist) {
                            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                        }
                        String Dir = sdDir+ "/CarBook/Cache";
                        File file = new File(Dir);
                        if(file.isDirectory()){
                            File[] files=file.listFiles();
                            for(int j=0;j<files.length;j++){
                                files[j].delete();
                            }
                            file.delete();
                            Toast.makeText(getActivity().getApplicationContext(),"清除完成",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Log.d("Wrong","Delete Wrong");
                        }
                        break;
                    //「推送设置」
                    case 1: {
                        Intent intent = new Intent(getActivity(),PushSetting.class);
                        startActivity(intent);
                    }
                        break;
                }
            }
        });

        ArrayAdapter baseSettingsAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, baseSettingsTitles);
        ListView baseSettingsList = (ListView) rootView.findViewById(R.id.base_settings_list);
        baseSettingsList.setAdapter(baseSettingsAdapter);
        baseSettingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: //「反馈」
                        Intent feedbackIntent = new Intent(getActivity(), Feedback.class);
                        startActivity(feedbackIntent);
                        break;
                    case 1: // 「新版本检测」
                        Toast.makeText(getActivity().getApplicationContext(), "当前是最新版本", Toast.LENGTH_LONG).show();
                        break;
                    case 2: // 「关于」
                        startActivity(new Intent(getActivity(), About.class));
                        break;
                    case 3: // 「退出」
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                        break;
                }
            }
        });
    }
}
