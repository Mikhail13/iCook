package za.co.mikhails.nanodegree.icook.spoonacular;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ICookDbAuthenticatorService extends Service {

    private ICookDbAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new ICookDbAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
