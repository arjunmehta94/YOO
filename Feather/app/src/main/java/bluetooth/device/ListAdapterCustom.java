package bluetooth.device;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by arjunmehta94 on 8/13/15.
 */
public class ListAdapterCustom extends BaseAdapter {
    List<BluetoothDevice> deviceList;
    DeviceConnectActivity activity;

    ListAdapterCustom(DeviceConnectActivity activity) {
        deviceList = new LinkedList<>();
        this.activity = activity;
    }

    public synchronized boolean add(BluetoothDevice device) {
        boolean success = !contains(device) && deviceList.add(device);
        if (success) {
            this.notifyDataSetChanged();
        }
        return success;
    }

    public synchronized void remove(BluetoothDevice device) {
        deviceList.remove(device);
    }

    public synchronized boolean contains(BluetoothDevice device) {
        return deviceList.contains(device);
    }

    @Override
    public synchronized int getCount() {
        return deviceList.size();
    }

    @Override
    public synchronized BluetoothDevice getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public synchronized long getItemId(int position) {
        return position;
    }


    @Override
    public synchronized View getView(int position, View convertView, ViewGroup parent) {
        final BluetoothDevice curr_device = getItem(position);
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).
                    inflate(R.layout.list_item, parent, false);
        }
        ((TextView) v.findViewById(R.id.device_name)).setText(curr_device.getName());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.listItemClicked(curr_device);
            }
        });
        return v;
    }
}
