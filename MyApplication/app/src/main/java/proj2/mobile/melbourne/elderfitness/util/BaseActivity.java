package proj2.mobile.melbourne.elderfitness.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 通过这个类让所有活动继承，来知道当前界面对应的是那个活动
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        // 将正在创建的活动添加到 ActivityCollector 里面
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 将一个马上要销毁的活动从 ActivityCollector 删除
        ActivityCollector.removeActivity(this);
    }
}