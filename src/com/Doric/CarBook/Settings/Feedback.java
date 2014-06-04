package com.Doric.CarBook.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.Doric.CarBook.R;
import org.json.JSONException;
import org.json.JSONObject;


public class Feedback extends Activity implements OnClickListener {

    String username;    //�û���
    String password;    //����
    private JSONObject loginInfo;       //�����������õ���json����
    //����ؼ�
    private EditText edtContent;
    private Button btnSend, btnBack;
    private ProgressDialog progressDialog;   //�첽����ʱ��ʾ�Ľ�����

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        //���ÿؼ�
        edtContent = (EditText) findViewById(R.id.content);
        btnSend = (Button) findViewById(R.id.send);
        btnBack = (Button) findViewById(R.id.back);

        //��Ӽ�����
        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //����Actionbar
        getActionBar().hide();
    }

    public void onClick(View v) {

        int id = v.getId();

        //�����͡���ť
        if (id == R.id.send) {

            //�ж������Ƿ�Ϊ��
            if (edtContent.getText().equals("")) {
                Toast.makeText(Feedback.this, "���벻��Ϊ��", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(Feedback.this, "���ͳɹ���", Toast.LENGTH_LONG).show();
                Feedback.this.finish();
            }
        }

        //"����"��ť
        if (id == R.id.back) {
            Feedback.this.finish();
        }

    }

    private class SendContent extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            //����"���ڷ���"��
            progressDialog = new ProgressDialog(Feedback.this);
            progressDialog.setMessage("���ڷ���..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected Void doInBackground(Void... params) {

            //���������������
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            //�ж��յ���json�Ƿ�Ϊ��
            if (loginInfo != null) {
                try {
                    //�˻���Ϣ��֤ʧ��
                    if (loginInfo.getString("success").equals("0")) {
                        Toast.makeText(Feedback.this, "��������", Toast.LENGTH_LONG).show();
                    }
                    //�˻���Ϣ��֤�ɹ�
                    else {
                        Toast.makeText(Feedback.this, "��������ɹ���", Toast.LENGTH_LONG).show();
                        Feedback.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Feedback.this, "����ʧ�ܣ��������������Ƿ�����", Toast.LENGTH_LONG).show();
            }
        }
    }
}
