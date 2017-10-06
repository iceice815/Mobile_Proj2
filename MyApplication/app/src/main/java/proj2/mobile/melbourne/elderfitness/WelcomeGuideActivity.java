package proj2.mobile.melbourne.elderfitness;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import proj2.mobile.melbourne.elderfitness.adapter.GuideViewPagerAdapter;
import proj2.mobile.melbourne.elderfitness.util.SPUtils;

public class WelcomeGuideActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.vp_guide)
    ViewPager vp;
    @BindView(R.id.ll)
    LinearLayout ll;
    private static final int[] pics = {
            R.layout.guide_view1,
            R.layout.guide_view2,
            R.layout.guide_view3};
    private ImageView[] dots;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_guide);
        ButterKnife.bind(this);

        List<View> views = new ArrayList<>();
        // initial the guide pages list:
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);
            if (i == pics.length - 1) {
                Button startBtn = (Button) view.findViewById(R.id.btn_enter);
                startBtn.setTag("enter");
                startBtn.setOnClickListener(this);
            }
            views.add(view);
        }
        vp.setAdapter(new GuideViewPagerAdapter(views));
        vp.addOnPageChangeListener(new PageChangeListener());
        initDots(ll);
    }

    // initial the dot list:
    private void initDots(LinearLayout ll) {
        dots = new ImageView[pics.length];

        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if switch to background, it won't show guide pages next time:
        SPUtils.put(WelcomeGuideActivity.this, SPUtils.FIRST_OPEN, false);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().equals("enter")) {
            enterMainActivity();
            return;
        }
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    // set current view:
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vp.setCurrentItem(position);
    }

    // set current dot:
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    private void enterMainActivity() {
        SPUtils.put(WelcomeGuideActivity.this, SPUtils.FIRST_OPEN, false);
        startActivity(new Intent(WelcomeGuideActivity.this, Login.class));finish();
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int position) {
        }

        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            setCurDot(position);
        }
    }
}
