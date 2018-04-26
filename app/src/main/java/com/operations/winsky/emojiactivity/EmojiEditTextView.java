package com.operations.winsky.emojiactivity;

/**
 * Name: EmojiEditTextView
 * Author: xieganag
 * Email:
 * Comment: //TODO
 * Date: 2018-04-26 09:55
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emoji.EmojBean;

/**
 * Created by mrs on 2017/4/10.
 */

public class EmojiEditTextView extends android.support.v7.widget.AppCompatEditText implements TextWatcher {
    private textChangedListener listener;
    @EmojiType
    private int emojiType = EmojiType.ORIGINAL;
    private int chatWords = Integer.MAX_VALUE;

    public EmojiEditTextView(Context context) {
        super(context);
    }

    public EmojiEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addOnTextChangedListener(textChangedListener listener1) {
        this.listener = listener1;
        addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (MainActivity.isEmoji(s.toString())) {//实现只能输入自定义的表情 屏蔽系统输入法的表情包
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            return;
        }
        if (listener != null)
            listener.onTextChanged(s, start, before, count);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public interface textChangedListener {
        void onTextChanged(CharSequence s, int start, int count, int after);
    }

    public void removeTextChangedListener() {
        this.listener = null;
        removeTextChangedListener(this);
    }

    public void insertEmoj(EmojBean emoji) {
        if (emoji != null && emoji.getMessage() == null) {
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            return;
        }
        Editable editable = getText();
        int index = getSelectionStart();
        if (chatWords < editable.length() + emoji.getDrawableName().length()) {
            return;
        }
        Drawable drawable = getDrawableByEmojiType(emoji.getResId());
        SpannableString sp = new SpannableString(emoji.getDrawableName());
        if (drawable != null)
            sp.setSpan(new CenterVerticalImageSpan(drawable), 0, sp.length(), ImageSpan.ALIGN_BASELINE);
        editable.insert(index, sp);
    }

    public void setEmojiTxt(String emojiTxt, @EmojiType int emojiType) {
        if (TextUtils.isEmpty(emojiTxt))
            return;
        this.emojiType = emojiType;
        setEmojiTxt(emojiTxt);
    }

    public void setEmojiTxt(String emojiTxt) {
        if (TextUtils.isEmpty(emojiTxt))
            return;
        Editable editable = getText();
        int index = getSelectionStart();
        if (chatWords < editable.length() + emojiTxt.length()) {
            return;
        }
        Pattern pattern = Pattern.compile("\\[[^\\[\\]]*\\]");
        Matcher matcher = pattern.matcher(emojiTxt);
        SpannableString sp = new SpannableString(emojiTxt);
        String packageName = getContext().getPackageName();
        while (matcher.find()) {
            String group = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            group = group.substring(1, group.length() - 1);
            int resid = getContext().getResources().getIdentifier("emoj_" + group, "drawable", packageName);
            if (resid != 0) {
                Drawable drawable = getDrawableByEmojiType(resid);
                sp.setSpan(new CenterVerticalImageSpan(drawable), start, end, ImageSpan.ALIGN_BASELINE);
            }
        }
        editable.insert(index, sp);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @IntDef({EmojiType.ORIGINAL, EmojiType.SCALE_HALF, EmojiType.SCALE_WITH_TXT})
    public @interface EmojiType {
        int ORIGINAL = 0;//保持原大小
        int SCALE_WITH_TXT = 1;//缩小到跟文本大小一致
        int SCALE_HALF = 2;//缩小到原来的一半
    }

    private Drawable getDrawableByEmojiType(int resid) {
        Drawable drawable = null;
        try {
            drawable = ContextCompat.getDrawable(getContext(), resid);
        } catch (Resources.NotFoundException exception) {
            return null;
        }
        if (drawable == null)
            return null;

        if (emojiType == EmojiType.ORIGINAL) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else if (emojiType == EmojiType.SCALE_HALF) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
        } else if (emojiType == EmojiType.SCALE_WITH_TXT) {
            drawable.setBounds(0, 0, (int) getTextSize(), (int) getTextSize());
        }
        return drawable;
    }

    public void setMaxLength(int chatWords) {
        this.chatWords = chatWords;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(chatWords), inputFilter});
    }

    InputFilter inputFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                return "";
            }
            return null;
        }
    };
}
