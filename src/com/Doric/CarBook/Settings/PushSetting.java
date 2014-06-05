package com.Doric.CarBook.settings;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import com.Doric.CarBook.MainActivity;
import com.Doric.CarBook.R;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.jpush.android.api.JPushInterface;
import com.Doric.CarBook.MainActivity;
import com.Doric.CarBook.R;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
/**
 * Created by Sunyao_Will on 2014/6/4.
 */
public class PushSetting extends InstrumentedActivity {
    // PushSetting��Context
    private static Context PushSettingContext ;
    // ���ͷ���Ŀ���switch
    Switch acceptPushSwitch = null;
    // ���ý�������ʱ��İ�ť
    Button startPushTimeBtn = null;
    // ����ֹͣ����ʱ��İ�ť
    Button endPushTimeBtn = null;
    // ���ÿ�ʼʱ���calendar
    Calendar startPushTime = null;
    // ���ý���ʱ���calendar
    Calendar endPushTime = null;
    // �û�����
    String PREFS_NAME = "com.Doric.CarBook";
    SharedPreferences settings = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_settings);
        // ���ù���
        settings= PreferenceManager.getDefaultSharedPreferences(this);
        // Activity��������
        PushSettingContext= this;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("��������");

        // Ϊswitch��ʼ��
        acceptPushSwitch = (Switch) this.findViewById(R.id.acceptPushSwitch);
        if (acceptPushSwitch!=null) {
            acceptPushSwitch.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // �������ͷ���
                            if (isChecked) {
                                JPushInterface.resumePush(getApplicationContext());
                            }else{
                                // �ر����ͷ���
                                JPushInterface.stopPush(getApplicationContext());
                            }
                        }
                    }
            );
        }
        // ��ʼ��calendar�ؼ�
        startPushTime = Calendar.getInstance();
        endPushTime = Calendar.getInstance();

        startPushTime.set(Calendar.HOUR_OF_DAY,settings.getInt("startTimeHour",6));
        System.out.println(settings.getInt("startTimeHour",6));
        endPushTime.set(Calendar.HOUR_OF_DAY,settings.getInt("endTimeHour",23));
        System.out.println(settings.getInt("endTimeHour",23));
        startPushTime.set(Calendar.MINUTE,settings.getInt("startTimeMin",0));
        endPushTime.set(Calendar.MINUTE,settings.getInt("endTimeMin",0));

        // ��һ�����ս�������
        final Set<Integer> days = new HashSet<Integer>();
        for (int i=0;i<7;i++)
            days.add(i);
        // Button��ʾ������
        final String startPushText = "��ʼ��������ʱ��:"+"       ";
        final String endPushText = "ֹͣ��������ʱ��:"+"       ";
        // ΪButton��ʼ��
        startPushTimeBtn = (Button) this.findViewById(R.id.startPushTimeBtn);
        startPushTimeBtn.setText(startPushText+startPushTime.get(Calendar.HOUR_OF_DAY)
                +":"+startPushTime.get(Calendar.MINUTE));
        // ��������¼������ڵ���󵯳�TimePickerDialog
        startPushTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(PushSettingContext,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int endTimeHour = endPushTime.get(Calendar.HOUR_OF_DAY);
                        int endTimeMin = endPushTime.get(Calendar.MINUTE);

                        System.out.println(startPushTime.get(Calendar.HOUR_OF_DAY));

                        // �Կ�ʼʱ��ͽ���ʱ������жϣ�����������������
                        if (check(hourOfDay,minute,endTimeHour,endTimeMin)) {
                            startPushTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            startPushTime.set(Calendar.MINUTE, minute);
                            startPushTime.set(Calendar.SECOND, 0);
                            startPushTime.set(Calendar.MILLISECOND, 0);
                            // ��ȡ�û����õ�ʱ��
                            int startTimeHour = startPushTime.get(Calendar.HOUR_OF_DAY);

                            //����JPush api����Pushʱ��
                            JPushInterface.setPushTime(getApplication().getApplicationContext(),
                                    days, startTimeHour, endTimeHour);
                            startPushTimeBtn.setText(startPushText + startPushTime.get(Calendar.HOUR_OF_DAY)
                                    + ":" + startPushTime.get(Calendar.MINUTE));
                            Toast.makeText(PushSettingContext, "���óɹ�", Toast.LENGTH_LONG).show();
                            // ���濪ʼ����ʱ��
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("startTimeHour", startPushTime.get(Calendar.HOUR_OF_DAY));
                            editor.putInt("startTimeMin",startPushTime.get(Calendar.MINUTE));
                            editor.commit();

                        }else{
                            Toast.makeText(PushSettingContext, "��ȷ����ʼʱ�����ڽ���ʱ��", Toast.LENGTH_LONG).show();
                        }
                    }
                },startPushTime.get(Calendar.HOUR_OF_DAY),startPushTime.get(Calendar.MINUTE),true).show();
            }
        });

        endPushTimeBtn = (Button) this.findViewById(R.id.endPushTimeBtn);
        endPushTimeBtn.setText(endPushText+endPushTime.get(Calendar.HOUR_OF_DAY)
                +":"+endPushTime.get(Calendar.MINUTE));
        endPushTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(PushSettingContext,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int startTimeHour = startPushTime.get(Calendar.HOUR_OF_DAY);
                        int startTimeMin = startPushTime.get(Calendar.MINUTE);

                        // �Կ�ʼʱ��ͽ���ʱ������жϣ�����������������
                        if (check(startTimeHour,startTimeMin,hourOfDay,minute)) {
                            endPushTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            endPushTime.set(Calendar.MINUTE,minute);
                            endPushTime.set(Calendar.SECOND, 0);
                            endPushTime.set(Calendar.MILLISECOND, 0);
                            // ��ȡ�û����õ�ʱ��
                            int endTimeHour = endPushTime.get(Calendar.HOUR_OF_DAY);

                            //����JPush api����Pushʱ��
                            JPushInterface.setPushTime(getApplication().getApplicationContext(),
                                    days, startTimeHour, endTimeHour);
                            endPushTimeBtn.setText(endPushText + endPushTime.get(Calendar.HOUR_OF_DAY)
                                    + ":" + endPushTime.get(Calendar.MINUTE));
                            Toast.makeText(PushSettingContext, "���óɹ�", Toast.LENGTH_LONG).show();
                            // ����ֹͣ����ʱ��
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("endTimeHour",endPushTime.get(Calendar.HOUR_OF_DAY));
                            editor.putInt("endTimeMin",endPushTime.get(Calendar.MINUTE));
                            editor.commit();

                        }else{
                            Toast.makeText(PushSettingContext, "��ȷ����ʼʱ�����ڽ���ʱ��", Toast.LENGTH_LONG).show();
                        }
                    }
                },endPushTime.get(Calendar.HOUR_OF_DAY),endPushTime.get(Calendar.MINUTE),true).show();
            }
        });

    }

    boolean check (int aHour, int aMin,int bHour, int bMin){
        if (aHour<bHour)
            return true;
        if (aHour>bHour)
            return false;
        if (aHour==bHour){
            if(aMin>bMin)
                return false;
            else
                return true;
        }
        return true;
    }
}
