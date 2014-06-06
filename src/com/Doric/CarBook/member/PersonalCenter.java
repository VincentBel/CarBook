package com.Doric.CarBook.member;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Doric.CarBook.Constant;
import com.Doric.CarBook.R;
import com.Doric.CarBook.utility.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonalCenter extends Fragment implements View.OnClickListener {

    UserFunctions userFunctions;
    private String name;
    private String whichHead = "0";

    //����ؼ�
    private Button btnInformation, btnComment, btnLogOut;
    private TextView textView;

    //������������ر���
    private String headURL = Constant.BASE_URL + "/user_setting.php";  //��¼�����url,��ؼ���http://��https://
    private List<NameValuePair> headParams;    //��¼ʱ���͸�������������
    private JSONObject headInfo;       //�����������õ���json����

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.personal_center, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mView = getView();

        userFunctions = new UserFunctions(getActivity().getApplicationContext());
        name = userFunctions.getUsername();

        //���ÿؼ�
        btnInformation = (Button) mView.findViewById(R.id.button_my_information);
        btnComment = (Button) mView.findViewById(R.id.button_my_comments);
        btnLogOut = (Button) mView.findViewById(R.id.button_log_out);
        textView = (TextView) mView.findViewById(R.id.bar_username);

        textView.setText(name);

        //��Ӽ�����
        btnComment.setOnClickListener(this);
        btnInformation.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);

        //����Actionbar
        //getActivity().getActionBar().hide();

        //�����û���Ϣ��������
        headParams = new ArrayList<NameValuePair>();
        headParams.add(new BasicNameValuePair("tag", "get_avatar"));
        headParams.add(new BasicNameValuePair("username", name));

        //�첽����
        new getHead().execute();
    }

    public void onClick(View v) {
        int id = v.getId();

        //"�ҵ�����"��ť
        if (id == R.id.button_my_information) {
            Intent intent = new Intent(getActivity(), MyInformation.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }

        //"�ҵ�����"��ť
        if (id == R.id.button_my_comments) {
            Intent intent = new Intent(getActivity(), MyComments.class);
            startActivity(intent);
        }

        //"�˳���¼"��ť
        if (id == R.id.button_log_out) {
            logOutDialog();
        }
    }

    //�˳���¼�Ի���
    public void logOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("ȷ��Ҫ�˳���¼��");
        builder.setTitle("�˳���¼");
        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                userFunctions.logoutUser();
                dialog.dismiss();
                /*FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new HotCarShow();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();*/
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });
        builder.create().show();
    }

    //��ȡͷ��
    private class getHead extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            //���������������
            JSONParser jsonParser = new JSONParser();
            headInfo = jsonParser.getJSONFromUrl(headURL, headParams);
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //�ж��յ���json�Ƿ�Ϊ��
            if (headInfo != null) {
                try {
                    if (headInfo.getString("success").equals("1")) {
                        whichHead = headInfo.getString("status");
                        setHead(whichHead);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //����ͷ��
    public void setHead(String which) {
        ImageView imageHead = (ImageView)getView().findViewById(R.id.pc_head);
        switch ( Integer.parseInt(which) ){
            case 1: imageHead.setBackgroundResource(R.drawable.head1); break;
            case 2: imageHead.setBackgroundResource(R.drawable.head2); break;
            case 3: imageHead.setBackgroundResource(R.drawable.head3); break;
            case 4: imageHead.setBackgroundResource(R.drawable.head4); break;
            case 5: imageHead.setBackgroundResource(R.drawable.head5); break;
            case 6: imageHead.setBackgroundResource(R.drawable.head6); break;
            case 7: imageHead.setBackgroundResource(R.drawable.head7); break;
            case 8: imageHead.setBackgroundResource(R.drawable.head8); break;
            case 9: imageHead.setBackgroundResource(R.drawable.head9); break;
            default:imageHead.setBackgroundResource(R.drawable.pc_default_head); break;
        }
    }

}
