package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.BaudRate;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.receivers.PortDataReceiver;
import com.skycaster.geomapper.service.PortDataBroadcastingService;
import com.skycaster.geomapper.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import project.SerialPort.SerialPort;
import project.SerialPort.SerialPortFinder;

public class BeidouSetting extends BaseActionBarActivity {
    private ListView mListView;

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
    private TextView tv_currentBd;
    private TextView tv_currentPath;
    private ImageView iv_noData;
    private float mTextSize;


    public static void start(Context context) {
        Intent starter = new Intent(context, BeidouSetting.class);
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
        tv_currentPath= (TextView) findViewById(R.id.activity_serial_port_admin_tv_current_path);
        tv_currentBd= (TextView) findViewById(R.id.activity_serial_port_admin_tv_current_bd_rate);
        iv_noData= (ImageView) findViewById(R.id.activity_serial_port_admin_iv_no_data);
    }


    @Override
    protected void initRegularData() {
        mSharedPreference=getSharedPreferences("Config",MODE_PRIVATE);
        path=mSharedPreference.getString(StaticData.SERIAL_PORT_PATH,"ttyS1");
        baudRate=mSharedPreference.getInt(StaticData.SERIAL_PORT_BAUD_RATE,115200);
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mTextSize = getResources().getDimension(R.dimen.sp_24)/metrics.scaledDensity;
        mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(mTextSize);
                return textView;
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);

        mPortDataReceiver=new MyPortDataReceiver();

        tv_currentPath.setText(path);
        tv_currentBd.setText(baudRate+"");
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        if(PortDataBroadcastingService.getSerialPort()!=null){
            if(this.path.equals(path)&&this.baudRate==baudRate){
                return;
            }
        }
        SerialPort temp=null;
        try {
            temp=new SerialPort(new File(path),baudRate,0);
        } catch (SecurityException e){
            ToastUtil.showToast(getString(R.string.serial_port_not_authorized));
        } catch (IOException pE) {
            if(pE.getMessage()!=null){
                ToastUtil.showToast(pE.getMessage());
            }
        }
        if(temp!=null){
            closeSerialPort();
            setSerialPortPath(path);
            setBaudRate(baudRate);
            mSerialPort=temp;
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    PortDataBroadcastingService.setSerialPort(mSerialPort);
                    startService(new Intent(BeidouSetting.this, PortDataBroadcastingService.class));
                }
            },500);
            ToastUtil.showToast(getString(R.string.serial_port_success));
        }else {
            ToastUtil.showToast(getString(R.string.serial_port_not_authorized));
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


    }

    private void setBaudRate(int paramBaudRate){
        baudRate=paramBaudRate;
        mSharedPreference.edit().putInt(StaticData.SERIAL_PORT_BAUD_RATE,baudRate).apply();
        tv_currentBd.setText(String.valueOf(baudRate));
    }

    private void setSerialPortPath(String paramPath){
        path=paramPath;
        mSharedPreference.edit().putString(StaticData.SERIAL_PORT_PATH,path).apply();
        tv_currentPath.setText(path);
    }

    class MyPortDataReceiver extends PortDataReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            iv_noData.setVisibility(View.GONE);
            byte[] bytes = intent.getByteArrayExtra(PortDataReceiver.DATA);
            updateConsole(new String(bytes));
        }
    }

    private void updateConsole(String msg) {
        list.add(msg);
        mAdapter.notifyDataSetChanged();
        mListView.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_serial_port_admin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sp_admin_setting:
                displaySPSettingDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog mAlertDialog;

    private void displaySPSettingDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //view
        View rootView=View.inflate(this,R.layout.dialog_set_serial_port,null);
        Spinner spn_serialPorts= (Spinner) rootView.findViewById(R.id.activity_serial_port_admin_spn_serial_port);
        Spinner spn_baudRates=(Spinner) rootView.findViewById(R.id.activity_serial_port_admin_spn_baud_rate);
        Button btn_confirm=(Button) rootView.findViewById(R.id.activity_serial_port_admin_btn_confirm);
        Button btn_cancel= (Button) rootView.findViewById(R.id.activity_serial_port_admin_btn_cancel);
        //data:
        pathIndex=0;
        bdRateIndex=0;
        //data: bd rate
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


        //data: serial port
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


        //onclick
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAllDevicesPath.length>0){
                    String path = mAllDevicesPath[pathIndex];
                    Integer bdRate = Integer.valueOf(mBdRates[bdRateIndex]);
                    openSerialPort(path,bdRate);
                    mAlertDialog.dismiss();
                }else {
                    ToastUtil.showToast(getResources().getString(R.string.serial_port_not_authorized));
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        //attach to alert dialog
        mAlertDialog=builder.setView(rootView).setCancelable(false).create();
        mAlertDialog.show();
    }
}
