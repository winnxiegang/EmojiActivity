package com.operations.winsky.emojiactivity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import emoji.EmojAdapter;
import emoji.EmojBean;
import emoji.EmojUtils;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.circles)
    ViewPagerIndicator circles;
    @Bind(R.id.emojLayout)
    LinearLayout emojLayout;
    ArrayList<View> viewList;
    List<List<EmojBean>> list;
    EmojiPagerAdapter adapter;
    @Bind(R.id.inputCommentEt)
    EmojiEditTextView inputCommentEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        intView();
        initData();
    }

    private void intView() {
        viewList = new ArrayList<>();
        list = EmojUtils.getEmojList(this);
        adapter = new EmojiPagerAdapter(viewList);
    }

    protected void initData() {
        for (int i = 0; i < list.size(); i++) {
            View gridView = createGridView((ArrayList<EmojBean>) list.get(i));
            viewList.add(gridView);
        }
        viewPager.setAdapter(adapter);
        circles.setViewPager(viewPager);
    }

    private View createGridView(final ArrayList<EmojBean> list) {
        //GridView gridView = new GridView(getContext());
        View view = View.inflate(this, R.layout.emoj_lib_item_view, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        //gridView.setNumColumns(7);
        EmojAdapter mAdapter = new EmojAdapter(this, list);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= list.size()) {
                    if (position != 20) {
                        return;
                    }
                    inputCommentEt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else {
                    inputCommentEt.insertEmoj(list.get(position));
                }

            }
        });
        return view;
    }

    class EmojiPagerAdapter extends PagerAdapter {
        private ArrayList<View> list;

        public EmojiPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }
    public static boolean isEmoji(String string) {
        Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }
}
/**
 * 表情可以使用接口的形式 ，让别人继承 你在这里调用
 */
//public interface InputCallback {
//    void onEmojAdd(EmojBean emojBean);
//
//    void onEmojDelete();
//
//    void onBubbleSelected(String fontColor);
//
//    void onFansMedalSelected(UserMedalBean userMedalBean);
//}


