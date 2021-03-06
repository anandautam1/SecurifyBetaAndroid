package org.farmate.securifybeta.other;

/**
 * Created by Ananda on 1/09/2017.
 */

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.farmate.securifybeta.R;
import org.farmate.securifybeta.database.jobsLocal;
import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 * edited Ananda Utama
 * https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<jobsLocal> jobsLocalList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView jobID;
        public TextView jobNickNameString;
        public TextView registrationNumberString;
        public TextView lastupdatedString;
        public ImageView thumbnail;
        public Button sendButton;
        public Button detailsButton;
        public Button deleteButton;

        public MyViewHolder(View view) {
            super(view);
            jobID = (TextView) view.findViewById(R.id.jobID);
            jobNickNameString = (TextView) view.findViewById(R.id.jobNickName);
            registrationNumberString = (TextView) view.findViewById(R.id.registrationNumber);
            lastupdatedString = (TextView) view.findViewById(R.id.lastUpdated);
            thumbnail = (ImageView) view.findViewById(R.id.jobImage);
            sendButton = (Button) view.findViewById(R.id.buttonAcceptRequest);
            detailsButton = (Button) view.findViewById(R.id.buttonDeclineRequest);
            deleteButton = (Button) view.findViewById(R.id.buttonDeleteRequest);
        }
    }

    public AlbumsAdapter(Context mContext, List<jobsLocal> jobsLocalList) {
        this.mContext = mContext;
        this.jobsLocalList = jobsLocalList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        jobsLocal jobs = jobsLocalList.get(position);
        holder.jobID.setText(String.valueOf(jobs.getJobID()));
        holder.jobNickNameString.setText(jobs.getJobNickName());
        holder.registrationNumberString.setText(jobs.getRegistrationNumberString());
        holder.lastupdatedString.setText(jobs.getLastUpdated());

        /*
        sendButton = (Button) view.findViewById(R.id.buttonAcceptRequest);
        detailsButton = (Button) view.findViewById(R.id.buttonDeclineRequest);
        deleteButton = (Button) view.findViewById(R.id.buttonDeleteRequest);
        */

        // loading album cover using Glide library
        Glide.with(mContext).load(new File(jobs.getImage_uri())).into(holder.thumbnail);

        holder.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Send To The Database", Toast.LENGTH_SHORT).show();
            }
        });

        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Opening Details", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Deleting Database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                 return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                 return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return jobsLocalList.size();
    }
}