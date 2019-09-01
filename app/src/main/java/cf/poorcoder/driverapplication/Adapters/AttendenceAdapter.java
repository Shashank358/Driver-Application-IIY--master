package cf.poorcoder.driverapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cf.poorcoder.driverapplication.Models.Attendence;
import cf.poorcoder.driverapplication.R;

public class AttendenceAdapter extends RecyclerView.Adapter<AttendenceAdapter.AttendenceViewHolder>{

    Context mCtx;
    List<Attendence> attendenceList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String,String> hashMap;
    int ProductPrice;
    public AttendenceAdapter(Context mCtx, List<Attendence> attendenceList)
    {
        this.mCtx = mCtx;
        this.attendenceList = attendenceList;
    }
    @NonNull
    @Override
    public AttendenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.single_attendence,
                parent, false);
        AttendenceViewHolder attendenceViewHolder = new AttendenceViewHolder(view);
        return attendenceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AttendenceViewHolder holder, final int position) {
        final Attendence Attendence = attendenceList.get(position);




        holder.date.setText(Attendence.getDate());

        try{
            holder.status.setText(Attendence.getAttendence());
            holder.status.setTextColor(Color.GREEN);
        }

        catch (Exception e)
        {
            holder.status.setText("Absent");
            holder.status.setTextColor(Color.RED);
        }

        if(!holder.status.getText().toString().equals("Present"))
        {
            holder.status.setText("Absent");
            holder.status.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return attendenceList.size();
    }

    class AttendenceViewHolder extends RecyclerView.ViewHolder
    {

        TextView date,status;
        public AttendenceViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date);
            status = (TextView) itemView.findViewById(R.id.status);
            //ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBarMain);
        }
    }
}
