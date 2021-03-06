package brotherteam.com.tabletapp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import brotherteam.com.tabletapp.Adapter.PlaceArrayAdapter;
import brotherteam.com.tabletapp.Fonts.UtilitiesClass;
import brotherteam.com.tabletapp.connection.GPSTracker;
import brotherteam.com.tabletapp.connection.InternetConnection;

public class MainActivity extends AppCompatActivity {




    GPSTracker mGps;
    EditText txtPhone,firstName,lastName,email,phone,workStyle,note,workPlace;
    Button btnRegister;
    String phoneNum;
    CoordinatorLayout coordinateLayout;
    Snackbar snackbar;
    Timestamp timestamp;
    private Session session;
    TextView txtTitle;
////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPhone=(EditText) findViewById(R.id.phoneNumber);
        btnRegister=(Button) findViewById(R.id.register);
        coordinateLayout=(CoordinatorLayout) findViewById(R.id.coordinateLayout);
        mGps=new GPSTracker(MainActivity.this);
        workPlace=(EditText) findViewById(R.id.workplace);
        firstName= (EditText) findViewById(R.id.firsName);
        lastName=(EditText) findViewById(R.id.lastName);
        email=(EditText) findViewById(R.id.email);
        phone=(EditText) findViewById(R.id.phone);
        workStyle = (EditText) findViewById(R.id.workStyle);
        note= (EditText) findViewById(R.id.note);
        setUpFonts();

        //timestamp = new Timestamp(System.currentTimeMillis());
        //Log.e("timeStamp",timestamp+"");
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

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(){
            @Override
            public synchronized void start() {
                startService(new Intent(MainActivity.this,LocalService.class));
            }
        }.start();
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


    public void setUpFonts(){
        UtilitiesClass.setFont(txtPhone,MainActivity.this,1);
        UtilitiesClass.setFont(btnRegister,MainActivity.this,0);
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

                sendMAil();
                txtPhone.setText("");
                phone.setText("");
                workPlace.setText("");
                workStyle.setText("");
                note.setText("");
                firstName.setText("");
                lastName.setText("");
                email.setText("");
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
                param.put("name",firstName.getText().toString()+""+lastName.getText().toString());
                param.put("work",workStyle.getText().toString());
                param.put("email",email.getText().toString());
                param.put("details",note.getText().toString());
                return param;
            }
        };

        Volley.newRequestQueue(MainActivity.this).add(stringRequest);

    }

    public boolean validateFields() {
        boolean valid = true;
        phoneNum=txtPhone.getText().toString();


        if (phoneNum.isEmpty() || Pattern.compile("01[012]\\d{8}").matcher(phoneNum).matches()) {
            txtPhone.setError("تأكد من رقم الهاتف.");
            valid = false;
        } else {
            txtPhone.setError(null);
        }

        if(lastName.getText().toString().isEmpty()){
            lastName.setError("هذا الحقل مطلوب");
            valid=false;
        }else{
            lastName.setError(null);
        }
        if (email.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("أدخل بريد إلكتروني صحيح");
            valid = false;
        } else {
            email.setError(null);
        }

        if (phone.getText().toString().isEmpty() || Pattern.compile("01[012]\\d{8}").matcher(phone.getText().toString()).matches()) {
            phone.setError("تأكد من رقم الهاتف.");
            valid = false;
        } else {
            phone.setError(null);
        }
        if(workPlace.getText().toString().isEmpty()){
            workPlace.setError("هذا الحقل مطلوب");
            valid=false;
        }else{
            workPlace.setError(null);
        }
        if(workStyle.getText().toString().isEmpty()){
            workStyle.setError("هذا الحقل مطلوب");
            valid=false;
        }else{
            workStyle.setError(null);
        }

        if(note.getText().toString().isEmpty()){
            note.setError("هذا الحقل مطلوب");
            valid=false;
        }else{
            note.setError(null);
        }
        return valid;
    }
    public void sendMAil(){

       /* Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"kareemhassan851@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "notification");
        intent.putExtra(Intent.EXTRA_TEXT,new String[]{"hi sir you have new notification"});
        startActivity(Intent.createChooser(intent,"sending mail ...."));
*/
        Properties prop= new Properties();
        prop.put("mail.stmp.host","stmp.gmail.com");
        prop.put("mail.stmp.socketFactory.port",465);
        prop.put("mail.stmp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        prop.put("mail.stmp.auth","true");
        prop.put("mail.stmp.port","465");
         session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("abdelmageedalghor44@gmail.com", "123456789!@#$%^&*(");
            }
        });

        ReciveFeedTask task = new ReciveFeedTask();
        task.execute();

    }


    class ReciveFeedTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            MimeMessage message = new MimeMessage(session);
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("abdelmageedalghor44@gmail.com"));
                message.setSubject("notification");
                message.setContent("new users registered sir","text/html; charset=utf-8;");
                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "تم ارسال الايميل", Toast.LENGTH_SHORT).show();
        }
    }
}


