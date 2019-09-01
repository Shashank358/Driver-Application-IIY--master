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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cf.poorcoder.driverapplication.Adapters.StudentAdapter;
import cf.poorcoder.driverapplication.Models.Student;

public class AbsentStudentActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbarTitle;
    StudentAdapter StudentAdapter;
    RecyclerView mStudentList;
    List<Student> StudentList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent_student);
        setUpToolBar();pd = new ProgressDialog(AbsentStudentActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setMessage("Please Wait");
        pd.show();

        mStudentList = (RecyclerView) findViewById(R.id.student_list);
        mStudentList.setHasFixedSize(false);
        mStudentList.setLayoutManager(new LinearLayoutManager(this));

        StudentList = new ArrayList<>();

        firebaseUser = mAuth.getCurrentUser();

        db.collection("Holiday").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Student p = doc.toObject(Student.class);

                        Date date = new Date();
                        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date dateFrom = format.parse(p.getFrom());
                            Date dateTo = format.parse(p.getTo());

                            if(dateTo.equals(dateFrom) && String.valueOf(date.getDate()).equals(String.valueOf(dateFrom.getDate()))) {
                                //Toast.makeText(AbsentStudentActivity.this, "SAME", Toast.LENGTH_LONG).show();
                                StudentList.add(p);
                            }
                            if (date.after(dateFrom) && date.before(dateTo) && p.getDriver_id().equals(firebaseUser.getUid())) {
                                StudentList.add(p);
                                }
                        }
                        catch (Exception e)
                        {

                        }
                    }

                    StudentAdapter = new StudentAdapter(AbsentStudentActivity.this, StudentList);
                    mStudentList.setAdapter(StudentAdapter);
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
