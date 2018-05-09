package pt.ulisboa.tecnico.meic.cmu.locmess.location;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;

/**
 * Created by Akilino on 06/04/2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locationList;
    private Context context;
    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onItemClick(int p);
        void onSecondaryIconClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    public LocationAdapter(Context context, List<Location> locations){
        this.context = context;
        this.locationList = locations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View locationView = inflater.inflate(R.layout.location_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(locationView);
        return viewHolder;
    }

    private SpannableString formatStyles(String bold, String normal)
    {
        SpannableString styledText = new SpannableString(bold + normal);
        styledText.setSpan(new StyleSpan(Typeface.BOLD), 0, bold.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return styledText;
    }

    @Override
    public void onBindViewHolder(LocationAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        Location location = locationList.get(position);

        TextView locationNameTextView = holder.textViewLocationName;
        TextView longTextView = holder.textViewLong;
        TextView latTextView = holder.textViewLat;
        TextView radiusTextView = holder.textViewRadius;
        TextView ssidTextView = holder.textViewSSID;
        Button viewButton = holder.viewButton;
        LinearLayout linearLayoutLocation = holder.linearLayoutLocation;
        LinearLayout linearLayoutLocationButtons = holder.linearLayoutLocationButtons;

        if(location instanceof Wifi){
            Wifi wifiLocation = (Wifi) location;
            locationNameTextView.setText(wifiLocation.locationName);
            ssidTextView.setText(formatStyles("SSID: ", wifiLocation.SSID));
            linearLayoutLocation.removeView(longTextView);
            linearLayoutLocation.removeView(latTextView);
            linearLayoutLocation.removeView(radiusTextView);
            linearLayoutLocationButtons.removeView(viewButton);
        }else if(location instanceof GPS){
            GPS gpsLocation = (GPS) location;
            locationNameTextView.setText(gpsLocation.locationName);
            longTextView.setText(formatStyles("Longitude: ",gpsLocation.lon));
            latTextView.setText(formatStyles("Latitude: ", gpsLocation.lat));
            radiusTextView.setText(formatStyles("Radius: ", gpsLocation.radius + "m"));
            linearLayoutLocation.removeView(ssidTextView);
        }

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewLong, textViewLat, textViewRadius, textViewSSID, textViewLocationName;
        public Button deleteButton, viewButton;
        public LinearLayout linearLayoutLocation, linearLayoutLocationButtons;
        //public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            linearLayoutLocation = (LinearLayout) itemView.findViewById(R.id.linearLayoutLocation);
            linearLayoutLocationButtons = (LinearLayout) itemView.findViewById(R.id.linearLayoutLocationButtons);
            //textViewLocationExtraInfo = (TextView) itemView.findViewById(R.id.textViewLocationExtraInfo);
            textViewLong = (TextView) itemView.findViewById(R.id.textViewLong);
            textViewLat = (TextView) itemView.findViewById(R.id.textViewLat);
            textViewRadius = (TextView) itemView.findViewById(R.id.textViewRadius);
            textViewSSID = (TextView) itemView.findViewById(R.id.textViewSSID);
            textViewLocationName = (TextView) itemView.findViewById(R.id.textViewLocationName);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButtonLocation);
            viewButton = (Button) itemView.findViewById(R.id.viewButtonLocation);
            deleteButton.setOnClickListener(this);
            viewButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.deleteButtonLocation){
                itemClickCallback.onSecondaryIconClick(getAdapterPosition());
            }else if(v.getId() == R.id.viewButtonLocation){
                itemClickCallback.onItemClick(getAdapterPosition());
                Toast.makeText(context, "View Button clicked", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
