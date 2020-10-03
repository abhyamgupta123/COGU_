package com.app.cogu;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.startActivity;

public class channelNameAdapter extends RecyclerView.Adapter<channelNameAdapter.ViewHolder>{

    private List<channel_list_Class> channels;
    private Map<String, String> deviceMapping;
    private String pubkey;
    private String subkey;
    private String app_uuid;
    Context context;



    public channelNameAdapter(List<channel_list_Class> full_info, Map<String,String> devicemap, String pubkey, String subkey, String uuid, Context contextm) {
        this.channels      = full_info;
        this.context       = contextm;
        this.deviceMapping = devicemap;
        this.pubkey        = pubkey;
        this.subkey        = subkey;
        this.app_uuid      = uuid;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView channelname;
            public ConstraintLayout constraintLayout;


            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super( itemView );


                channelname = (TextView) itemView.findViewById( R.id.name );
                constraintLayout = (ConstraintLayout) itemView.findViewById( R.id.constraintlayout );
            }

        }

        // Pass in the contact array into the constructor


        @Override
        public com.app.cogu.channelNameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from( context );

            // Inflate the custom layout
            View sysview = inflater.inflate( R.layout.channel_list_view, parent, false );

            // Return a new holder instance
            com.app.cogu.channelNameAdapter.ViewHolder viewHolder = new com.app.cogu.channelNameAdapter.ViewHolder( sysview );
            return viewHolder;
//            View v = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.channel_list_view, parent, false);                               // This is where you actually should call your context from parent
//            // because the context is for each view (row) and the parent here
//            // is the ViewGroup parent (parameter) not for the whole adapter
//
//            ViewHolder vh = new ViewHolder(v);                                                                         // Instantiate a viewholder from a view and return it
//            return vh;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(com.app.cogu.channelNameAdapter.ViewHolder holder, final int position) {
            // Get the data model based on position
            final channel_list_Class channelNames = channels.get( position );

            TextView channelname_field = holder.channelname;
            channelname_field.setText( channelNames.getChannelName() );


            holder.constraintLayout.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText( context, "Connecting to " + channelNames.getChannelName(), Toast.LENGTH_SHORT ).show();
                    String device_name = deviceMapping.get( channelNames.getChannelName() );
                    Intent mainactivity = new Intent( context, MainActivity.class );
                    mainactivity.putExtra( "channel_name", channelNames.getChannelName() );
                    mainactivity.putExtra( "deviceName", device_name );
                    mainactivity.putExtra( "pubkey", pubkey );
                    mainactivity.putExtra( "subkey", subkey );
                    mainactivity.putExtra( "uuid", app_uuid );
                    context.startActivity( mainactivity );
//                    ((Activity)context).finish();
                }
            } );
        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return channels.size();
        }
    }
//public class channelNameAdapter extends FirestoreRecyclerAdapter<channel_list_Class, channelNameAdapter.ViewHolder> {
//
//
//    /**
//     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
//     * FirestoreRecyclerOptions} for configuration options.
//     *
//     * @param options
//     */
//    public channelNameAdapter(@NonNull FirestoreRecyclerOptions<channel_list_Class> options) {
//        super( options );
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull channel_list_Class model) {
//        holder.channelname.setText( model.getChannelName() ) ;
//
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from( context );
//
//        // Inflate the custom layout
//        View sysview = inflater.inflate( R.layout.channel_list_view, parent, false );
//
//        // Return a new holder instance
//        com.app.cogu.channelNameAdapter.ViewHolder viewHolder = new com.app.cogu.channelNameAdapter.ViewHolder( sysview );
//        return viewHolder;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        // Your holder should contain a member variable
//        // for any view that will be set as you render a row
//        public TextView channelname;
//        public ConstraintLayout constraintLayout;
//
//
//        // We also create a constructor that accepts the entire item row
//        // and does the view lookups to find each subview
//        public ViewHolder(View itemView) {
//            // Stores the itemView in a public final member variable that can be used
//            // to access the context from any ViewHolder instance.
//            super( itemView );
//
//
//            channelname = (TextView) itemView.findViewById( R.id.name );
//            constraintLayout = (ConstraintLayout) itemView.findViewById( R.id.constraintlayout );
//        }
//
//    }
//}

