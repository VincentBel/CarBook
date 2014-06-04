package com.Doric.CarBook.member;

import android.content.Context;
import android.util.Log;
import com.Doric.CarBook.Constant;
import com.Doric.CarBook.utility.DatabaseHelper;
import com.Doric.CarBook.utility.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserFunctions {

    //����������URL   ��¼�����url,��ؼ���http://��https://
    private static String loginURL = Constant.BASE_URL + "/login.php";
    private static String registerURL = Constant.BASE_URL + "/register.php";
    private static String collectionURL = Constant.BASE_URL + "/user_collect.php";

    //��ǩ
    private static String loginTag = "login";
    private static String registerTag = "register";
    private static String collectionTag = "collect";
    private static String collectionDeleteTag = "delete_collect";
    private static String collectionSyncTag = "collect_sync";
    private static String myCollection = "my_collection";
    private static String defaultCollection = "default_collection";

    DatabaseHelper db;   //����SQLite���ݿ⸨����
    private JSONParser jsonParser;     //json�������ݹ�����

    // constructor
    public UserFunctions(Context context) {
        jsonParser = new JSONParser();
        db = new DatabaseHelper(context);
    }

    /**
     * ���͵�¼����
     *
     * @param username  ��¼���û���
     * @param password  ��¼����
     * @return ���ص�¼�Ƿ�ɹ���json���󣬾����@link json�ļ�����register_login.json
     */
    public JSONObject loginUser(String username, String password) {
        // ���췢�͸��������˵Ĳ���
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", loginTag));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));

        // ��ȡ json ����
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);

        //���û���Ϣ�浽����SQLite���ݿⲢͬ���ղ�
        SyncData(json);
        return json;
    }

    /**
     * ����ע������
     *
     * @param name ע���û���
     * @param email  ע��email
     * @param password  ע������
     * @return ����ע���Ƿ�ɹ���json���󣬾����@link json�ļ�����register_login.json
     */
    public JSONObject registerUser(String name, String email, String password) {
        // ���췢�͸��������˵Ĳ���
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", registerTag));
        params.add(new BasicNameValuePair("username", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        // ��ȡ json ����
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);

        //���û���Ϣ�浽����SQLite���ݿ�
        SyncData(json);
        syncCollection();
        return json;
    }

    /**
     * ���û���Ϣ�浽����SQLite���ݿⲢͬ���ղ�
     * @param json ��������ȡ���û�����json����
     * @return �Ƿ�ɹ�
     */
    public boolean SyncData(JSONObject json) {
        if (json != null) {
            try {
                //���ע����¼�ɹ�
                if (json.getString("success").equals("1")) {
                    //���û���Ϣ�浽user���У�����-1��ʾ���ɹ�
                    long id = db.addUser(json.getString("username"),
                            Integer.parseInt(json.getString("user_id")),
                            json.getString("created_at"));
                    //
                    int updatedRow = db.addUserIdToCollection(Integer.parseInt(json.getString("user_id")));
                    syncCollection();
                    return (id != -1 && updatedRow > 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Function get Login status
     */
    public boolean isUserLoggedIn() {
        int count = db.getUserCount();
        if (count > 0) {
            // user logged in
            return true;
        }
        return false;
    }

    /**
     * Function to logout user
     * Reset Database
     */
    public boolean logoutUser() {
        //db.resetCollection();
        db.resetTables();
        return true;
    }


    public String getUsername() {
        return db.getUserDetails().get(DatabaseHelper.KEY_USER_NAME);
    }

    public int getUserId() {
        return Integer.parseInt(db.getUserDetails().get(DatabaseHelper.KEY_USER_ID));
    }

    //TODO �ղ�
    public boolean addToCollection(int carId) {
        if (isUserLoggedIn()) {
            int userId = getUserId();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", collectionTag));
            params.add(new BasicNameValuePair("user_id", userId + ""));
            params.add(new BasicNameValuePair("car_id", carId + ""));
            // getting JSON Object
            JSONObject json = jsonParser.getJSONFromUrl(collectionURL, params);
            try {
                if (json.getString("success").equals("1") && db.addCollection(userId, carId) > -1) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return (db.addCollection(carId) > -1);
        }
        return false;
    }

    public boolean cancelCollect(int carId) {
        if (isUserLoggedIn()) {
            int userId = getUserId();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", collectionDeleteTag));
            params.add(new BasicNameValuePair("user_id", userId + ""));
            params.add(new BasicNameValuePair("car_id", carId + ""));
            // getting JSON Object
            JSONObject json = jsonParser.getJSONFromUrl(collectionURL, params);
            try {
                if (json.getString("success").equals("1") && db.deleteCollection(userId, carId) > -1) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return (db.deleteCollection(carId) > 0);
        }
        return false;
    }

    //�����ص��ղ�ͬ����������
    public void syncCollection() {
        String collectJsonString = db.getAllCollection();
        if (collectJsonString != null && isUserLoggedIn()) {
            int userId = getUserId();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", collectionSyncTag));
            params.add(new BasicNameValuePair("user_id", userId + ""));
            params.add(new BasicNameValuePair("collect_data", collectJsonString));
            JSONObject jsonObject = jsonParser.getJSONFromUrl(collectionURL, params);
            //TODO ���������ϵ��ղر����ڱ���
            try  {
                if (jsonObject.getString("success").equals(1)) {
                    JSONArray JSONCar = jsonObject.getJSONArray("cars");
                    db.resetCollection();
                    for (int i = 0; i < JSONCar.length(); i++) {
                        int carId = JSONCar.getJSONObject(i).getInt("car_id");
                        String createTime = JSONCar.getJSONObject(i).getString("create_time");
                        db.addCollection(userId, carId, createTime);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject getMyCollection() {
        if (isUserLoggedIn()) {
            return getCollectionFromURL();
        } else {
            String collectJsonString = db.getAllCollection();
            Log.i(defaultCollection, collectJsonString);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", defaultCollection));
            params.add(new BasicNameValuePair("car_id", collectJsonString));
            return jsonParser.getJSONFromUrl(collectionURL, params);
        }
    }

    private JSONObject getCollectionFromURL() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", myCollection));
        params.add(new BasicNameValuePair("user_id", getUserId() + ""));
        return jsonParser.getJSONFromUrl(collectionURL, params);
    }

    public boolean isCollected(int carId) {
        return db.isCarCollected(carId);
    }
}
