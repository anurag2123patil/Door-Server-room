package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.core.content.IntentCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.server.room.R;
import com.project.server.room.Home;
import com.project.server.room.LockOperate;

import java.util.ArrayList;
import java.util.List;

//This class has been created for providing adapter to the recyclerView,
//RecyclerView contains all the lock added to user.
public class LockListAdapter extends RecyclerSwipeAdapter<LockListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> arrayName;        //Device name
    private ArrayList<String> arrayLocation;   //Device Location
    private int ItemCount;                    //Number of item to be displayed on list
    private List lockNumber;                     //Device lockNumber

    public LockListAdapter(Context mContext, ArrayList<String> arrayName,
                           ArrayList<String> arrayLocation, int length, List lockNumber) {
        this.mContext = mContext;
        this.ItemCount = length;
        this.arrayName = arrayName;
        this.arrayLocation = arrayLocation;
        this.lockNumber = lockNumber;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.swipe_lock_list_item, parent, false);
        return new MyViewHolder(view);
    }


    //This method will handle all the action to be performed on list
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        //Here we set device name and device location on the list
        viewHolder.cDeviceName.setText(arrayName.get(position));
        viewHolder.cDeviceLocation.setText(arrayLocation.get(position));
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.setRightSwipeEnabled(false);

        // Handling different events when swiping
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                //Toast.makeText(mContext, "onStartOpen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                //Toast.makeText(mContext, "onStartClose", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });


        viewHolder.swipeLayout.getSurfaceView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(viewHolder.cDeviceName.getText().toString())
                        .setMessage("Do you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Home) mContext).onDeleteOption(lockNumber.get(position).toString());
                                arrayName.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, arrayName.size());
                                ItemCount--;
                                ((Home) mContext).fetchLock();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });

        //This will handle onClick action on list
        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                try {
                    String lockid = (String) lockNumber.get(position);

                    if (lockid != null) {
                        Intent i = new Intent(mContext, LockOperate.class);
                        i.putExtra("lockNumber", lockid);
                        i.putExtra("lockName", viewHolder.cDeviceName.getText().toString());
                        i.putExtra("lockAddress", viewHolder.cDeviceLocation.getText().toString());
                        //Needed for intent calling
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        mContext.startActivity(i);

                    } else {
                        Toast.makeText(view.getContext(), "Error (Lockid null)", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }
        });


        mItemManger.bindView(viewHolder.itemView, position);
    }


    //This method decides how many list of item to created.
    @Override
    public int getItemCount() {
        return ItemCount;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    //This method is used for initializing the component present on swipe_lock_list_item activity
    public class MyViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView cDeviceName;
        TextView cDeviceLocation;

        public MyViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            cDeviceName = (TextView) itemView.findViewById(R.id.tvName);
            cDeviceLocation = (TextView) itemView.findViewById(R.id.tvLocation);
        }
    }
}
