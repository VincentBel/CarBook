package com.Doric.CarBook.member;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Doric.CarBook.R;
import com.Doric.CarBook.utility.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserCollection extends Fragment {

    // �û��ղ�JSON��
    private JSONObject userCollection = null;
    // ������
    ProgressDialog progressDialog;
    // ��ȡ�������ݿ�Ĺ�����ʵ��
    private UserFunctions userFunctions;
    // ��̬����û��ղص��б�
    ListView userCollectionList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_collection, container, false);
        // ��ȡ�������ݿ�Ĺ�����ʵ��
        userFunctions = new UserFunctions(getActivity());
        // ��ʼ���û��ղ��б�
        userCollectionList = (ListView) (rootView != null ? rootView.findViewById(R.id.bodyStruText) : null);
        // �����첽�̻߳�ȡJson����������fragment
        new GetUserCollection().execute();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*

            ��̬����ղ���Ŀ
        int userCollectionCount =2;
        */
        /*
        ListView userCollectionList = (ListView) findViewById(R.id.userCollectionList);
        ArrayList<Map<String, Object>> list = getData();
        SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.sale_company_list,
                new String[]{"storeName","storeAddr"},
                new int[]{R.id.storeName,R.id.storeAddr});
        if (userCollectionList != null) {
            userCollectionList.setAdapter(adapter);
            setListViewHeightBasedOnChildren(userCollectionList);
        }
        */
    }
    /*
     *  �첽��ȡ�û��ղع�����
     */
    private class GetUserCollection extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //����ʱ����
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("������..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //  �ӱ������ݿ��ȡ�û��ղ�Json��
            //userCollection = userFunctions.getUserCollection();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initFragment();
        }
    }
    private void initFragment(){

        ArrayList<Map<String, Object>> list = getData();
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),list,R.layout.user_collection_list,
                new String[]{"userCollectionThumPic","carNameText","carGradeText","carPriceText"},
                new int[]{R.id.userCollectionThumPic,R.id.carNameText,R.id.carGradeText,R.id.carPriceText});
        if (userCollectionList != null) {
            userCollectionList.setAdapter(adapter);
            setListViewHeightBasedOnChildren(userCollectionList);
        }
    }

    ArrayList<Map<String, Object>> getData(){
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject carInCollection = null;
        try {
            for (Integer i=1;i<= userCollection.getInt("number");i++){
                map =  new HashMap<String, Object>();
                carInCollection = userCollection.getJSONObject("car_"+i.toString());
                map.put("carNameText",carInCollection.getString("brand")+" "+carInCollection.getString("brand_series")
                        +" "+carInCollection.getString("model_number"));
                map.put("carGradeText",carInCollection.getString("grade"));
                map.put("carPriceText",carInCollection.getString("price"));

                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // ��ȡListView��Ӧ��Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()�������������Ŀ
            View listItem = listAdapter.getView(i, null, listView);
            // ��������View �Ŀ��
            if (listItem != null) {
                listItem.measure(0, 0);
            }
            // ͳ������������ܸ߶�
            if (listItem != null) {
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (params != null) {
            params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        }
        // listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
        // params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
        listView.setLayoutParams(params);
    }

}
