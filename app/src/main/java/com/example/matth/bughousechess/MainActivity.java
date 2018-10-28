package com.example.matth.bughousechess;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static Communications coms;
    private static final String TAG = "MY_APP_DEBUG_TAG";
    EditText nameText;
    static String name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Intent intent = new Intent(MainActivity.this, ChessBoardActivity.class);
        startActivity(intent);*/
        for(int i = 0; i < 100; i++)
        {
            uuids[i] = UUID.nameUUIDFromBytes(String.valueOf(i*56+10).getBytes());
        }
        nameText = (EditText) findViewById(R.id.editTextName);

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = editable.toString();
                setButtons(!(name.equals("Name") || name.equals("")));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);


        coms = new Communications(getApplicationContext(), mAdapter, getSupportFragmentManager(), this);
        startListening(getApplicationContext());
    }
    static UUID[] uuids = new UUID[100];
    void setButtons(boolean enabled)
    {
        mAdapter.enabled = enabled;
        mAdapter.notifyDataSetChanged();
    }
    static ListenerClass listener;
    static void startListening(Context context)
    {
        if(listener==null || !listener.isAlive())
        {
            listener = new ListenerClass(context);
            listener.start();
        }
    }
    static int uuidNum = 0;
    static class ListenerClass extends Thread
    {
        final Context context;
        public ListenerClass(Context context)
        {
            this.context = context;
        }
        public void run()
        {
            try {
                Log.i(TAG, "waiting for connection");
                BluetoothServerSocket sock = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord("Max's Bluetooth communication system", uuids[uuidNum]);
                uuidNum++;
                BluetoothSocket socket = sock.accept();
                if(socket!=null)
                {
                    coms.receiveConnectionFrom(socket, context);
                    Log.i(TAG, "accepted");
                }
                else
                {
                    Log.i(TAG, "failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        boolean enabled = false;
        public void updatePhones()
        {
            notifyDataSetChanged();
        }
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public RelativeLayout RL;
            public MyViewHolder(RelativeLayout v) {
                super(v);
                RL = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter() {

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Button button = ((Button)holder.RL.findViewById(R.id.add_btn));
            button.setEnabled(enabled);
            button.setText(coms.candidateDevices.get(position).getName());
            button.setOnClickListener(new ClickListener(position));
        }
        class ClickListener implements View.OnClickListener
        {
            int position = 0;
            public ClickListener(int pos)
            {
                position = pos;
            }
            @Override
            public void onClick(View view)
            {
                //Toast.makeText(getApplicationContext(), "Connecting to "+coms.candidateDevices.get(position).getName(), Toast.LENGTH_SHORT).show();
                ((Button) view).setText("Connecting...");
                coms.connectTo(coms.candidateDevices.get(position).getAddress(), getApplicationContext());
            }
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return coms.candidateDevices.size();
        }
    }
}
