package brotherteam.com.tabletapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import brotherteam.com.tabletapp.connection.GPSTracker;
import brotherteam.com.tabletapp.connection.InternetConnection;

public class MainActivity extends AppCompatActivity {
    GPSTracker mGps;
    EditText txtPhone;
    FloatingActionButton btnRegister;
    String phoneNum;
    CoordinatorLayout coordinateLayout;
    Snackbar snackbar;
    Timestamp timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPhone=(EditText) findViewById(R.id.phoneNumber);
        btnRegister=(FloatingActionButton) findViewById(R.id.register);
        coordinateLayout=(CoordinatorLayout) findViewById(R.id.coordinateLayout);
        mGps=new GPSTracker(MainActivity.this);

        timestamp = new Timestamp(System.currentTimeMillis());
        Log.e("timeStamp",timestamp+"");

        // btn Register click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!InternetConnection.isConnectingToInternet(MainActivity.this)){
                    showSnackBar();
                }else {
                    if(validateFields()) {
                        sendData();
                    }
                }
            }
        });

    }

    private void showSnackBar() {

        snackbar = Snackbar.make(coordinateLayout, "لا يوجد إتصال بالإنترنت", Snackbar.LENGTH_INDEFINITE)
                .setAction("إعادة المحاولة", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (InternetConnection.isConnectingToInternet(MainActivity.this)) {
                             sendData();
                        } else {
                            showSnackBar();
                        }
                    }
                });
        snackbar.setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.snackBarActionTxtColor, null));
        snackbar.show();
    }


    /**
     *Method to send Data to Server... Json Code
     * you have class for current jps using it to get latitude and langituide its method in GPSTRACKER CLASS use It
     *
     */

    private void sendData() {
        final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("جارى ارسال البيانات...");
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://196.218.129.64/upload.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response",response);
                progressDialog.dismiss();

                final Dialog dialog=new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_congrates);
                dialog.setCanceledOnTouchOutside(false);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);

                Button done=(Button) dialog.findViewById(R.id.done);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                txtPhone.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorMessage",error.getMessage());
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                String lat=String.valueOf(mGps.getLatitude());
                String lon=String.valueOf(mGps.getLongitude());
                param.put("phone",txtPhone.getText().toString());
                param.put("latitude",lat);
                param.put("longitude",lon);
                param.put("last_update",timestamp+"");
                return param;
            }
        };

        Volley.newRequestQueue(MainActivity.this).add(stringRequest);

    }

    public boolean validateFields() {
        boolean valid = true;
        phoneNum=txtPhone.getText().toString();

        if (phoneNum.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(phoneNum).matches()) {
            txtPhone.setError("تأكد من رقم الهاتف.");
            valid = false;
        } else {
            txtPhone.setError(null);
        }

        return valid;
    }
}


