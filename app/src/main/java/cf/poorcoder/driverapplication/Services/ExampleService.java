package cf.poorcoder.driverapplication.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cf.poorcoder.driverapplication.MainActivity;
import cf.poorcoder.driverapplication.R;

import static cf.poorcoder.driverapplication.Services.App.CHANNEL_ID;


public class ExampleService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String input = "Running in Background..";
    String uid;
    String lat = "30.7",lon = "76.0";
    HashMap<String,String> hm;
    String formattedDate;

    int hours,mins;

    public ExampleService(Context applicationContext) {
        super();

    }
    public ExampleService() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        final Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Akal Academy Transporter")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText(input)
                .setContentInfo("Running in Background")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //handler work for location update
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i;
                for (i=0; i<6; i++){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //updating location data on the UI thread
                            Toast.makeText(ExampleService.this, "updated", Toast.LENGTH_SHORT).show();
                            fetchLocation();
                        }
                    }, 0);
                    //Add some downtime
                    SystemClock.sleep(5000);
                }
            }
        };
        new Thread(runnable).start();

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                fetchLocation();
//                //getAlarm();
///*
//                try {
//                    firebaseFirestore.collection("Alarm").document("alarmTime").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            hours = Integer.parseInt(documentSnapshot.get("hour").toString());
//                            mins = Integer.parseInt(documentSnapshot.get("min").toString());
//
//                            Calendar calendar = Calendar.getInstance();
//                            Calendar calendar2 = Calendar.getInstance();
//
//                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
//                                    hours, mins);
//
//                            if (calendar.equals(calendar2)) {
//                                //startActivity(notificationIntent);
//                            }
//                        }
//                    });
//
//                }
//                catch (Exception e)
//                {
//
//                }*/
//                handler.postDelayed(this, 65000);
//            }
//        },65000);

        //do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);

    }

    private void fetchLocation() {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            }
            else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Logic to handle location object
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        HashMap<String,Object> hash = new HashMap<>();
                        Random random = new Random();
                        hash.put("longitude",longitude);
                        hash.put("latitude",latitude);

                        try {
                            firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot snapshot = task.getResult();

                                    lat = snapshot.get("school_latitude").toString();
                                    lon = snapshot.get("school_longitude").toString();
                                }
                            });

                            double dist = distFrom(latitude, longitude, Double.parseDouble(lat), Double.parseDouble(lon));

                            hash.put("distance", dist);

                            Date c = Calendar.getInstance().getTime();
                            System.out.println("Current time => " + c);
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            formattedDate = df.format(c);

                            hm = new HashMap<String, String>();
                            hm.put("date", formattedDate);
                            Calendar cal = Calendar.getInstance();
                            Calendar start = Calendar.getInstance();
                            start.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                                    6, 30);
                            Calendar end = Calendar.getInstance();

                            end.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                                    8, 30);

                            Calendar start1 = Calendar.getInstance();
                            start1.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                                    14, 30);
                            Calendar end1 = Calendar.getInstance();

                            end1.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                                    17, 0);
                            //CHANGE NEW

                            if (cal.before(end) && cal.after(start)) {
                                firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).update(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                                if(dist < 1000)
                                {
                                    hm.put("attendence", "Present");
                                }
                            }

                            if (cal.before(end1) && cal.after(start1)) {
                                firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).update((Map) hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                                if(dist < 1000)
                                {
                                    hm.put("attendence", "Present");
                                }
                            }

                            uid = firebaseUser.getUid();

                            if (hm.containsKey("attendence")) {
                                firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).collection("Attencence").document(formattedDate).update((Map) hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                            }

                            firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).collection("Attencence").document(formattedDate).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (!task.getResult().exists()) {
                                        firebaseFirestore.collection("Drivers").document(uid).collection("Attencence").document(formattedDate).set(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                    }
                                }
                            });

                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
            });

        }

    }

    private void getAlarm()
    {
        firebaseFirestore.collection("Drivers").document(firebaseUser.getUid()).collection("alarm").document("alarmTime").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                hours = Integer.parseInt(documentSnapshot.get("hour").toString());
                mins = Integer.parseInt(documentSnapshot.get("min").toString());

                Calendar calendar = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();

                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        hours, mins);

                if(calendar.before(calendar2))
                {
                    //setAlarm(calendar.getTimeInMillis());
                }
            }
        });
    }


    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, MyAlarm.class);
        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_NO_CREATE);

        Calendar now = Calendar.getInstance();
        long oldtimer = now.getTimeInMillis();
        if(time < oldtimer) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}