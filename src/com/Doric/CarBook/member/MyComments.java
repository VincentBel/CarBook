package com.Doric.CarBook.member;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Doric.CarBook.Constant;
import com.Doric.CarBook.R;
import com.Doric.CarBook.car.ImageLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyComments extends Activity {

    UserFunctions userFunctions;
    ProgressDialog progressDialog;
    JSONObject commentsData;
    JSONObject comments;
    ListView commentList;
    int columnWidth = 300;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private List<ImageView> imageViews = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFunctions = new UserFunctions(getApplicationContext());
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_launcher);
        imageViews.add(imageView);
        imageViews.add(imageView);

        setContentView(R.layout.my_comments);
        commentList = (ListView) findViewById(R.id.my_comments_list);
        new GetMyComments().execute();
    }

    private class GetMyComments extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            commentsData = userFunctions.getMyComments();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                comments = commentsData.getJSONObject("comments");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new GetPicData().execute();
            commentList.setAdapter(new CommentsAdapter(MyComments.this, comments));
        }
    }

    private class GetPicData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
                String mImageUrl = null;
                // ��ȡ��Ӧ������ͼƬURL
            for (Integer i = 0; i < comments.length(); i++) {
                try {
                    mImageUrl = Constant.BASE_URL + "/" + comments.getJSONObject(i + "").getString("pictures_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // �ӻ����м���
                Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);

                if (imageBitmap == null) {
                    System.out.println("�����в�����λͼ����Ҫ���ػ��sd������ͼƬ");
                    // �����в�����λͼ����Ҫ���ػ��sd������ͼƬ
                    imageBitmap = loadImage(mImageUrl);
                } else {
                    System.out.println("�����д���λͼ������Ҫ����ͼƬ");
                }
                // ΪViewPager���ͼƬ

                imageViews.get(i).setImageBitmap(imageBitmap);
                imageViews.get(i).setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                        300);
                if (bitmap != null) {
                    // �ɹ���ȡͼƬ����ͼƬ���뻺��
                    System.out.println("�ɹ���ȡͼƬ����ͼƬ���뻺��");
                    imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                    return bitmap;
                }
            }
            return null;
        }

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

        public String Transform(String str){
            byte[] b = str.getBytes();
            char[] c = new char[b.length];
            for (int i=0;i<b.length;i++){
                if(b[i]!=' ')
                    c[i] = (char)(b[i]&0x00FF);

            }
            return new String(c);
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

    private class CommentsAdapter extends ArrayAdapter<String> {

        private Context context;
        private JSONObject values;

        public CommentsAdapter(Context context, JSONObject values) {
            super(context, R.layout.my_comments_item);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.my_comments_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.carNameTextView = (TextView) rowView.findViewById(R.id.my_comments_car_name);
                viewHolder.imageView = (ImageView) rowView.findViewById(R.id.my_comments_list_image);
                rowView.setTag(viewHolder);
            }
            String carName = null;
            String comment = null;
            try {
                carName = values.getJSONObject(position + "").getString("carname");
                comment = values.getJSONObject(position + "").getString("short_comments");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int commentMaxLength = 50;
            if (comment.length() > commentMaxLength) {
                comment = comment.substring(0, commentMaxLength) + "...";
            }
            System.out.println("comment:" + comment);
            ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            viewHolder.carNameTextView.setText(carName);
            viewHolder.commentsTextView.setText(comment);
            viewHolder.imageView.setImageBitmap(imageViews.get(position).getDrawingCache());
            return rowView;
        }

    }

    static class ViewHolder {
        TextView carNameTextView;
        TextView commentsTextView;
        ImageView imageView;
    }
}
