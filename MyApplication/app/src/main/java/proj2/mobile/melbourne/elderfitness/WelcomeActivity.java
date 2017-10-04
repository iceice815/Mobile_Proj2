package proj2.mobile.melbourne.elderfitness;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import proj2.mobile.melbourne.elderfitness.util.SPUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WelcomeActivity extends Activity {

    @BindView(R.id.iv_entry)
    ImageView mIVEntry;

    private static final int ANIM_TIME = 2000;
    private static final float SCALE_END = 1.15F;
    private static final int[] Imgs={
            R.drawable.welcomimg1,R.drawable.welcomimg2,
            R.drawable.welcomimg3,R.drawable.welcomimg4,
            R.drawable.welcomimg5, R.drawable.welcomimg6,
            R.drawable.welcomimg7,R.drawable.welcomimg8,
            R.drawable.welcomimg9,R.drawable.welcomimg10,
            R.drawable.welcomimg11,R.drawable.welcomimg12};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        startMainActivity();
    }

    private void startMainActivity(){

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        startAnim();
                    }
                });
    }

    private void startAnim() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mIVEntry, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mIVEntry, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                boolean isFirstOpen = (boolean) SPUtils.get(WelcomeActivity.this,
                        SPUtils.FIRST_OPEN, true);
                if(isFirstOpen) {
                    startActivity(new Intent(WelcomeActivity.this, WelcomeGuideActivity.class));
                    WelcomeActivity.this.finish();
                } else {
                    startActivity(new Intent(WelcomeActivity.this, Login.class));
                    WelcomeActivity.this.finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode==KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }
}
