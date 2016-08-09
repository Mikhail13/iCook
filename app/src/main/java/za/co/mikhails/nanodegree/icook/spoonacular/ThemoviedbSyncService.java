package za.co.mikhails.nanodegree.icook.spoonacular;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ThemoviedbSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapterMovies = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapterMovies == null) {
                sSyncAdapterMovies = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapterMovies.getSyncAdapterBinder();
    }
}