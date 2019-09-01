package cf.poorcoder.driverapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cf.poorcoder.driverapplication.Models.Student;
import cf.poorcoder.driverapplication.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder>{

    Context mCtx;
    List<Student> StudentList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String,String> hashMap;
    int ProductPrice;
    public StudentAdapter(Context mCtx, List<Student> StudentList)
    {
        this.mCtx = mCtx;
        this.StudentList = StudentList;
    }
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.single_student,
                parent, false);
        StudentViewHolder StudentViewHolder = new StudentViewHolder(view);
        return StudentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentViewHolder holder, final int position) {
        final Student Student = StudentList.get(position);

        holder.name.setText(Student.getName());

        holder.status.setText("Absent");

        //if(Student.getImage()!="default")
         //   Picasso.get().load(Student.getImage()).into(holder.imageV);
    }

    @Override
    public int getItemCount() {
        return StudentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder
    {

        TextView name,status;
        CircleImageView imageV;
        public StudentViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            status = (TextView) itemView.findViewById(R.id.status);
            imageV = (CircleImageView) itemView.findViewById(R.id.image);
            //ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBarMain);
        }
    }
}
