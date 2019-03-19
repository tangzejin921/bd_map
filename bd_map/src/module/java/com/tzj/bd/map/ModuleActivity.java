package com.tzj.bd.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.LocationListener;

import java.util.Map;


public class ModuleActivity extends Activity implements View.OnClickListener {
    private TextView result;
    private UtilGPS utilGPS;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onReceiveLocation(Map<String, Object> map) {
            result.append(map.toString());
            result.append("\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        result = findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());
        utilGPS = new UtilGPS(this);
        utilGPS.addLocationListener(getClass().getSimpleName(), listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map:
                startActivity(new Intent(this, MapActivity.class));
                break;
            case R.id.button:
                // todo 权限
                utilGPS.getLocation(listener);
                break;
            case R.id.button2:
                // todo 权限
                utilGPS.start(5000);
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utilGPS.clear();
        Log.e("test", "=====================");
    }
}
