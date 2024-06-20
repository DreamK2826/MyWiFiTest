package com.dreamk.mywifitest;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//主要参考资料：https://blog.csdn.net/gwplovekimi/article/details/106015415
//https://blog.csdn.net/gwplovekimi/article/details/106015415
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1000;

    ListView lv1;
    Button btn_test,btn_send,btn_refresh;
    EditText et_POS1,et_POS2,et_nameOfPoint;

    private WifiManager mWifiManager;   // 调用WiFi各种API的对象
    private Timer mTimer;   // 启动定时任务的对象

    String name;
    String pos1;
    String pos2;

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
        findViews();
        setListener();

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        getLocationAccessPermission();  // 先获取位置权限

        mTimer = new Timer();
        // 采样周期，以毫秒为单位
        int SAMPLE_RATE = 10000;
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
    }

    private void setListener(){
        btn_test.setOnClickListener(v -> {});
        btn_send.setOnClickListener(v -> {
            getTextFromUI();
            //TODO: 点完按钮将数据发送到服务端上

        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();    // 取消定时任务
    }

    public void scanWifi() {
        // 如果WiFi未打开，先打开WiFi
        if (!mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(true);
        // 开始扫描WiFi
        mWifiManager.startScan();
        //扫描的结果
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling 还没有测试此处，请留意！！！
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

        //把扫描结果中添加到list用于排序
        for (ScanResult sr : scanResults) {
            list.add(new ListViewData(sr.SSID,sr.level,sr.BSSID));
        }
        //按照rssi来排序
        list.sort(comparator);

        //把排序后的结果中添加到scanResults0用于显示
        for (ListViewData ls : list) {

            scanResults0.add(new ListViewData(ls.getSSID(),ls.getRSSI(),ls.getMac()));
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
     * @param ld 要显示的数据
     */
    private void setListview(LinkedList<ListViewData> ld){
        MyListviewAdapter myListviewAdapter = new MyListviewAdapter(ld,MainActivity.this);
        lv1.setAdapter(myListviewAdapter);
    }
}