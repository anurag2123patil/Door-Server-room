package Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.project.server.room.R;

import java.util.List;


public class LockLogAdapter extends RecyclerView.Adapter<LockLogAdapter.MyViewHolder> {

    String lockName;
    List<String> userName; //= new ArrayList<>();
    List<String> timeStamp; //= new ArrayList<>();

    private Context mContext;

    public LockLogAdapter(Context mContext, List<String> userName, List<String> timeStamp, String lockName) {
        this.mContext = mContext;
        this.timeStamp = timeStamp;
        this.userName = userName;

        this.lockName = lockName;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lock_log, parent, false);
        return new LockLogAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.logDetails.setText(userName.get(position) + " unlocked: " + lockName);
        holder.logTime.setText(timeStamp.get(position));
    }

    @Override
    public int getItemCount() {
        return userName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView logDetails, logTime;


        public MyViewHolder(View itemView) {
            super(itemView);
            logDetails = (TextView) itemView.findViewById(R.id.tvName);
            logTime = (TextView) itemView.findViewById(R.id.tvTime);

        }
    }
}
