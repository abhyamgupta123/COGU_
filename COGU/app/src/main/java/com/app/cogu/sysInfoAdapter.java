package com.app.cogu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//public class sysInfoAdapter {
//}


// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class sysInfoAdapter extends RecyclerView.Adapter<sysInfoAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView heading;
        public TextView data;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super( itemView );

            heading = (TextView) itemView.findViewById( R.id.heading );
            data = (TextView) itemView.findViewById( R.id.data );
        }
    }

    private List<sysInformations> informations;

    // Pass in the contact array into the constructor
    public sysInfoAdapter(List<sysInformations> full_info) {
        this.informations = full_info;
    }

    @Override
    public sysInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from( context );

        // Inflate the custom layout
        View sysview = inflater.inflate( R.layout.info_data_layout, parent, false );

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder( sysview );
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(sysInfoAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        sysInformations sysinfo = informations.get( position );

        // Set item views based on your views and data model
        TextView textView_head = holder.heading;
        textView_head.setText( sysinfo.getName() );
        TextView textView_data = holder.data;
        textView_data.setText( sysinfo.getData() );
//        button.setEnabled(contact.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return informations.size();
    }
}

