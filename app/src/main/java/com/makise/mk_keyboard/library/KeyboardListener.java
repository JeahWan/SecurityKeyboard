package com.makise.mk_keyboard.library;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


/**
 * 键盘监听
 * Created by Makise on 2016/8/20.
 */
public abstract class KeyboardListener implements SecurityKeyboard.KeyboardListener {
    private SecurityKeyboard mKeyboard;
    private EditText mEditText;
    private View mView;

    //需要0开头
    private boolean needZeroBeginning;

    public KeyboardListener(SecurityKeyboard keyboard, View view) {
        mKeyboard = keyboard;
        mView = view;
        if (view != null && view instanceof EditText) {
            mEditText = (EditText) view;
            //显示键盘
            mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mEditText.requestFocus();
                        //显示光标
                        mEditText.setCursorVisible(true);
                        mKeyboard.show();
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 标识需要0开头
     *
     * @return
     */
    public KeyboardListener needZeroBeginning() {
        this.needZeroBeginning = true;
        return this;
    }

    /**
     * 输入的监听
     *
     * @param s
     */
    @Override
    public void input(String s) {
        if (mEditText != null) {
            //给et追加字符
            String str = mEditText.getText().toString();
            switch (s) {
                //小数点的处理
                case ".":
                    //不允许第一位输入
                    if (str.length() == 0) return;
                    //不允许输入多次
                    if (str.contains(".")) return;
                    //不允许输入0.
//                    if (str.length() == 1 && "0".equals(str)) return;
                    break;
                //X的处理
                case "X":
                    //输入身份证时 只允许最后一位输入X
                    if (str.length() < 19) return;
                    //不允许多个X
                    if (str.contains("X")) return;
                    break;
            }

            //只允许两位小数
            if (str.length() > 3 && ".".equals(str.substring(str.length() - 3, str.length() - 2)))
                return;

            //不以0开头
            if (!needZeroBeginning && "0".equals(str) && !".".equals(s)) str = "";

            str += s;
            mEditText.setText(str);
            //设置光标位置
            mEditText.setSelection(mEditText.length());
        }
    }

    /**
     * 删除键
     */
    @Override
    public void del() {
        //判断et中不为空就删除一位
        if (mEditText != null) {
            String str = mEditText.getText().toString();
            if (mEditText.length() != 0) {
                String str1;
                if (str.length() >= 2 && " ".equals(str.substring(str.length() - 2, str.length() - 1))) {
                    //空格连续删除
                    str1 = str.substring(0, str.length() - 2);
                } else {
                    //如果前一位是小数点 直接删除
                    if (mEditText.getText().toString().substring(0, str.length() - 1).endsWith(".")) {
                        str1 = str.substring(0, str.length() - 2);
                    } else {
                        str1 = str.substring(0, str.length() - 1);
                    }
                }
                mEditText.setText(str1);
            }
            //设置光标位置
            mEditText.setSelection(mEditText.length());
        }
    }

    /**
     * 按下输入完成键
     */
    @Override
    public void done() {
        //输入完成 隐藏键盘
        mKeyboard.dismiss();
    }

    /**
     * 输入完成
     *
     * @param str
     */
    @Override
    public void inputComplete(String str) {
    }

    /**
     * 键盘显示
     */
    @Override
    public void show() {
        //显示光标
        if (mEditText != null)
            mEditText.setCursorVisible(true);
        //使view监听返回键隐藏键盘
        mView.setFocusable(true);
        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                    if (mEditText != null && mKeyboard.isShowing()) {
                        //普通输入型键盘 按返回键时关闭键盘
                        mKeyboard.dismiss();
                    }
                    //扩展型键盘不做处理 相当于禁用返回键
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 键盘隐藏
     */
    @Override
    public void hide() {
        //隐藏光标
        if (mEditText != null)
            mEditText.setCursorVisible(false);
        //键盘隐藏后清空提示信息和输入框
        if (mKeyboard != null) {
            mKeyboard.hideTip();
            mKeyboard.clearTextBox();
        }
        //去掉onkey监听
        mView.setOnKeyListener(null);
    }
}
