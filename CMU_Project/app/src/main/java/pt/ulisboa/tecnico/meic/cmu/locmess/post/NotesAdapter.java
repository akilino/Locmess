package pt.ulisboa.tecnico.meic.cmu.locmess.post;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Akilino on 16/03/2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{

    private ArrayList<Post> listPosts;
    private Context mContext;
    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onStatusButtonOwnerClicked(int p);
        void onViewButtonOwnerClicked(int p);
        void onViewButtonUserClicked(int p);
        void onStatusButtonUserClicked(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView messageTextView, senderTextView, modeTextView,locationTextView, titleTextView;
        private LinearLayout linearLayoutPost;
        private RelativeLayout relativeLayoutUser, relativeLayoutOwner;
        private Button statusButtonOwner, statusButtonUser, viewButtonOwner,viewButtonUser;
        private View container;

        public ViewHolder(View itemView) {
            super(itemView);

            messageTextView = (TextView) itemView.findViewById(R.id.messageEditText);
            senderTextView = (TextView) itemView.findViewById(R.id.senderTextView);
            modeTextView = (TextView) itemView.findViewById(R.id.modeTextView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextViewPosts);
            locationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
            linearLayoutPost = (LinearLayout) itemView.findViewById(R.id.linearLayoutPost);
            relativeLayoutOwner = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutOwner);
            relativeLayoutUser = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutUser);
            statusButtonOwner = (Button) itemView.findViewById(R.id.statusButtonOwner);
            statusButtonUser = (Button) itemView.findViewById(R.id.statusButtonUser);
            viewButtonOwner = (Button) itemView.findViewById(R.id.viewButtonOwner);
            viewButtonUser = (Button) itemView.findViewById(R.id.viewButtonUser);
            statusButtonOwner.setOnClickListener(this);
            statusButtonUser.setOnClickListener(this);
            viewButtonOwner.setOnClickListener(this);
            viewButtonUser.setOnClickListener(this);
            container = itemView.findViewById(R.id.layout);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.statusButtonOwner){
                Log.d(TAG, "onClick: unpostButtonClicked " + getAdapterPosition());
                itemClickCallback.onStatusButtonOwnerClicked(getAdapterPosition());
            }else if(v.getId() == R.id.viewButtonOwner){
                Log.d(TAG,"onClick: viewButtonClicked " + getAdapterPosition());
                itemClickCallback.onViewButtonOwnerClicked(getAdapterPosition());
            }else if(v.getId() == R.id.viewButtonUser){
                Log.d(TAG,"onClick: viewButtonClicked " + getAdapterPosition());
                itemClickCallback.onViewButtonUserClicked(getAdapterPosition());
            }else if(v.getId() == R.id.statusButtonUser){
                Log.d(TAG,"onClick: viewButtonClicked " + getAdapterPosition());
                itemClickCallback.onStatusButtonUserClicked(getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.post_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(postView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String username = null;

        SharedPreferences sharedPref = getContext().getSharedPreferences("file", Context.MODE_PRIVATE);
        boolean logged= sharedPref.getBoolean("logged", false);

        if(logged==true) {
            username = sharedPref.getString("username", null);
        }

        Log.d(TAG, "onBindViewHolder: " + username);

        Post post = listPosts.get(position);
        
        TextView messageTextView = holder.messageTextView;
        TextView titleTextView = holder.titleTextView;
        TextView senderTextView = holder.senderTextView;
        TextView modeTextView = holder.modeTextView;
        TextView locationTextView = holder.locationTextView;
        LinearLayout linearLayoutPost = holder.linearLayoutPost;
        RelativeLayout relativeLayoutOwner = holder.relativeLayoutOwner;
        RelativeLayout relativeLayoutUser = holder.relativeLayoutUser;

        titleTextView.setText(formatStyles(post.getTitle(),""));
        messageTextView.setText(formatStyles("Message: ", post.getMessage()));
        senderTextView.setText(formatStyles("Author: ", post.getUser()));
        modeTextView.setText(formatStyles("Mode: ",post.getMode()));
        locationTextView.setText(formatStyles("Location: ",post.getLocation()));

        int colorGreen = 0xff3E8914;
        int colorDarkGreen = 0xff134611;

        if(post.getUser().equals(username)){
            titleTextView.setBackgroundColor(colorDarkGreen);
            linearLayoutPost.setBackgroundColor(colorGreen);
            linearLayoutPost.removeView(relativeLayoutUser);
        }else{
            linearLayoutPost.removeView(relativeLayoutOwner);
        }

    }

    private SpannableString formatStyles(String bold, String normal)
    {
        SpannableString styledText = new SpannableString(bold + normal);
        styledText.setSpan(new StyleSpan(Typeface.BOLD), 0, bold.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return styledText;
    }

    @Override
    public int getItemCount() {
        if(listPosts==null)
            return 0;
        return listPosts.size();
    }

    public NotesAdapter(Context context, ArrayList<Post> posts){
        listPosts = posts;
        mContext = context;
    }

    private Context getContext(){
        return mContext;
    }
}
