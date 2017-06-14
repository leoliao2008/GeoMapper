package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.broadcast.PortDataReceiver;
import com.skycaster.geomapper.data.BaudRate;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.service.PortDataBroadcastingService;
import com.skycaster.geomapper.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import project.SerialPort.SerialPort;
import project.SerialPort.SerialPortFinder;

public class SerialPortAdminActivity extends BaseActionBarActivity {
    private ListView mListView;
    private Spinner spn_serialPorts;
    private Spinner spn_baudRates;
    private Button btn_confirm;
    private String path;
    private int baudRate;
    private SharedPreferences mSharedPreference;
    private SerialPort mSerialPort;
    private ArrayAdapter<String> mPathAdapter;
    private ArrayAdapter<String> mBaudRateAdapter;
    private int pathIndex;
    private int bdRateIndex;
    private String[] mBdRates;
    private String[] mAllDevicesPath;
    private MyPortDataReceiver mPortDataReceiver;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> list=new ArrayList<>();


    public static void start(Context context) {
        Intent starter = new Intent(context, SerialPortAdminActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.set_serial_port;
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_serial_port_admin;
    }

    @Override
    protected void initChildViews() {
        mListView= (ListView) findViewById(R.id.activity_serial_port_admin_console);
        spn_baudRates= (Spinner) findViewById(R.id.activity_serial_port_admin_spn_baud_rate);
        spn_serialPorts= (Spinner) findViewById(R.id.activity_serial_port_admin_spn_serial_port);
        btn_confirm= (Button) findViewById(R.id.activity_serial_port_admin_btn_confirm);
    }

    @Override
    protected void initRegularData() {
        mSharedPreference=getSharedPreferences("Config",MODE_PRIVATE);
        path=mSharedPreference.getString(Constants.SERIAL_PORT_PATH,"ttyAMA04");
        baudRate=mSharedPreference.getInt(Constants.SERIAL_PORT_BAUD_RATE,19200);
        initSpinnerSerialPortPaths();
        initSpinnerBaudRates();

        mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);

        mPortDataReceiver=new MyPortDataReceiver();
    }

    private void initSpinnerBaudRates() {
        BaudRate[] values = BaudRate.values();
        mBdRates = new String[values.length];
        for(int i=0;i<values.length;i++){
            mBdRates[i]=values[i].toString();
            if(mBdRates[i].equals(String.valueOf(baudRate))){
                bdRateIndex=i;
            }
        }
        mBaudRateAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mBdRates){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv= (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv= (TextView) super.getDropDownView(position, convertView, parent);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }
        };
        mBaudRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_baudRates.setAdapter(mBaudRateAdapter);
        spn_baudRates.setSelection(bdRateIndex);
        spn_baudRates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bdRateIndex=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerSerialPortPaths(){
        SerialPortFinder portFinder=new SerialPortFinder();
        mAllDevicesPath = portFinder.getAllDevicesPath();
        for(int i = 0; i< mAllDevicesPath.length; i++){
            if(mAllDevicesPath[i].equals(path)){
                pathIndex=i;
                break;
            }
        }
        mPathAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mAllDevicesPath){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv= (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv= (TextView) super.getDropDownView(position, convertView, parent);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }
        };
        mPathAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_serialPorts.setAdapter(mPathAdapter);
        spn_serialPorts.setSelection(pathIndex);
        spn_serialPorts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pathIndex=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mPortDataReceiver,new IntentFilter(PortDataReceiver.ACTION));
        if(PortDataBroadcastingService.getSerialPort()==null){
            openSerialPort(path,baudRate);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mPortDataReceiver);
    }

    private void openSerialPort(String path, int baudRate) {
        try {
            mSerialPort=new SerialPort(new File(path),baudRate,0);
        } catch (SecurityException e){
            ToastUtil.showToast(getResources().getString(R.string.serial_port_inauthorized));
        } catch (IOException pE) {
            if(pE.getMessage()!=null){
                ToastUtil.showToast(pE.getMessage());
            }
        }
        if(mSerialPort!=null){
            PortDataBroadcastingService.setSerialPort(mSerialPort);
            startService(new Intent(this, PortDataBroadcastingService.class));
        }
    }

    private void closeSerialPort(){
        stopService(new Intent(this, PortDataBroadcastingService.class));
        if(mSerialPort!=null){
            mSerialPort.close();
            mSerialPort=null;
        }
    }


    @Override
    protected void initListeners() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAllDevicesPath.length>0){
                    closeSerialPort();
                    String path = mAllDevicesPath[pathIndex];
                    setSerialPortPath(path);
                    Integer bdRate = Integer.valueOf(mBdRates[bdRateIndex]);
                    setBaudRate(bdRate);
                    openSerialPort(path,bdRate);
                }else {
                    ToastUtil.showToast(getResources().getString(R.string.serial_port_inauthorized));
                }
            }
        });

    }

    private void setBaudRate(int paramBaudRate){
        baudRate=paramBaudRate;
        mSharedPreference.edit().putInt(Constants.SERIAL_PORT_BAUD_RATE,baudRate).apply();
    }

    private void setSerialPortPath(String paramPath){
        path=paramPath;
        mSharedPreference.edit().putString(Constants.SERIAL_PORT_PATH,path).apply();
    }

    class MyPortDataReceiver extends PortDataReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra(PortDataReceiver.DATA);
            updateConsole(new String(bytes));
        }
    }

    private void updateConsole(String msg) {
        list.add(msg);
        mAdapter.notifyDataSetChanged();
        mListView.smoothScrollToPosition(Integer.MAX_VALUE);
    }
}
