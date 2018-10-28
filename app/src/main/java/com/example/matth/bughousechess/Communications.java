package com.example.matth.bughousechess;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public class Communications
{
    ConnectedThread conThread;
    private static final String TAG = "MY_APP_DEBUG_TAG";
    final MainActivity.MyAdapter dataAdapter;
    static LinkedList<BluetoothDevice> candidateDevices = new LinkedList<BluetoothDevice>();
    final MainActivity mainActivity;
    public Communications(Context context, MainActivity.MyAdapter dataAdapter, FragmentManager fm, MainActivity mainActivity)
    {
        this.fm = fm;
        this.dataAdapter = dataAdapter;
        this.mainActivity = mainActivity;
        scanAndUpdateRecycleView(context);
    }
    void reset(Context context)
    {
        conThread.cancel();
        conThread = null;
        accepted = false;
        scanAndUpdateRecycleView(context);
        MainActivity.startListening(context);
    }
    void scanAndUpdateRecycleView(Context context)
    {
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(context, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        candidateDevices.clear();
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            //String deviceName = device.getName();
                            //String deviceHardwareAddress = device.getAddress(); // MAC address
                            int type = device.getBluetoothClass().getDeviceClass();
                            if(type==BluetoothClass.Device.PHONE_SMART)
                            {
                                candidateDevices.add(device);
                            }
                            //Log.d("Max: 41", deviceName+":"+deviceHardwareAddress+":"+(type==BluetoothClass.Device.PHONE_SMART));
                        }
                        dataAdapter.updatePhones();
                    }
                }
            });
            /*if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(enableBtIntent, Activity.REQUEST_ENABLE_BT);
            }*/
        }
    }
    public void connectTo(String address, Context context)
    {
        weSendInvite = true;
        Log.d(TAG, "attempting to connect");
        new ConnectThread(address, context).start();
    }
    int uuidNum = 0;
    class ConnectThread extends Thread
    {
        private final String address;
        private final Context context;
        public ConnectThread(String add, Context context)
        {
            this.address = add;
            this.context = context;
        }
        public void run() {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            try {
                while(true)
                {
                    final BluetoothSocket sock = device.createInsecureRfcommSocketToServiceRecord(MainActivity.uuids[uuidNum]);
                    uuidNum++;
                    if(uuidNum>=100)
                    {
                        break;
                    }
                    //new cancelConnectThread(sock).start();
                    sock.connect();
                    Log.d(TAG, "connected?");
                    if (sock.isConnected())
                    {
                        new ConnectedThread(sock, context).start();//a56f3f83-5b88-4101-9eb1-8109bb9eebb9
                        break;
                    }
                    else
                    {
                        Log.d(TAG, "uuid "+uuidNum+" failed");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class cancelConnectThread extends Thread
    {
        final BluetoothSocket sock;
        public cancelConnectThread(BluetoothSocket s)
        {
            sock = s;
        }
        public void run() {
            try {
                Thread.sleep(3000);
                if(!sock.isConnected())
                {
                    sock.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "stop cancel");
        }
    }
    public void receiveConnectionFrom(BluetoothSocket sock, Context context)
    {
        weSendInvite = false;
        try {
            new ConnectedThread(sock, context).start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    static void gotoChess(Context context)
    {
        Log.d(TAG, "GO TO CHESS");
        Intent intent = new Intent(context, ChessBoardActivity.class);
        context.startActivity(intent);
    }

    void checkMate(String winnerTeam)
    {
        StaleCheckDialog SCD = new StaleCheckDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("check", true);
        bundle.putString("team", winnerTeam);
        SCD.setArguments(bundle);
        SCD.show(fm, "FireMissiles");
        MainActivity.coms.conThread.write("C|"+(winnerTeam.equals("black")?"white":"black"));
    }
    void staleMate(String winnerTeam)
    {
        StaleCheckDialog SCD = new StaleCheckDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("check", false);
        bundle.putString("team", winnerTeam);
        SCD.setArguments(bundle);
        SCD.show(fm, "FireMissiles");
        MainActivity.coms.conThread.write("S|"+(winnerTeam.equals("black")?"white":"black"));
    }

    static boolean accepted = false;
    static void acceptGame(Context context)
    {
        if(accepted)
        {
            gotoChess(context);//go if accepted is already set
        }
        accepted = true;
        MainActivity.coms.conThread.write("A");
        //MainActivity.coms.reset(context);
    }
    static void declineGame(Context context)
    {
        accepted = false;
        MainActivity.coms.conThread.write("D");
        MainActivity.coms.reset(context);
    }
    public static class StaleCheckDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String mess = "Stalemate! "+getArguments().getString("team")+" wins";
            if(getArguments().getBoolean("check"))
            {
                mess = "Checkmate! "+getArguments().getString("team")+" wins";
            }
            builder.setMessage(mess)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG, "Ok");
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public static class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to play a game with "+getArguments().getString("name")+"?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            acceptGame(getContext());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            declineGame(getContext());
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public static class UserNoName extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String mess = "The user your trying to connect to does not have a name set";
            if(getArguments().getBoolean("me"))
            {
                mess = "Someone tried to connect to you for a game but you dont have a name set";
            }
            builder.setMessage(mess)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public static class TheyDecliened extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String mess = "They decliened your invitation";
            if(getArguments().getBoolean("hostDeclined"))
            {
                mess = "They canceled their invitation";
            }
            builder.setMessage(mess)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    void sendReserve(ChessPiece piece)
    {
        conThread.write("R|"+piece.team+"+"+piece.type);
    }
    FragmentManager fm = null;
    FireMissilesDialogFragment fmdf;
    boolean weSendInvite = false;
    boolean parse(String str, Context context)
    {
        String[] messageParts = str.split("\\|");
        if(messageParts[0].equals("R"))
        {
            String[] parts = messageParts[1].split("\\+");
            Log.d(TAG, "Reserve receive "+parts[0]+":"+parts[1]);
            ChessBoardActivity.board.recieveReserve(parts[0], parts[1]);
        }
        else if(messageParts[0].equals("H"))
        {
            if(messageParts[1].equals("!") || MainActivity.name.equals(""))
            {
                return false;
            }
            fmdf = new FireMissilesDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", messageParts[1]);
            fmdf.setArguments(bundle);
            fmdf.show(fm, "FireMissiles");
        }
        else if(messageParts[0].equals("A"))
        {
            if(accepted)
            {
                gotoChess(context);//go if we already accepted
            }
            //reset(context);
            accepted = true;
        }
        else if(messageParts[0].equals("D"))
        {
            if(fmdf.getDialog().isShowing())
            {
                fmdf.dismiss();
                TheyDecliened TD = new TheyDecliened();
                Bundle bundle = new Bundle();
                bundle.putBoolean("hostDeclined", !weSendInvite);
                TD.setArguments(bundle);
                TD.show(fm, "FireMissiles");
            }
            reset(context);
            Log.d(TAG, "Cancel dialogs");
        }
        else if(messageParts[0].equals("S"))
        {
            StaleCheckDialog SCD = new StaleCheckDialog();
            Bundle bundle = new Bundle();
            bundle.putBoolean("check", false);
            bundle.putString("team", messageParts[1]);
            SCD.setArguments(bundle);
            SCD.show(fm, "FireMissiles");
        }
        else if(messageParts[0].equals("C"))
        {
            StaleCheckDialog SCD = new StaleCheckDialog();
            Bundle bundle = new Bundle();
            bundle.putBoolean("check", true);
            bundle.putString("team", messageParts[1]);
            SCD.setArguments(bundle);
            SCD.show(fm, "FireMissiles");
        }
        return true;
    }
    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BufferedReader mmInStream;
        private final PrintWriter mmOutStream;
        private final Context context;

        public ConnectedThread(BluetoothSocket socket, Context context) throws UnsupportedEncodingException {
            conThread = this;
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.context = context;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }
            mmInStream = new BufferedReader(new InputStreamReader(tmpIn, "UTF-8"));
            mmOutStream = new PrintWriter(tmpOut);
            if(MainActivity.name.equals(""))
            {
                write("H|!");
            }
            else
            {
                write("H|" + MainActivity.name);
            }
        }

        public void run() {
            Log.d(TAG, "Starting communications");
            while (true) {
                try {
                    // Read from the InputStream.
                    String str = mmInStream.readLine();
                    Log.d(TAG, "<- " + str);
                    boolean con = parse(str, context);
                    if (!con) {
                        UserNoName fmdf = new UserNoName();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("me", MainActivity.name.equals(""));
                        fmdf.setArguments(bundle);
                        fmdf.show(fm, "");
                        break;
                    }
                    // Send the obtained bytes to the UI activity.
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
            cancel();
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String str) {
            Log.d(TAG, "-> "+str);
            mmOutStream.println(str);
            mmOutStream.flush();
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmOutStream.close();
                mmInStream.close();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}