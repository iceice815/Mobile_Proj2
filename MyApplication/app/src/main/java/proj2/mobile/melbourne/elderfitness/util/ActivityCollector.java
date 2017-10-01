package proj2.mobile.melbourne.elderfitness.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * ActivityCollector 是一个活动管理器
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    /**
     * addActivity 用于向 List 添加一个活动
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * removeActivity 用于从 List 中删除一个活动
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 调用 ActivityCollector.finishAll 方法来退出程序
     */
    public static void finishAll(){
        for (Activity activity : activities){
            activity.finish();
        }
    }
}
