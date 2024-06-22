package com.dreamk.mywifitest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;

import com.dreamk.mywifitest.util.ToastUtil;
import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.easysocket.utils.LogUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


//
//github仓库：
//https://github.com/DreamK2826/MyWiFiTest
//主要参考资料：https://blog.csdn.net/gwplovekimi/article/details/106015415
//https://blog.csdn.net/gwplovekimi/article/details/106015415
//https://github.com/jiusetian/EasySocket
//https://www.runoob.com/w3cnote/android-tutorial-listview.html
//


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1000;

    ListView lv1;
    Button btn_test,btn_send,btn_refresh;
    EditText et_POS1,et_POS2,et_nameOfPoint,et_port,et_addr;

    private WifiManager mWifiManager;   // 调用WiFi各种API的对象
    private Timer mTimer;   // 启动定时任务的对象
    boolean Connected = false,initflag1 = false;
    String name;
    String pos1;
    String pos2;
    String ipAddress,tempStrRX;
    int port;
    List<String> apData;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = getSharedPreferences("config1", Context.MODE_PRIVATE);

        findViews();
        setListener();

        //判断是否保存过
        if(sp.getBoolean("isSaved",false)){
            et_addr.setText(sp.getString("addr1","127.0.0.1"));
            et_port.setText(sp.getString("port1","8080"));
        }

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        getLocationAccessPermission();  // 先获取位置权限

        mTimer = new Timer();
        // 采样周期，以毫秒为单位
        int SAMPLE_RATE = 3000;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                scanWifi();
            }
        }, 0, SAMPLE_RATE);
        // 立即执行任务，每隔（int SAMPLE_RATE）执行一次WiFi扫描的任务
        // 扫描周期不能太快，WiFi扫描所有的AP需要一定时间
    }


    void getTextFromUI(){
        name = et_nameOfPoint.getText().toString();
        pos1 = et_POS1.getText().toString();
        pos2 = et_POS2.getText().toString();
    }
    void findViews(){
        lv1 = findViewById(R.id.lv1);

        btn_test = findViewById(R.id.btn_test);
        btn_send = findViewById(R.id.btn_send);
        btn_refresh = findViewById(R.id.btn_refresh);

        et_nameOfPoint = findViewById(R.id.et_nameOfPoint);
        et_POS1 = findViewById(R.id.et_POS1);
        et_POS2 = findViewById(R.id.et_POS2);
        et_port = findViewById(R.id.et_port);
        et_addr = findViewById(R.id.et_addr);
    }
    private void saveSP(){

        SharedPreferences.Editor editor = sp.edit();
        String string1 = et_addr.getText().toString();
        String string2 = et_port.getText().toString();
        if(!string1.isEmpty() && !string2.isEmpty()) {
            editor.putString("addr1", string1);
            editor.putString("port1", string2);
            editor.putBoolean("isSaved", true);
            editor.apply();
        }
    }


    private void setListener(){
        btn_test.setOnClickListener(v -> {
            saveSP();
            if(Connected){
                //断开连接时
//                deInitEasySocket();
                disconnect1();
                Connected = false;
                btn_test.setText("连接");
                ToastUtil.show(MainActivity.this,"已断开连接");

            } else {
                ipAddress =et_addr.getText().toString();
                port = Integer.parseInt(et_port.getText().toString()); // 获取端口号
                if(initflag1){
                    connect1();
                } else {
                    initEasySocket(ipAddress,port);
                }
//                Connected = true;
                btn_test.setText("断开");
//                ToastUtil.show(MainActivity.this,"已连接：" + ipAddress + ":" + port);
            }
        });
        btn_send.setOnClickListener(v -> {
            getTextFromUI();
            //TODO: 点完按钮将数据发送到服务端上
            if(Connected){
                String txText;
                //此处为点击发送按钮后发送的字符串
                if(!name.isEmpty() && !pos1.isEmpty() && !pos2.isEmpty()){
                    txText = "#START#\r\n"
                            + "\0name:" + name
                            + "\0POS1:" + pos1
                            + "\0POS2:" + pos2
                            + "\0";
                    sendText(txText);
                    for (int i = 0; i < apData.size(); i++) {
                        if(i < apData.size()-1){
                            sendText(apData.get(i) + "\0");
                        } else {
                            sendText(apData.get(i) + "\0\r\n#END#$");
                        }
                    }
                } else {
                    ToastUtil.show(this,"请先检查输入！");
                }

            } else {
                ToastUtil.show(this,"请先连接！");
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();    // 取消定时任务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
    }

    /**
     * socket行为监听
     */
    private final ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket连接成功
         * @param socketAddress 地址
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            LogUtil.w("端口" + socketAddress.getPort() + "---> 连接成功~~");
            if(socketAddress.getPort() == port){
                ToastUtil.show(MainActivity.this, socketAddress.getPort() + "端口" + "socket已连接");
                Connected = true;
                btn_test.setText("断开");
            }
        }

        /**
         * socket连接失败
         * @param socketAddress 地址
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketConnFail(SocketAddress socketAddress, boolean isNeedReconnect) {
            ToastUtil.show(MainActivity.this,"连接失败！！！");
            if(socketAddress.getPort() == port){
                Connected = false;
                btn_test.setText("连接");
            }
        }

        /**
         * socket断开连接
         * @param socketAddress 地址
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, boolean isNeedReconnect) {
            LogUtil.d(socketAddress.getPort() + "端口" + "---> socket断开连接，是否需要重连：" + isNeedReconnect);

            if(socketAddress.getPort() == port){
                Connected = false;
                btn_test.setText("连接");
            }


        }

        /**
         * socket接收的数据
         * @param socketAddress 地址
         * @param readData 收到的数据
         */
        @Override
        public void onSocketResponse(SocketAddress socketAddress, String readData) {
            LogUtil.d(socketAddress.getPort() + "端口" + "SocketActionListener收到数据-->" + readData);
            tempStrRX = readData;

            {
                //TODO:编写接收消息处理
            }
            tempStrRX = null;

        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            LogUtil.d(socketAddress.getPort() + "端口" + "SocketActionListener收到数据-->" + originReadData.getBodyString());
        }
    };

    private void initEasySocket(String ip,int portA) {
        //socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(ip, portA)) //主机地址
                .setConnectTimeout(100)
                .build();
        EasySocket.getInstance().setDebug(true);
        EasySocket.getInstance().createConnection(options,this);
        EasySocket.getInstance().connect();
        // 监听socket行为
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
        initflag1 = true;
    }
    //TODO:断开连接功能有问题，可能需要修改
    private void disconnect1(){

        EasySocket.getInstance().disconnect(ipAddress+":"+port,false);
        EasySocket.getInstance();
//        deInitEasySocket();
    }
    private void connect1(){
        EasySocket.getInstance().connect(ipAddress+":"+port);
    }


    private void deInitEasySocket(){

        EasySocket.getInstance().destroyConnection(ipAddress+":"+port);
        Connected = false;
        initflag1 = false;

    }

    /**
     * 通过  EasySocket.getInstance().upMessage(str.getBytes()) 发送消息
     * @param str 要发送的数据
     */
    void sendText(String str){
        //发送数据
        EasySocket.getInstance().upMessage(str.getBytes());

    }

    public void scanWifi() {
        // 如果WiFi未打开，先打开WiFi
        if (!mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(true);
        // 开始扫描WiFi
        mWifiManager.startScan();
        //扫描的结果
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling 还没有详细测试此处，请留意！！！
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //申请权限
            getLocationAccessPermission();
            return;
        }
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        LinkedList<ListViewData> scanResults0 = new LinkedList<>();
        List<ListViewData> list = new ArrayList<>();
        apData = new ArrayList<>();

        //把扫描结果中添加到list用于排序
        for (ScanResult sr : scanResults) {
            list.add(new ListViewData(sr.SSID,sr.level,sr.BSSID));
        }
        //按照rssi来排序
        list.sort(comparator);

        //把排序后的结果中添加到scanResults0用于显示
        for (ListViewData ls : list) {
            scanResults0.add(new ListViewData(ls.getSSID(),ls.getRSSI(),ls.getMac()));
            apData.add("{" + ls.getSSID() + "," + ls.getRSSI() +"}");
        }
        //通过主线程显示在listView
        runOnUiThread(() -> setListview(scanResults0));

    }

    //按照rssi来排序(小到大排序。)
    static Comparator<ListViewData> comparator = (p1, p2) -> Integer.compare(p2.getRSSI(), p1.getRSSI());

    /**
     * 增加开启位置权限功能，以适应Android 6.0及以上的版本
     */
    private void getLocationAccessPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * 用于listview显示
     * @param ld 要显示的数据 LinkedList<ListViewData>
     */
    private void setListview(LinkedList<ListViewData> ld){
        MyListviewAdapter myListviewAdapter = new MyListviewAdapter(ld,MainActivity.this);
        lv1.setAdapter(myListviewAdapter);
    }
}