package e.feather_con_1.device;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import e.feather_con_1.R;

public class DeviceConnectActivity extends Activity {

    public static final String ARG_ALLOW_TO_AUTO_CONNECT = "allow_to_auto_connect";
    private boolean allow_to_auto_connect;

    private ListView deviceListView;
    private ListAdapterCustom adapterCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connect);
        allow_to_auto_connect = getIntent().getBooleanExtra(ARG_ALLOW_TO_AUTO_CONNECT, true);
        adapterCustom = new ListAdapterCustom(this);
        deviceListView = (ListView)findViewById(R.id.list);
        deviceListView.setAdapter(adapterCustom);
    }

    public void rescanClicked(View view) {
        //todo
    }

    public void listItemClicked(BluetoothDevice device) {
        //todo
    }
}
