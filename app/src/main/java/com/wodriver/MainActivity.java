package com.wodriver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.internal.Constants;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;
import android.content.ServiceConnection;
import android.content.ComponentName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import android.location.Address;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wodriver.AWS.userpool.SignUpActivity;
import com.wodriver.util.CustomList;
import com.wodriver.util.DBManager;
import com.wodriver.util.listAdapter;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback{

//    AmazonDynamoDBClient ddbClient;
//    DynamoDBMapper mapper;

    public static Activity activity;
    private ConsumerService mConsumerService = null;

    private static final String TAG = "@@@";
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_CODE_LOCATION = 2000;//임의의 정수로 정의
    private static final int REQUEST_CODE_GPS = 2001;//임의의 정수로 정의
    private GoogleMap googleMap;
    private GoogleMap warningMap;
    LocationManager locationManager;
    MapFragment mapFragment;
    boolean setGPS = false;
    LatLng SEOUL = new LatLng(37.56, 126.97);
    double warning_lat;
    double warning_lon;

    double warning_lat1;
    double warning_lon1;

    String url = "https://taas.koroad.or.kr/data/rest/frequentzone/child?authKey=";
    String key = "JLhKMQ2leYopB9RaJJMa42RHuRxcw8qtFcvmKAKTPQii4x%2FnA9J3sisLuag4w7xF";
    String year = "&searchYearCd=2016048";
    String sido = "&sido=41";
    String gugun = "&gugun=113";
    String death = "&DEATH=N";

//    String request = url + key + year + sido + gugun + death;

    String request = "http://taas.koroad.or.kr/data/rest/frequentzone/child?authKey=aVH2smnlTTTiclsProLkHcRpia9IJJwblHNt%2Fd%2FZVeql7dQoHlkKnPnVFzhVhksi&searchYearCd=2015049&sido=11&gugun=680&DEATH=N";

    private String JSONdata = null;

    /**
     *  Class name for log messages. */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** The identity manager used to keep track of the current user account. */
    private IdentityManager identityManager;

    /** Our navigation drawer class for handling navigation drawer logic. */
    private DrawerLayout drawerLayout;

    private ListView listView;

    public boolean isUserSignedIn;
    private boolean mIsBound = false;

    private LocationManager mLocMgr;

    /** Buttons in drawerlayout */
    private Button signUpButton;
    private Button signInButton;
    private Button ConnectionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = MainActivity.this;

        AWSMobileClient.initializeMobileClientIfNecessary(this);
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
        identityManager = awsMobileClient.getIdentityManager();

        setContentView(R.layout.activity_main);

        signInButton = (Button) findViewById(R.id.button_signin);
        signInButton.setOnClickListener(this);

        signUpButton = (Button) findViewById(R.id.button_signup);
        signUpButton.setOnClickListener(this);

        ConnectionButton = (Button) findViewById(R.id.button_connect);
        ConnectionButton.setOnClickListener(this);

        mIsBound = bindService(new Intent(MainActivity.this, ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        ArrayList<CustomList> list = new ArrayList

        new Thread(){
            @Override
            public void run() {
                super.run();
                JSONdata = readJSON();
                Log.d("data", JSONdata);

            }
        }.start();

        try{
            DBManager dbManager = new DBManager(getApplicationContext(), "WoDriver.db", null, 1);
            dbManager.insert("insert into HR_DATA values(null, '0', '0', '0', '0');");
            dbManager.delete("DELETE FROM HR_DATA");
        }catch (Exception e){

        }



    }



    public String readJSON(){
        StringBuilder JSONdata1 = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        byte[] buffer = new byte[1024];

        try{
            HttpGet httpGetRequest = new HttpGet(request);
            HttpResponse httpResponse = httpClient.execute(httpGetRequest);

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                Log.d("data", "200");
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    InputStream inputStream = entity.getContent();
                    try{
                        int bytesRead = 0;
                        BufferedInputStream bis = new BufferedInputStream(inputStream);

                        while((bytesRead = bis.read(buffer)) != -1){
                            String line = new String(buffer, 0, bytesRead);

                            JSONdata1.append(line);
                        }
                    }catch (Exception e){
                        Log.e("logcat", Log.getStackTraceString(e));
                    }finally {
                        try{
                            inputStream.close();
                        }catch (Exception ignore){

                        }
                    }
                }
            }
        }catch (Exception e){

        }finally {
            httpClient.getConnectionManager().shutdown();
            return JSONdata1.toString();
        }
    }

    public boolean checkLocationPermission()
    {
        Log.d( TAG, "checkLocationPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //퍼미션 요청을 위해 UI를 보여줘야 하는지 검사
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    //Prompt the user once explanation has been shown;
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);

                } else
                    //UI보여줄 필요 없이 요청
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);

                return false;
            } else {

                Log.d( TAG, "checkLocationPermission"+"이미 퍼미션 획득한 경우");

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)
                {
                    Log.d(TAG, "checkLocationPermission Version >= M");
                    showGPSDisabledAlertToUser();
                }

                if (mGoogleApiClient == null) {
                    Log.d( TAG, "checkLocationPermission "+"mGoogleApiClient==NULL");
                    buildGoogleApiClient();
                }
                else  Log.d( TAG, "checkLocationPermission "+"mGoogleApiClient!=NULL");

                if ( mGoogleApiClient.isConnected() ) Log.d( TAG, "checkLocationPermission"+"mGoogleApiClient 연결되 있음");
                else Log.d( TAG, "checkLocationPermission"+"mGoogleApiClient 끊어져 있음");


                mGoogleApiClient.reconnect();//이미 연결되 있는 경우이므로 다시 연결

                googleMap.setMyLocationEnabled(true);
            }
        }
        else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)
            {
                Log.d(TAG, "checkLocationPermission Version < M");
                showGPSDisabledAlertToUser();
            }

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            googleMap.setMyLocationEnabled(true);
        }

        return true;
    }

    public void onStatusChanged(String provider, int val, Bundle bundle){

    }

    public void onProviderEnabled(String provider){

    }

    public void onProviderDisabled(String provider){

    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        googleMap = map;

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        warningMap = map;


        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                Log.d( TAG, "onMapLoaded" );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    checkLocationPermission();
                }
                else
                {

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)
                    {
                        Log.d(TAG, "onMapLoaded");
                        showGPSDisabledAlertToUser();
                    }

                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient();
                    }
                    googleMap.setMyLocationEnabled(true);
                }

            }
        });

        //구글 플레이 서비스 초기화
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();

                googleMap.setMyLocationEnabled(true);
            }
            else
            {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }
        }
        else
        {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
    }

    //성공적으로 GoogleApiClient 객체 연결되었을 때 실행
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d( TAG, "onConnected" );

        if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            setGPS = true;

        mLocationRequest = new LocationRequest();
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);


        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Log.d( TAG, "onConnected " + "getLocationAvailability mGoogleApiClient.isConnected()="+mGoogleApiClient.isConnected() );
            if ( !mGoogleApiClient.isConnected()  ) mGoogleApiClient.connect();


            // LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);

            if ( setGPS && mGoogleApiClient.isConnected() )//|| locationAvailability.isLocationAvailable() )
            {
                Log.d( TAG, "onConnected " + "requestLocationUpdates" );
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if ( location == null ) return;

                //현재 위치에 마커 생성
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("현재위치");
                googleMap.addMarker(markerOptions);

                //지도 상에서 보여주는 영역 이동
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            }

        }

    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //구글 플레이 서비스 연결이 해제되었을 때, 재연결 시도
        Log.d(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        Log.d( TAG, "OnDestroy");

        // Clean up connections
        if (mIsBound == true && mConsumerService != null) {
            if (mConsumerService.closeConnection() == false) {
//                updateTextView("Disconnected");
//                mMessageAdapter.clear();
            }
        }
        // Un-bind service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }


        super.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location) {

        String errorMessage = "";
        int sig = 0;

        googleMap.clear();

        //현재 위치에 마커 생성
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        markerOptions.title("현재위치");
        googleMap.addMarker(markerOptions);

        //지도 상에서 보여주는 영역 이동
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.getUiSettings().setCompassEnabled(true);

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "인터넷 연결 불가";
            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "잘못된 GPS 좌표";
            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "주소 미발견";
                Log.e(TAG, errorMessage);
            }
            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();
        } else {
            Address address = addresses.get(0);

            LatLng warning_area = new LatLng(37.284331, 127.044453);
            setDangerArea(warning_area);
            warning_area = new LatLng(37.286552, 127.045823);
            setDangerArea(warning_area);

            warning_lat1 = 37.284331;
            warning_lon1 = 127.044453;

            warning_lat = 37.286552;
            warning_lon = 127.045823;

//            Toast.makeText( this, "Warning Area", Toast.LENGTH_LONG).show();
            mConsumerService.sendData("vibration");

            if((location.getLatitude() > warning_lat-0.0004 && location.getLatitude() < warning_lat + 0.0004) && (location.getLongitude() > warning_lon - 0.0004 && location.getLongitude() < warning_lon + 0.0004)){
//                Toast.makeText( this, "Warning Area", Toast.LENGTH_LONG).show();
//                if(sig == 0) {
            //    try{
                    mConsumerService.sendData("vibration");
               //     Thread.sleep(2000);
           //     }catch (Exception e){

           //     }

//                    sig = 1;
//                }

            }

            if((location.getLatitude() > warning_lat1-0.0004 && location.getLatitude() < warning_lat1 + 0.0004) && (location.getLongitude() > warning_lon1 - 0.0004 && location.getLongitude() < warning_lon1 + 0.0004)){
//                Toast.makeText( this, "Warning Area", Toast.LENGTH_LONG).show();
                mConsumerService.sendData("vibration");
            }
        }
    }

    public void setDangerArea(LatLng warning_area){
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(warning_area);
        markerOptions1.title("warning_area");
        warningMap.addMarker(markerOptions1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //퍼미션이 허가된 경우
                    if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
                    {

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)
                        {
                            Log.d(TAG, "onRequestPermissionsResult");
                            showGPSDisabledAlertToUser();
                        }


                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "퍼미션 취소", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //GPS 활성화를 위한 다이얼로그 보여주기
    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS가 비활성화 되어있습니다. 활성화 할까요?")
                .setCancelable(false)
                .setPositiveButton("설정", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(callGPSSettingIntent, REQUEST_CODE_GPS);
                    }
                });

        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //GPS 활성화를 위한 다이얼로그의 결과 처리
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        switch (requestCode) {
            case  REQUEST_CODE_GPS:
                //Log.d(TAG,""+resultCode);
                //if (resultCode == RESULT_OK)
                //사용자가 GPS 활성 시켰는지 검사
                if ( locationManager == null)
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    // GPS 가 ON으로 변경되었을 때의 처리.
                    setGPS = true;

                    mapFragment.getMapAsync(MainActivity.this);
                }
                break;
        }
    }

   public boolean onOptionsItemSelected(MenuItem item){
       return true;
   }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), "hey", Toast.LENGTH_LONG).show();
        }
    };

    public void onClick(final View view){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(view == signUpButton){
            // Start the sign-in activity. Do not finish this activity to allow the user to navigate back.
            startActivity(new Intent(this, SignUpActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            // Close the navigation drawer.
            drawerLayout.closeDrawers();
            return;
        }
        if(view == signInButton){
            // Start the sign-in activity. Do not finish this activity to allow the user to navigate back.
            startActivity(new Intent(this, LogIn.class));

//            // Close the navigation drawer.
            drawerLayout.closeDrawers();
//            finish();
            return;
        }
        if(view == ConnectionButton){
            if (mIsBound == true && mConsumerService != null) {
                Log.d("gear", "good");
                mConsumerService.findPeers();
            }
        }

    }
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mConsumerService = ((ConsumerService.LocalBinder) service).getService();
//            updateTextView("onServiceConnected");
            Log.d("Seccess", "connect");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mConsumerService = null;
            mIsBound = false;
            Log.d("fail", "disconnect");
//            updateTextView("onServiceDisconnected");
        }
    };
    protected void onResume(){
        super.onResume();


        // Obtain a reference to the mobile client
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // pause/resume Mobile Analytics collection
        awsMobileClient.handleOnResume();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    protected void onPause(){
        if ( mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onPause();

        // Obtain a reference to the mobile client
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // pause/resume Mobile Analytics collection
        awsMobileClient.handleOnPause();

    }
}
