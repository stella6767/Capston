package com.example.capston;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    Context context = this;
    final String TAG = "LoadingActivity";
    static int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Shelter> shelters = xml_parse();
                ArrayList<Location> shelter_address = new ArrayList<Location>();
             /*   for(int i = 0 ; i < shelters.size(); i++) {
                    Log.d(TAG, "convert");
                    //shelter_address.add(addrToPoint(context, shelters.get(i).getAddress()));
                    shelter_address.add(shelters.get(i).getX());
                } // 대피소 주소만 위도경보로 변환하여 모아놓음*/
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                intent.putExtra("shelter", shelters);
                intent.putExtra("shelter_addr", shelter_address);
                startActivity(intent);
            }
        }).start();

       /* ArrayList<Shelter> shelters = xml_parse(); //shelter
        Log.d(TAG, String.valueOf(shelters.size()));
        Log.d(TAG, "되냐?");

        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.putExtra("shelter", shelters);  //넘김
        startActivity(intent);*/
    }
        private ArrayList<Shelter> xml_parse() {
        ArrayList<Shelter> shelterList = new ArrayList<Shelter>();
        InputStream inputStream = getResources().openRawResource(R.raw.earthquakee);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        XmlPullParserFactory xmlPullParserFactory = null;
        XmlPullParser xmlPullParser = null;

        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(reader);

            Shelter shelter = null;
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){ //원하는 태그만 파싱
                    case XmlPullParser.START_DOCUMENT:
                        Log.i(TAG, "xml START");
                        break;
                    case XmlPullParser.START_TAG:
                        String startTag = xmlPullParser.getName();
                        Log.i(TAG, "Start TAG :" + startTag);
                        if(startTag.equals("row")) {
                            shelter = new Shelter();
                            Log.d(TAG, "Shelter 추가");
                        }
                        else if(startTag.equals("vt_acmdfclty_nm")) {
                            shelter.setName(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("dtl_adres")) {
                            shelter.setAddress(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("rdnmadr_cd")) { //도로명 주소로 구분
                            shelter.setNumber(this.count);
                            Log.d(TAG, String.valueOf(shelter.getNumber()));
                            this.count ++; //배열 인덱스 겸 객체 찾기라고 생각하셈
                            Log.d(TAG, "shelter 번호");
                        }
                        else if(startTag.equals("xcord")) { //double 형으로 안 받아지는디...
                            shelter.setX(Double.parseDouble(xmlPullParser.nextText()));
                            Log.d("x 값", String.valueOf(shelter.x));
                        }
                        else if(startTag.equals("ycord")) {
                            shelter.setY(Double.parseDouble(xmlPullParser.nextText()));
                            Log.d("y 값", String.valueOf(shelter.y));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = xmlPullParser.getName();
                        Log.i(TAG,"End TAG : "+ endTag);
                        if (endTag.equals("row")) {
                            shelterList.add(shelter); //대피소 추가
                        }
                        break;
                }
                try {
                    eventType = xmlPullParser.next();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(reader !=null) reader.close();
                if(inputStreamReader !=null) inputStreamReader.close();
                if(inputStream !=null) inputStream.close();
            }catch(Exception e2){
                e2.printStackTrace();
            }
        }
        Log.d("배열 사이즈", String.valueOf(shelterList.size())); //50개 리스트
        return shelterList;
    }



    /*public static Location addrToPoint2(Context context, String addr) {
        Location location = new Location("");


        if(location != null) {
            for(int i = 0 ; i < shelters.size() ; i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
            }
        }
        return location;
    }
*/


    public static Location addrToPoint(Context context, String addr) { // 주소명으로 위도 경도를 구하는 메소드
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(addr,3);
            Log.e("test", "위도경도 변환");
        } catch (IOException e) {
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            e.printStackTrace();
        }
        if(addresses != null) {
            for(int i = 0 ; i < addresses.size() ; i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
            }
        }
        return location;
    }
}