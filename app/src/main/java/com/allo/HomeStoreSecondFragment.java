package com.allo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by baek_uncheon on 2015. 3. 25..
 */
public class HomeStoreSecondFragment extends Fragment {
    Context context;
    ListView lv_scond;
    HomeFragment homeFragment;
    ArrayList<Allo> ar_allo;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_store_second, container, false);
        setLayout(view);
        setListener();
        getStoreFirstList();

        return view;
    }

    private void setLayout(View view){
        lv_scond = (ListView) view.findViewById(R.id.lv_second);
    }

    private void setListener(){
        lv_scond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RingbackTone ringbackTone = RingbackTone.getInstance();
                ringbackTone.setCurrentAllo(ar_allo.get(position));
                homeFragment.playRingbackTone();
            }
        });
    }
    public void getStoreFirstList() {
        AsyncHttpClient myClient;

        myClient = new AsyncHttpClient();
        myClient.setTimeout(30000);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        myClient.setCookieStore(myCookieStore);

        String url = context.getString(R.string.url_store_charged_new);
        RequestParams params = new RequestParams();
        final SingleToneData singleToneData = SingleToneData.getInstance();
        params.put("token", singleToneData.getToken());

        myClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("HTTP RESPONSE......getAlloList", new String(responseBody));
                getStoreFirstListFinish(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getStoreFirstListFinish(String st_result){
        ar_allo = new ArrayList<>();

        try{
            JSONObject jo_result = new JSONObject(st_result);
            JSONObject jo_response = jo_result.getJSONObject("response");
            JSONArray ja_allo_list = jo_response.getJSONArray("allo_list");
            String status = jo_result.getString("status");
            if (status.equals("success")){

                for (int i = 0; i < ja_allo_list.length(); i++) {
                    JSONObject jo_allo = ja_allo_list.getJSONObject(i);

                    Allo allo = new Allo();
                    allo.setTitle(jo_allo.getString("title"));
                    allo.setArtist(jo_allo.getString("artist"));
                    allo.setURL(jo_allo.getString("url"));
                    if (jo_allo.has("thumbs"))
                        allo.setThumbs(jo_allo.getString("thumbs"));
                    if (jo_allo.has("image"))
                        allo.setImage(jo_allo.getString("image"));
                    if (jo_allo.has("uid"))
                        allo.setId(jo_allo.getString("uid"));
//                    allo.setRank(jo_allo.getString("rank"));
                    ar_allo.add(allo);
                }

            } else if (status.equals("fail")) {
                String st_error = jo_response.getString("error");
                if (st_error.equals("not login")) {
                    LoginUtils loginUtils = new LoginUtils(context);
                    loginUtils.onLoginRequired();
                }
            }

        }catch (Exception e){
            System.out.println(e);
        }

        HomeStoreFirstAdapter adapter = new HomeStoreFirstAdapter(context, R.layout.layout_store_item, ar_allo);
        lv_scond.setAdapter(adapter);


    }
}

