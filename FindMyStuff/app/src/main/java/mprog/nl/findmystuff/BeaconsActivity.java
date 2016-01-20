package mprog.nl.findmystuff;

/**
 * Created by Jochem on 19-1-2016.
 */
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.repackaged.android_21.ScanRecord;

import java.util.List;
import java.util.UUID;
import android.support.multidex.MultiDexApplication;
import android.support.multidex.MultiDex;
import android.util.Log;

public class BeaconsActivity extends Application {

    private BeaconManager beaconManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



    @Override
    public void onCreate() {
        super.onCreate();


        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.d("beacons", "beacon entered range");
                Log.d("beacons", String.valueOf(region.describeContents()));

            }
            @Override
            public void onExitedRegion(Region region) {
                Log.d("beacons", "beacon exited range");
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d("beacons", "service is ready");
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        56515, 45028));
            }
        });
    }

}