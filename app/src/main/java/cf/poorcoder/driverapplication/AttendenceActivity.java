package cf.poorcoder.driverapplication;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cf.poorcoder.driverapplication.Adapters.AttendenceAdapter;
import cf.poorcoder.driverapplication.Adapters.NotificationAdapter;
import cf.poorcoder.driverapplication.Models.Attendence;
import cf.poorcoder.driverapplication.Models.Notification;

public class AttendenceActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbarTitle;

    AttendenceAdapter attendenceAdapter;
    RecyclerView mAttendenceAdapter;
    List<Attendence> attendenceList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        setUpToolBar();


        pd = new ProgressDialog(AttendenceActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setMessage("Please Wait");
        pd.show();

        mAttendenceAdapter = (RecyclerView) findViewById(R.id.attendenceView);
        mAttendenceAdapter.setHasFixedSize(false);
        mAttendenceAdapter.setLayoutManager(new LinearLayoutManager(this));

        attendenceList = new ArrayList<>();

        firebaseUser = mAuth.getCurrentUser();

        db.collection("Drivers").document(firebaseUser.getUid()).collection("Attencence").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Attendence p = doc.toObject(Attendence.class);
                        attendenceList.add(p);
                    }

                    Collections.reverse(attendenceList);

                    attendenceAdapter = new AttendenceAdapter(AttendenceActivity.this, attendenceList);
                    mAttendenceAdapter.setAdapter(attendenceAdapter);
                    //If ProgressDialog is showing Dismiss it
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            }
        });

    }

    void setUpToolBar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/font.ttf");
        toolbarTitle.setTypeface(type);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
