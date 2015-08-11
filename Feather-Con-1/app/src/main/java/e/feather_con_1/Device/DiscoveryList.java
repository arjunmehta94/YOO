package e.feather_con_1.Device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import e.feather_con_1.R;

/**
 * Created by anurag on 7/8/15.
 */
public class DiscoveryList extends DialogFragment {
    DeviceManager deviceManager;

    ListView deviceListView;
    ListAdapterCustom adapterCustom;

    Button scanButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceManager = DeviceManager.getInstance();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_search_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceListView = (ListView)view.findViewById(R.id.list);
        if(adapterCustom==null) {
            adapterCustom = new ListAdapterCustom(getActivity());
        }
        deviceListView.setAdapter(adapterCustom);
        //deviceManager.deviceListExists = true;

        this.getView().setVisibility(View.GONE);

        scanButton = (Button) getView().findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceManager.scanButtonPressed();
                deviceManager.bleConnect();
            }
        });

        deviceManager.startDiscovery();

        //scanLeDevice(true);

    }

    public void add(BluetoothDevice device) {
        if(adapterCustom.add(device)) {
            Log.e("Device Added","Device Added");
        } else {
            Log.e("failed to add","already exists");
        }
    }

    private class ListAdapterCustom extends BaseAdapter {
        List<BluetoothDevice> deviceList;
        Context context;
        private ListAdapterCustom(Context context) {
            deviceList = new LinkedList<>();
            this.context = context;
        }

        public boolean add(BluetoothDevice device) {
            boolean b = !deviceList.contains(device) && deviceList.add(device);
            if (b){
                Log.e("inside", ""+deviceList.size());
                this.notifyDataSetChanged();
            }
            return b;
        }

        public boolean contains(BluetoothDevice device) {
            if(deviceList.contains(device)) {
                return true;
            }
            return false;
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return deviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final BluetoothDevice curr_device = getItem(position);
            View v = convertView;
            if(v==null) {
                v = ((LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).
                        inflate(R.layout.list_item, parent, false);
            }
            ((TextView)v.findViewById(R.id.device_name)).setText(curr_device.getName());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("clicked","here");
                    deviceManager.establishConnection(curr_device);
                }
            });
            return  v;
        }
    }
}
