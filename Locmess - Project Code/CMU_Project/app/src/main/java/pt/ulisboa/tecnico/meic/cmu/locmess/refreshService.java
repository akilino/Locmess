package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Sheng on 24/04/2017.
 */

public class refreshService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
