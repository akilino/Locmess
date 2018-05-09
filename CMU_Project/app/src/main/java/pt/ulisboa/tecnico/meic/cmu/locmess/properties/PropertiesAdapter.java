package pt.ulisboa.tecnico.meic.cmu.locmess.properties;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Akilino on 06/04/2017.
 */

public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.ViewHolder> {

    private ItemClickCallback itemClickCallback;
    private ArrayList<Property> propertyList;
    private Context context;

    public interface ItemClickCallback{
        void onItemClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView propertiesTextView;
        private View container;

        public ViewHolder(View itemView) {
            super(itemView);

            propertiesTextView = (TextView) itemView.findViewById(R.id.propertyTextView);
            container = itemView.findViewById(R.id.layoutProperty);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.layoutProperty){
                Log.d(TAG, "onClick: ItemClicked " + getAdapterPosition());
                itemClickCallback.onItemClick(getAdapterPosition());
            }
        }
    }



    public PropertiesAdapter(Context context, ArrayList<Property> propertyList){
        this.context = context;
        this.propertyList = propertyList;
    }

    private Context getContext(){
        return context;
    }

    @Override
    public PropertiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View propertyView = inflater.inflate(R.layout.property_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(propertyView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PropertiesAdapter.ViewHolder holder, int position) {
        Property property = propertyList.get(position);

        TextView propertyTextView = holder.propertiesTextView;
        propertyTextView.setText(property.getText());
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }
}
