package com.Doric.CarBook.member;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Doric.CarBook.Constant;
import com.Doric.CarBook.R;
import com.Doric.CarBook.car.CarShow;
import com.Doric.CarBook.car.ImageLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UserCollection extends Fragment {

    // �û��ղ�JSON��
    private JSONObject userCollection = null;
    // ������
    ProgressDialog progressDialog;
    // ��ȡ�������ݿ�Ĺ�����ʵ��
    private UserFunctions userFunctions;
    // ��̬����û��ղص��б�
    ListView userCollectionList = null;
    // ͼƬ�б�
    List<ImageView> imageViewList = new ArrayList<ImageView>();
    // ��ͼƬ����Ĺ�����
    private ImageLoader imageLoader=ImageLoader.getInstance();;
    // ͼƬ���
    private final int picWidth = 150;
    // ���û��ղص���ʾ
    TextView noCollectionTextView = null;
    //  ��¼�����ص�ͼƬ��ȣ������ڴ����
    private final int columnWidth = 480 ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_collection, container, false);
        // ��ȡ�������ݿ�Ĺ�����ʵ��
        userFunctions = new UserFunctions(getActivity());
        // ��ʼ���û��ղ��б�
        userCollectionList = (ListView) rootView.findViewById(R.id.userCollectionList);
        // ��ʼ��TextView
        noCollectionTextView = (TextView) rootView.findViewById(R.id.noCollectionTextView);
        Log.d("UserCollection","GetUserCollection.execute");
        // �����첽�̻߳�ȡJson����������fragment
        new GetUserCollection().execute();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    /*
     *  �첽��ȡ�û��ղع�����
     */
    private class GetUserCollection extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //����ʱ����
            Log.d("GetUserCollection","progressDialog");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("������..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //  �ӱ������ݿ��ȡ�û��ղ�Json��
            userCollection = userFunctions.getMyCollection();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            super.onPostExecute(aVoid);
            if (userCollection!=null){
                try {
                    if(userCollection.getInt("number")==0){
                        noCollectionTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initFragment();
                new GetPicData().execute();
            }else{
                Toast.makeText(getActivity(), "�޷��������磬���������ֻ���������", Toast.LENGTH_LONG).show();
            }

        }
    }
    private void initFragment(){
        Log.d("GetUserCollection","initFragment");
        // ��ȡ����
        ArrayList<Map<String, Object>> list = getData();
        // ����������������car_id,carName,carGrade,carPrice��ͼƬ��Ϣ
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),list,R.layout.user_collection_list,
                new String[]{"userCollectionThumPic","carNameText","carGradeText",
                        "carPriceText","car_id"},
                new int[]{R.id.userCollectionThumPic,R.id.carNameText,R.id.carGradeText,
                        R.id.carPriceText,R.id.car_id});
        // �Զ����bitmap
        adapter.setViewBinder(new myViewBinder());
        if (userCollectionList != null) {
            // ���������
            userCollectionList.setAdapter(adapter);
        }
        userCollectionList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ��ȡ��listView
                ListView tempList = (ListView) parent;
                HashMap<String, String> map = (HashMap<String, String>) tempList.getItemAtPosition(position);

                // ��map�л�ȡcar_id
                String car_id = map.get("car_id");

                Bundle bundle = new Bundle();
                bundle.putString("car_id",car_id);
                // ��ת����ӦActivity
                Intent intent = new Intent(getActivity(),CarShow.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    ArrayList<Map<String, Object>> getData(){
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject carInCollection = null;
        try {

            for (Integer i=1;i<= userCollection.getInt("number");i++){
                map =  new HashMap<String, Object>();
                // ��ȡ��Ӧ����
                carInCollection = userCollection.getJSONObject("car_"+i.toString());

                // ����Ϣ��ӵ�map��

                map.put("carNameText",carInCollection.getString("brand")+" "+carInCollection.getString("brand_series")
                        +" "+carInCollection.getString("model_number"));
                map.put("carGradeText",carInCollection.getString("grade"));
                map.put("carPriceText",carInCollection.getString("price"));
                map.put("car_id",carInCollection.getString("car_id"));
                map.put("userCollectionThumPic",R.drawable.ic_launcher);
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
    * �Զ����
    */
    class myViewBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if ((view instanceof ImageView) & (data instanceof Bitmap)) {
                ImageView iv = (ImageView) view;
                Bitmap bmp = (Bitmap) data;
                iv.setImageBitmap(bmp);
                return true;
            }
            return false;
        }
    }
    /*
    * ListView������Ϣ����
    */
    final Handler cwjHandler = new Handler();
    class UpdateRunnable implements  Runnable{
        SimpleAdapter simpleAdapter = null;
        public UpdateRunnable(SimpleAdapter sa){
            simpleAdapter = sa;
        }
        public void run() {
            simpleAdapter.notifyDataSetChanged();
        }
    };
    /**
     * �첽����ͼƬ������
     */

    private class GetPicData extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {

        }


        @Override
        protected Void doInBackground(Void... params) {
            JSONObject car = null;
            SimpleAdapter simpleAdapter = (SimpleAdapter) userCollectionList.getAdapter();

            for (Integer i = 1; i <= simpleAdapter.getCount(); i++) {
                Log.d("GetPicData", "download" + i.toString());
                Map<String, Object> map = (Map<String, Object>) simpleAdapter.getItem(i - 1);
                String mImageUrl = null;
                try {
                    // ��ȡ��Ӧ������ͼƬURL
                    car = userCollection.getJSONObject("car_" + i.toString());
                    mImageUrl = Constant.BASE_URL + "/" + car.getString("pictures_url");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // �ӻ����м���
                Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);
                if (imageBitmap == null) {
                    System.out.println("�����в�����λͼ����Ҫ���ػ��sd������ͼƬ");
                    // �����в�����λͼ����Ҫ���ػ��sd������ͼƬ
                    imageBitmap = loadImage(mImageUrl);
                }else {
                    System.out.println("�����д���λͼ������Ҫ����ͼƬ");
                }
                // ��imageBitmap ��ӵ� map��Ӧλ����
                map.put("userCollectionThumPic", imageBitmap);
                // ���͸�����Ϣ
                cwjHandler.post(new UpdateRunnable(simpleAdapter));

            }
            return null;
        }

        /*
         * ���ݴ����URL����ͼƬ���м��ء��������ͼƬ�Ѿ�������SD���У���ֱ�Ӵ�SD�����ȡ������ʹ����������ء�
         *
         * @param imageUrl ͼƬ��URL��ַ
         * @return ���ص��ڴ��ͼƬ��
         */
        private Bitmap loadImage(String imageUrl) {
            File imageFile = new File(getImagePath(imageUrl));
            if (!imageFile.exists()) {
                System.out.println("sd���в�����׼���ӷ���������");
                // sd���в�����׼���ӷ���������
                downloadImage(imageUrl);
            } else {
                System.out.println("sd���д���");
            }
            if (imageUrl != null) {
                System.out.println("��sd����Ӧ·������ͼƬ");
                // ��sd����Ӧ·������ͼƬ��������columnWidth����
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
                        columnWidth);
                if (bitmap != null) {
                    // �ɹ���ȡͼƬ����ͼƬ���뻺��
                    System.out.println("�ɹ���ȡͼƬ����ͼƬ���뻺��");
                    imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                    return bitmap;
                }
            }
            return null;
        }

        /*
         * ��ͼƬ���ص�SD������������
         *
         * @param  ��
         */
        private void downloadImage(String imageUrl) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.d("TAG", "monted sdcard");
            } else {
                Log.d("TAG", "has no sdcard");
            }
            HttpURLConnection con = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            File imageFile = null;
            try {
                URL url = new URL(Transform(imageUrl.replace(" ","%20")));
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                con.setDoInput(true);
                con.setDoOutput(true);
                bis = new BufferedInputStream(con.getInputStream());
                imageFile = new File(getImagePath(imageUrl));
                fos = new FileOutputStream(imageFile);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                int length;
                while ((length = bis.read(b)) != -1) {
                    bos.write(b, 0, length);
                    bos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (con != null) {
                        con.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                System.out.println("����ͼƬ��ȡ�ɹ�");
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
                        columnWidth);
                if (bitmap != null) {
                    imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                }
            }else {
                System.out.println("����ͼƬ��ȡ���ɹ�");
            }
        }

        protected void onPostExecute(Void aVoid) {

        }
    }


    private String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //�ж�sd���Ƿ����
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼
        }
        return sdDir.toString();

    }

    /**
     * ��ȡͼƬ�ı��ش洢·����
     *
     * @param imageUrl ͼƬ��URL��ַ��
     * @return ͼƬ�ı��ش洢·����
     */

    private String getImagePath(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        String imageTPath = imageUrl.substring(0, lastSlashIndex);
        // ͼƬ��ż���ʽ��׺
        String extra ="_"+ imageUrl.substring(imageUrl.lastIndexOf("/")+1);

        lastSlashIndex = imageTPath.lastIndexOf("/");
        String imageSeries = imageTPath.substring(lastSlashIndex + 1);  //  Series
        imageTPath = imageTPath.substring(0, lastSlashIndex);
        String imageName = imageTPath.substring(imageTPath.lastIndexOf("/") + 1);
        imageName = imageName + imageSeries + extra;
        System.out.println(imageName);
        // ͼƬ�Ĵ���·��
        String imageDir = getSDPath()
                + "/CarBook/Cache/";
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String imagePath = imageDir + imageName;

        return imagePath;
    }
    public static String Transform(String str){
        byte[] b = str.getBytes();
        char[] c = new char[b.length];
        for (int i=0;i<b.length;i++){
            if(b[i]!=' ')
                c[i] = (char)(b[i]&0x00FF);

        }
        return new String(c);
    }
}


