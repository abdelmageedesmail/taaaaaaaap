package brotherteam.com.tabletapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MyReceiver extends BroadcastReceiver {
    private String mid;
    private int sid;
    private int counter=0;
    ArrayList<Integer> tmp=new ArrayList<Integer>();
    private Session session;

    public MyReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
           sendRequest(context);
            }
        }, TimeUnit.MINUTES.toMillis(0),TimeUnit.MINUTES.toMillis(1));

    }
/*send request to the server and recive the response
* response will be last ID
* */
    private void sendRequest(final Context context) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://196.218.129.64/getid.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    JSONArray ids = root.getJSONArray("ids");
                    JSONObject current = ids.getJSONObject(0);
                    //last ID
                     mid = current.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Parse last ID to int
                 sid = Integer.parseInt(mid);
                /*counter for indexing with increment
                * create array list take current ID and compare it with
                * the last ID if the current > pervious
                *                 then some one registered
                *              if it was first time
                *                   then currentID = the parsed ID
                * */

                if(counter==0){
                    tmp.add(counter,sid);
                  //  System.out.println("hello "+counter+" tmp:"+tmp.get(counter)+" sid:"+sid);
                    }
                else{
                    tmp.add(counter,sid);
                    if(tmp.get(counter)>tmp.get(counter-1)) {
                        //  System.out.println("do notification");
                        sendMail(context);
                        sendNotification(context,counter);
                        //System.out.println(counter+" tmp:"+tmp.get(counter-1)+" current:"+tmp.get(counter));
                    }
                    else{
                        //System.out.println("no notification");
                        //System.out.println(counter+" tmp:"+tmp.get(counter)+" sid:"+sid);
                    }
                }

                counter++;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("error");
                builder.setMessage(""+error);
                builder.setCancelable(true);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private void sendMail(Context context) {

        Properties prop= new Properties();
        prop.put("mail.smtp.host","smtp.gmail.com");
        prop.put("mail.smtp.socketFactory.port",465);
        prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.auth","true");
        prop.put("mail.smtp.port","465");
        session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("javawy123@gmail.com", "123456789!@#$%^&*(");
            }
        });
        ReciveFeedTask task = new ReciveFeedTask();
        task.execute();

    }
    class ReciveFeedTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MimeMessage message = new MimeMessage(session);
            try {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("kareemhassan851@gmail.com"));
                message.setSubject("Notification");
                message.setContent("new users registered sir","text/html; charset=utf-8;");
                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
             Log.e("mail:","sent successfully ");

        }

    }

    private void sendNotification(Context context, int counter) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle("registration");
        builder.setSmallIcon(R.drawable.icon);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentText("some one has been registered");
        NotificationManager nmager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nmager.notify(counter,builder.build());

    }
}
