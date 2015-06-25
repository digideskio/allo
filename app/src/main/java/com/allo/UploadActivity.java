package com.allo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.urqa.clientinterface.URQAController;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by baek_uncheon on 2015. 3. 26..
 */
public class UploadActivity extends Activity {

    LinearLayout ll_back;

//    EditText et_file_name;
    EditText et_allo_title;
    EditText et_allo_artist;

    Allo allo;
    ImageView iv_allo_image;


    RadioGroup rg_allo_open;
    RadioButton rb_allo_open;
    RadioButton rb_allo_close;

    Bitmap bm_allo_image;
    Boolean is_allo_open;

    Button btn_allo_register;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private String IMAGE_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/allo/temp.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        is_allo_open = true;

        setLayout();
        setListener();
        setAlloInfo();
    }


    private void setLayout(){
        ll_back = (LinearLayout)findViewById(R.id.ll_back);

//        et_file_name = (EditText)findViewById(R.id.et_file_name);
        et_allo_title = (EditText)findViewById(R.id.et_allo_title);
        et_allo_artist = (EditText)findViewById(R.id.et_allo_artist);

        iv_allo_image = (ImageView)findViewById(R.id.iv_allo_image);

        rg_allo_open = (RadioGroup)findViewById(R.id.rg_allo_open);
        rb_allo_open = (RadioButton)findViewById(R.id.rb_allo_open);
        rb_allo_close = (RadioButton)findViewById(R.id.rb_allo_close);

        btn_allo_register = (Button)findViewById(R.id.btn_allo_register);
    }

    private void setListener(){
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_allo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAlloImage();
            }
        });
        rg_allo_open.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_allo_open:
                        is_allo_open = true;
                        break;
                    case R.id.rb_allo_close:
                        is_allo_open = false;

                }

            }
        });

        btn_allo_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAlloRegister();
            }
        });

    }

    private void setAlloInfo() {
        allo = (Allo) getIntent().getSerializableExtra("allo_temp");

        et_allo_title.setText(allo.getTitle());
        et_allo_artist.setText(allo.getArtist());


        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(allo.getURL());
        byte [] data = mmr.getEmbeddedPicture();
        if(data != null)
        {
            bm_allo_image = BitmapFactory.decodeByteArray(data, 0, data.length);
            iv_allo_image.setImageBitmap(bm_allo_image); //associated cover art in bitmap
        }
    }


    private void clickAlloImage(){
        final CharSequence[] items = {"앨범", "카메라"};
        new AlertDialog.Builder(this)
                .setTitle("이미지 가져오기")
                .setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                switch (i) {
                                    case 0:
                                        getImageFromAlbum();
                                        break;
                                    case 1:
                                        getImageFromCamera();
                                        break;
                                }
                            }
                        })
                .show();
    }

    private void getImageFromAlbum(){
        Intent intent = new Intent();
        // Gallery 호출
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // 잘라내기 셋팅
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 150);
        try {
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent,
                    "Complete action using"), PICK_FROM_GALLERY);
        } catch (ActivityNotFoundException e) {
            // Do nothing for now
        }
    }

    private void getImageFromCamera(){
        // 카메라 호출
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

        // 이미지 잘라내기 위한 크기
        intent.putExtra("crop", "true");
//        intent.putExtra("outputX", 200);
//        intent.putExtra("outputY", 200);
        intent.putExtra("aspectX", 1); //이걸 삭제한다
        intent.putExtra("aspectY", 1); //이걸 삭제한다
        intent.putExtra("scale", true); //이걸 삭제한다
        intent.putExtra("noFaceDetection",true);



        intent.putExtra("aspectX", 9);

        intent.putExtra("aspectY", 16);

        intent.putExtra("scale", true);

        try {
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            // Do nothing for now
        }


        /*
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "temp.jpg";
        mImageCaptureUri = Uri.fromFile(new File(IMAGE_TEMP_PATH, url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        try{
            Bundle extras = data.getExtras();
            if (extras != null){
                bm_allo_image = extras.getParcelable("data");
                iv_allo_image.setImageBitmap(bm_allo_image);

            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

//    public void SaveBitmapToFileCache() {
//
//
//
//
//        File file = new File(IMAGE_FILEPATH);
//        File fileCacheItem = new File(IMAGE_FILEPATH);
//        OutputStream out = null;
//
//        try
//        {
//            fileCacheItem.createNewFile();
//            out = new FileOutputStream(fileCacheItem);
//
//            bm_allo_image.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            try
//            {
//                out.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }


    private void clickAlloRegister() {

        AsyncHttpClient myClient = new AsyncHttpClient();
        myClient.setTimeout(30000);

        String url = getApplicationContext().getString(R.string.url_ucc_upload);

        PersistentCookieStore myCookieStore = new PersistentCookieStore(getApplicationContext());
        myClient.setCookieStore(myCookieStore);

        File file = new File(allo.getURL());

        RequestParams params = new RequestParams();

        String st_title = et_allo_title.getText().toString();
        String st_artist = et_allo_artist.getText().toString();
        if (bm_allo_image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm_allo_image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            params.put("image", new ByteArrayInputStream(stream.toByteArray()), "image.jpeg");
        }

        SingleToneData singleToneData = SingleToneData.getInstance();
        String st_token = singleToneData.getToken();

        params.put("title", st_title);
        params.put("artist", st_artist);
        params.put("is_open", is_allo_open);
        params.put("token", st_token);

        try {
            params.put("song", file, "multipart/form-data");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        myClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("HTTP RESPONSE......", new String(responseBody));
                try {
                    JSONObject response_object = new JSONObject(new String(responseBody));
                    String status = response_object.getString("status");
                    if (status.equals("success")) {
                        Toast.makeText(getApplicationContext(), "업로드가 완료되었습니다. 내 정보에서 확인하세요.", Toast.LENGTH_SHORT).show();
                    } else {

                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(responseBody);

            }
        });
    }



    @Override
    public void onResume(){
        super.onResume();

    }


    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        // This is somewhat like [[NSNotificationCenter defaultCenter] removeObserver:name:object:]
        super.onDestroy();
    }

}
