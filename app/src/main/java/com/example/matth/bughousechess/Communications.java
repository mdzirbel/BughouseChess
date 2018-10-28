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
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
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
    static LinkedList<BluetoothDevice> candidateDevices = new LinkedList<BluetoothDevice>();
    public Communications(Context context, MainActivity.MyAdapter dataAdapter, FragmentManager fm)
    {
        this.fm = fm;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(context, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        else
        {
            /*if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(enableBtIntent, Activity.REQUEST_ENABLE_BT);
            }*/
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
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
    }
    public void connectTo(String address)
    {
        new ConnectThread(address).start();
    }
    class ConnectThread extends Thread
    {
        String address;
        public ConnectThread(String add)
        {
            this.address = add;
        }
        public void run() {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            try {
                BluetoothSocket sock = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("a56f3f83-5b88-4101-9eb1-8109bb9eebb9"));
                sock.connect();
                new ConnectedThread(sock).start();//a56f3f83-5b88-4101-9eb1-8109bb9eebb9
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void receiveConnectionFrom(BluetoothSocket sock)
    {
        try {
            new ConnectedThread(sock).start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    static void gotoChess()
    {
        Log.d(TAG, "GO TO CHESS");
    }
    static boolean accepted = false;
    static void acceptGame()
    {
        if(accepted)
        {
            gotoChess();//go if accepted is already set
        }
        accepted = true;
        MainActivity.coms.conThread.write("A");
    }
    static void declineGame()
    {
        accepted = false;
        MainActivity.coms.conThread.write("D");
    }
    public static class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to play a game with "+getArguments().getString("name")+"?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            acceptGame();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            declineGame();
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
    boolean parse(String str)
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
                gotoChess();//go if already received accepted
            }
            accepted = true;
        }
        else if(messageParts[0].equals("D"))
        {
            if(fmdf.isVisible())
            {
                fmdf.dismiss();
                TheyDecliened TD = new TheyDecliened();
                TD.show(fm, "FireMissiles");
            }
            Log.d(TAG, "Cancel dialogs");
        }
        return true;
    }
    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BufferedReader mmInStream;
        private final PrintWriter mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) throws UnsupportedEncodingException {
            conThread = this;
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
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
            //mmInStream = (BufferedInputStream) tmpIn;
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
            Log.d(TAG, "Start listening");
            while (true) {
                try {
                    // Read from the InputStream.
                    String str = mmInStream.readLine();
                    Log.d(TAG, "<- "+str);
                    boolean con = parse(str);
                    if(!con)
                    {
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
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}