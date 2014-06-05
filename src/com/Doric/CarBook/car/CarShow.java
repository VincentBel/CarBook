package com.Doric.CarBook.car;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.Doric.CarBook.Constant;
import com.Doric.CarBook.R;
import com.Doric.CarBook.member.UserFunctions;
import com.Doric.CarBook.utility.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarShow extends FragmentActivity implements android.app.ActionBar.TabListener {
    // ���ñ�ǩ����
    public static final int MAX_TAB_SIZE = 5;
    // �����������ݵ�JSONObject
    static JSONObject carInfo;
    // ��ȡ��bundle
    static Bundle bundle;
    // ��ȡ��car_id
    static String car_id = null;
    // ��ʼ��carId
    int carId = 0;
    // ��ȡ������Ϣ��Url
    String CarInfoUrl = Constant.BASE_URL + "/showcar.php";
    // �������������������Ĳ����б�
    List<NameValuePair> carParamsRequest = new ArrayList<NameValuePair>();
    // ������
    ProgressDialog progressDialog;
    // ʵ����������Ϣ
    CarInfor car = new CarInfor();
    //ʵ��һ���������һ����ģ���������������ͼƬ���������������ۡ������ۡ��ĳ�����Ϣչʾҳ��
    private ViewPager mViewPager;
    //�û����ܺ��� ������Ӻ�ȡ�����ղ�
    UserFunctions userFunctions;
    /*
    *   ��ȡbundle���������첽�̻߳�ȡJSON��
    */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_show);

        userFunctions = new UserFunctions(getApplicationContext());

        //�������󲢻�ȡJson��
        bundle = getIntent().getExtras();
        // �ж��Ƿ�����ͷ����л�ȡ��Ϣ
        if (bundle.get("cn.jpush.android.EXTRA")!=null) {
            Log.d("CarShow","bundle.get(\"cn.jpush.android.EXTRA\")!=null");
            JSONObject jo = null;
            try {
                jo = new JSONObject(bundle.getString("cn.jpush.android.EXTRA"));
                Log.d("CarShow",jo.getString("car_id"));

                // �������������͵�����
                carParamsRequest.add(new BasicNameValuePair("tag", ("showcar")));
                carParamsRequest.add(new BasicNameValuePair("car_id", (jo.getString("car_id"))));
                car_id = jo.getString("car_id");
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }else{
            // �������������͵�����
            Log.d("CarShow","bundle.get(\"cn.jpush.android.EXTRA\")==null");
            carParamsRequest.add(new BasicNameValuePair("tag", HotCarShow.Transform("showcar")));
            carParamsRequest.add(new BasicNameValuePair("car_id", HotCarShow.Transform(bundle.getString("car_id"))));
            car_id = bundle.getString("car_id");
        }


        //ͨ�����̹߳���carʵ������ʼ��Activity
        new GetCarInfo().execute();
    }

    /*
    * ��ʼ��ViewPager
    */
    private void findViewById() {
        mViewPager = (ViewPager) this.findViewById(R.id.pager);
    }
    /*
    * ��ʼ��mune���ղذ�ť
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.car_show, menu)   ;
        MenuItem collectItem = menu.findItem(R.id.action_add_to_collection);
        if (userFunctions.isCollected(carId)) {
            collectItem.setIcon(R.drawable.ic_action_collected);
        } else {
            collectItem.setIcon(R.drawable.ic_action_add_to_collection);
        }
        return true;
    }
    /*
    * ����ղذ�ť���л�ͼ�꣬�������첽�̣߳���ʵ������ղ�
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.finish();
                return true;
            case R.id.action_add_to_collection:
                new CollectAsync().execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    * ��ʼ��View
    */
    private void initView() {
        // ��ʾactionBar�в˵�
        final android.app.ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        if (mActionBar != null) {
            mActionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);
        }


        // ���fragment��������
        TabFragmentPagerAdapter mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mActionBar.setSelectedNavigationItem(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        //��ʼ�� ActionBar
        for (int i = 0; i < MAX_TAB_SIZE; i++) {
            mActionBar.addTab(mActionBar.newTab().
                    setText(mAdapter.getPageTitle(i)).
                    setTabListener(this));
        }
    }


    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }
    /*
    *   ��ȡ������Ϣ���첽�̹߳�����
    */
    private class GetCarInfo extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            //����ʱ����
            progressDialog = new ProgressDialog(CarShow.this);
            progressDialog.setMessage("������..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected Void doInBackground(Void... params) {
            //���������������
            JSONParser jsonParser = new JSONParser();
            carInfo = jsonParser.getJSONFromUrl(CarInfoUrl, carParamsRequest);
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (carInfo != null) {
                try {
                    // �жϷ��������Ƿ��������������Ϣ
                    if (carInfo.getInt("success")==0){
                        Toast.makeText(CarShow.this.getApplicationContext(), "���ݿ��쳣", Toast.LENGTH_LONG).show();
                    }else{
                        // ͨ��series��model_number,���쳵����
                        String carName = carInfo.getString("series")+" "+carInfo.getString("model_number");
                        System.out.println("sd" + carName);
                        car.setCarName(carName);
                        // �Գ�������ΪactionBar��title
                        if (getActionBar() != null) {
                            getActionBar().setTitle(car.getCarName());
                        }
                        carId = Integer.parseInt(carInfo.getString("car_id"));
                        invalidateOptionsMenu();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                findViewById();
                // ��ʼ��fragment
                initView();
            } else {
                Toast.makeText(CarShow.this.getApplicationContext(), "�޷��������磬���������ֻ���������", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class CollectAsync extends AsyncTask<Void, Void, Integer> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Integer doInBackground(Void... params) {
            //userFunctions.getMyCollection();   //������
            if (!userFunctions.isCollected(carId)) {
                if (userFunctions.addToCollection(carId)) {
                    //�ղسɹ�������1
                    return 1;
                }
            } else {
                if (userFunctions.cancelCollect(carId)) {
                    //ȡ���ղسɹ�������2
                    return 2;
                }
            }
            //ʧ�ܣ�����0
            return 0;
        }

        protected void onPostExecute(Integer result) {
            if (result == 1) {
                invalidateOptionsMenu();
                Toast.makeText(getApplicationContext(), "���ղ�", Toast.LENGTH_SHORT).show();
            } else if (result == 2) {
                invalidateOptionsMenu();
                Toast.makeText(getApplicationContext(), "��ȡ���ղ�", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
    * fragment��������
    */
    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*
        * ȷ����Ҫ�����fragment
        */
        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;
            switch (arg0) {
                case 0:
                    // ����չʾ��������
                    ft = new SummaryFragment();
                    break;
                case 1:
                    // ����չʾͼƬ����
                    ft = new PictureFragment();
                    break;
                case 2:
                    // ����չʾ��������
                    ft = new ParameterFragment();
                    break;
                case 3:
                    // ����չʾ���۽���
                    ft = new PriceFragment();
                    break;
                case 4:
                    // ����չʾ���۽���
                    ft = new CommentFragment();
                    break;
                default:
                    break;
            }
            return ft;
        }

        @Override
        public int getCount() {

            return MAX_TAB_SIZE;
        }
        /*
        * Ϊfragment ����title
        */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "����";
                case 1:
                    return "ͼƬ";
                case 2:
                    return "����";
                case 3:
                    return "����";
                case 4:
                    return "����";
                default:
                    return "";
            }
        }
    }
}
