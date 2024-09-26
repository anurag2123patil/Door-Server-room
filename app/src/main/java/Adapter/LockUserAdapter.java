package Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.server.room.R;

import java.util.List;


public class LockUserAdapter extends RecyclerView.Adapter<LockUserAdapter.MyViewHolder> {

    List<String> userName; //= new ArrayList<>();

    private Context mContext;

    public LockUserAdapter(Context mContext, List<String> userName) {
        this.mContext = mContext;

        this.userName = userName;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lock_log, parent, false);
        return new LockUserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.logDetails.setText("User: " + userName.get(position));
    }

    @Override
    public int getItemCount() {
        return userName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView logDetails;


        public MyViewHolder(View itemView) {
            super(itemView);
            logDetails = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}
