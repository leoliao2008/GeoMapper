package com.skycaster.geomapper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.skycaster.geomapper.models.GPIOModel;
import com.skycaster.geomapper.service.BeidouDataBroadcastingService;
import com.skycaster.geomapper.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

import project.SerialPort.SerialPortFinder;

public class GPSSetting extends BaseActionBarActivity {
    private ListView mListView;

    private String path;
    private int baudRate;
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
    private GPIOModel mGPIOModel;


    public static void start(Context context) {
        Intent starter = new Intent(context, GPSSetting.class);
        context.startActivity(starter);
    }

    @Override
    protected String setActionBarTitle() {
        return getResources().getString(R.string.set_serial_port);
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
    protected void initData() {
        path=StaticData.GPS_MODULE_SP_PATH;
        baudRate=StaticData.GPS_MODULE_SP_BAUD_RATE;
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mTextSize = getResources().getDimension(R.dimen.sp_18)/metrics.scaledDensity;
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

        tv_currentPath.setText(path);
        tv_currentBd.setText(baudRate+"");
        mGPIOModel=new GPIOModel();
        try {
            mGPIOModel.turnOnAllModulesPow();
        } catch (IOException e) {
            ToastUtil.showToast(e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPortDataReceiver=new MyPortDataReceiver();
        registerReceiver(mPortDataReceiver,new IntentFilter(StaticData.ACTION_GPS_SERIAL_PORT_DATA));
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mPortDataReceiver);
        mPortDataReceiver=null;
        if(isFinishing()){
            try {
                mGPIOModel.turnOffAllModulesPow();
            } catch (IOException e) {
                ToastUtil.showToast(e.getMessage());
            }
        }
    }



    @Override
    protected void initListeners() {


    }


    class MyPortDataReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            iv_noData.setVisibility(View.GONE);
            byte[] bytes = intent.getByteArrayExtra(StaticData.EXTRA_BYTES_GPS_MODULE_SERIAL_PORT_DATA);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                tv.setTextSize(mTextSize);
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv= (TextView) super.getDropDownView(position, convertView, parent);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(mTextSize);
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
                tv.setTextSize(mTextSize);
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv= (TextView) super.getDropDownView(position, convertView, parent);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(mTextSize);
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
                    final String path = mAllDevicesPath[pathIndex];
                    final Integer bdRate = Integer.valueOf(mBdRates[bdRateIndex]);
                    //先停止目前服务
                    stopService(new Intent(GPSSetting.this, BeidouDataBroadcastingService.class));
                    //一秒后重启服务
                    BaseApplication.postDelay(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent=new Intent(GPSSetting.this,BeidouDataBroadcastingService.class);
                            intent.putExtra(StaticData.SERIAL_PORT_PATH,path);
                            intent.putExtra(StaticData.SERIAL_PORT_BAUD_RATE,bdRate);
                            startService(intent);
                        }
                    },1000);
                    tv_currentPath.setText(path);
                    tv_currentBd.setText(String.valueOf(bdRate));
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
