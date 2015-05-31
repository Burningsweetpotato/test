package com.example.jinukcha.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {
    private Button btnShowLocation;
    private TextView txtLat;
    private TextView txtLon;

    GoogleMap gmap;

    private LocationManager locationManager;
    Geocoder geoCoder;
    //private Location myLocation = null;
    //float speed = 0;

    // GPSTracker class
    private LocationDemo gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // GPS로 부터 위치정보를 업데이트 요청, 1초마다 5km 이동시
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        // 기지국으로 부터 위치정보를 업데이트 요청
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, (android.location.LocationListener) this);

        // 주소를 확인하기 위한 Geocoder KOREA 와 KOREAN 둘다 가능
        geoCoder = new Geocoder(this, Locale.KOREAN);

        btnShowLocation = (Button) findViewById (R.id.gpsButton);
        txtLat = (TextView) findViewById (R.id.Latitude);
        txtLon = (TextView) findViewById (R.id.Longitude);

        // GPS 정보를 보여주기 위한 이벤트 클래스 등록
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                gps = new LocationDemo(MainActivity.this);
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    StringBuffer sb = getAddress(latitude,longitude);

                    txtLat.setText(String.valueOf(latitude));
                    txtLon.setText(String.valueOf(longitude));

                    setMap(latitude, longitude);
                    addMarker(latitude,longitude,sb);

                    Toast.makeText(
                            getApplicationContext(),
                            "당신의 주소 - \n" + sb,
                            Toast.LENGTH_LONG).show();
                }
                else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
            }
        });
    }



    public void addMarker(double x, double y, StringBuffer sb){
        LatLng loc = new LatLng(x,y);
        MarkerOptions mko = new MarkerOptions().position(loc);
        mko.title("위치정보");
        mko.snippet(sb.toString());
        gmap.addMarker(mko);
    }

    public StringBuffer getAddress(double latitude, double longitude){
        TextView addrText = (TextView) findViewById(R.id.Address);
        StringBuffer addr = new StringBuffer();

        try{
            List<Address> addresses;
            addresses = geoCoder.getFromLocation(latitude,longitude,1);
            for(Address address: addresses){
                int index = address.getMaxAddressLineIndex();
                for(int i = 0; i <= index; i++){
                    addr.append(address.getAddressLine(i));
                    addr.append(" ");
                }
                addr.append("\n");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        addrText.setText(String.valueOf(addr));
        return addr;
    }

    public void setMap(double latitude, double longitude){
        gmap  = ((MapFragment)(getFragmentManager().findFragmentById(R.id.map))).getMap();
        gmap.setMyLocationEnabled(true);
        LatLng startingPoint = new LatLng(latitude, longitude);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 16));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
