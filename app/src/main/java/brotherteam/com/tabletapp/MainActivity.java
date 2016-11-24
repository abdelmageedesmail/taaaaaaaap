package brotherteam.com.tabletapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import brotherteam.com.tabletapp.Adapter.PlaceArrayAdapter;
import brotherteam.com.tabletapp.connection.GPSTracker;
import brotherteam.com.tabletapp.connection.InternetConnection;

public class MainActivity extends AppCompatActivity {
    GPSTracker mGps;
    EditText txtPhone;
    FloatingActionButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPhone=(EditText) findViewById(R.id.phoneNumber);
        btnRegister=(FloatingActionButton) findViewById(R.id.register);

        mGps=new GPSTracker(MainActivity.this);

        // btn Register click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!InternetConnection.isConnectingToInternet(MainActivity.this)){
                    Toast.makeText(MainActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                }else {
                    sendData();
                }
            }
        });

    }

    /**
     *Method to send Data to Server... Json Code
     * you have class for current jps using it to get latitude and langituide its method in GPSTRACKER CLASS use It
     *
     */

    private void sendData() {


    }


//    StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://196.218.129.64/upload.php", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> param=new HashMap<>();
//                String lat=String.valueOf(mGps.getLatitude());
//                String lon=String.valueOf(mGps.getLongitude());
//                param.put();
//                param.put("phone",txtPhone.getText().toString());
//                param.put("latitude",lat);
//                param.put("longit")
//                return super.getParams();
//            }
//        };
//
//        Volley.newRequestQueue(MainActivity.this).add(stringRequest);


}

