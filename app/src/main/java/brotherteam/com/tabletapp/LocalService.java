package brotherteam.com.tabletapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocalService extends Service {
    public LocalService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mintent = new Intent(this,MyReceiver.class);
        sendBroadcast(mintent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
