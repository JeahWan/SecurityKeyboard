package com.makise.mk_keyboard.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makise.mk_keyboard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 键盘的核心类
 * Created by Makise on 2016/8/8.
 */
public class SecurityKeyboard extends RelativeLayout implements View.OnClickListener {
    public TextView resend;
    public KeyboardListener listener;
    public boolean canSend;
    private Context context;
    private GridView gridView;    //用GrideView布局模拟键盘
    private ArrayList<Map<String, String>> valueList;    //Adapter适配需要 不能用数组
    private ArrayList<TextView> nums;
    private PopupWindow popWindow;
    private ImageView close, pb_loading, shouqi;
    private View view;
    private LogoView logoView;
    private TextView tip, errorText, title, findPwd, jdTips, doneKey;
    private RelativeLayout titleBar, error;
    private CountDownTimer timer;
    private StringBuffer inputNum;
    private boolean showTextBox, showRight, showError, showClose, showJDIcon;
    private LinearLayout text_box, jdSafeIcon, resultRL, rightLayout, delKey;

    private String tenkey;

    private Window window;
    private View rootView;
    private Type currentType;
    private Animation loadingAnim;
    //键盘是否可输入
    private boolean canInput;
    //是否隐藏已输入的字符
    private boolean showInput;
    private boolean tenkeyIsChar;
    //GrideView的适配器
    BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return valueList.size();
        }

        @Override
        public Object getItem(int position) {
            return valueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(context, R.layout.item_keyboard, null);
                viewHolder.btnKey = convertView.findViewById(R.id.btn_keys);
                //根据style设置 修改样式
                switch (keyboardStyle) {
                    case STYLE_1:
                        viewHolder.btnKey.setTextColor(Color.parseColor("#434a59"));
                        break;
                    case STYLE_2:
                        viewHolder.btnKey.setTextColor(Color.parseColor("#B0B0BF"));
                        viewHolder.btnKey.setTextSize(18);

                        ViewGroup.LayoutParams params = viewHolder.btnKey.getLayoutParams();
                        params.height = dp2px(52);
                        viewHolder.btnKey.setLayoutParams(params);
                        break;
                }
                if (position == 11) {
                    viewHolder.ll_img = convertView.findViewById(R.id.ll_img);
                    viewHolder.btnImg = convertView.findViewById(R.id.btn_img);
                    //根据style设置 修改样式
                    switch (keyboardStyle) {
                        case STYLE_1:
                            viewHolder.btnImg.setImageResource(R.mipmap.icon_load_del);
                            break;
                        case STYLE_2:
                            viewHolder.btnImg.setVisibility(INVISIBLE);
                            viewHolder.btnImg.setClickable(false);

                            ViewGroup.LayoutParams params = viewHolder.ll_img.getLayoutParams();
                            params.height = dp2px(52);
                            viewHolder.ll_img.setLayoutParams(params);
                            break;
                    }
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (!showRight) {
                viewHolder.btnKey.setWidth(0);
            }
            viewHolder.btnKey.setText(valueList.get(position).get("name"));
            if (position == 9) {
                viewHolder.btnKey.setTextSize(12);
                switch (keyboardStyle) {
                    case STYLE_2:
                        viewHolder.btnKey.setTextSize(18);
                        break;
                }
                if (tenkeyIsChar) {
                    viewHolder.btnKey.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //拿到输入结果
                            String currentNum = valueList.get(position).get("name");
                            if (currentNum.equals("●") || currentNum.equals("·"))
                                currentNum = ".";
                            listener.input(currentNum);
                        }
                    });
                } else {
                    viewHolder.btnKey.setBackgroundResource(R.drawable.selector_key_del);
                    viewHolder.btnKey.setEnabled(false);
                    viewHolder.btnKey.setTextColor(Color.parseColor("#999999"));
                }
            }
            if (position == 11) {
                viewHolder.btnKey.setVisibility(GONE);
                viewHolder.ll_img.setVisibility(VISIBLE);
                viewHolder.ll_img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        del();
                    }
                });
            }

            //给数字键盘0-9设置的点击事件
            if (position < 11 && position != 9) {
                viewHolder.btnKey.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (canInput) {
                            //拿到数字
                            String currentNum = valueList.get(position).get("name");
                            listener.input(currentNum);
                            if (showTextBox) {
                                //给输入框添加圆点
                                for (final TextView num : nums) {
                                    if (num.getVisibility() == INVISIBLE) {
                                        //是否显示已输入的字符
                                        if (!showInput) {
                                            //需要延时变化时 在这里先设置正常数字 再延时变化
                                            num.setTextSize(12);
                                            num.setText("●");
                                        } else {
                                            num.setTextSize(24);
                                            num.setText(currentNum);
                                        }
                                        num.setVisibility(VISIBLE);
                                        break;
                                    }
                                }
                                //不足6个数时，累加
                                if (inputNum.length() < 6)
                                    inputNum.append(currentNum);

                                //满6个数传入监听
                                if (inputNum.length() == 6) {
                                    listener.inputComplete(inputNum.toString());
                                }
                            }
                        }
                    }
                });
            }
            return convertView;
        }
    };

    public SecurityKeyboard(Context context) {
        this(context, null);
    }

    public SecurityKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        view = View.inflate(context, R.layout.keyboard, null);

        valueList = new ArrayList<>();

        gridView = view.findViewById(R.id.gv_keybord);

        logoView = view.findViewById(R.id.logo_view);

        resultRL = view.findViewById(R.id.result);

        //右侧布局及按钮
        rightLayout = view.findViewById(R.id.right);
        delKey = view.findViewById(R.id.del);
        doneKey = view.findViewById(R.id.done);
        delKey.setOnClickListener(this);
        doneKey.setOnClickListener(this);

        //顶部
        titleBar = view.findViewById(R.id.title_bar);
        error = view.findViewById(R.id.error);
        jdSafeIcon = view.findViewById(R.id.safe_icon);

        tip = view.findViewById(R.id.tv_tip);
        errorText = view.findViewById(R.id.tv_error);
        shouqi = view.findViewById(R.id.iv_shouqi);
        title = view.findViewById(R.id.title);
        resend = view.findViewById(R.id.reSend);
        findPwd = view.findViewById(R.id.tv_find_pwd);
        jdTips = view.findViewById(R.id.tv_safe_tips);

        pb_loading = view.findViewById(R.id.pb_loading);

        shouqi.setOnClickListener(this);

        inputNum = new StringBuffer();

        text_box = view.findViewById(R.id.text_box);

        //根据style设置 修改样式
        switch (keyboardStyle) {
            case STYLE_1:
                shouqi.setImageResource(R.mipmap.icon_load_shou);
                break;
            case STYLE_2:
                //显示右侧删除键和确定按钮
                rightLayout.setVisibility(VISIBLE);
                //替换中间提示文案的样式
                jdTips.setTextSize(14);
                jdTips.setTextColor(Color.parseColor("#B0B0BF"));
                jdTips.setCompoundDrawablePadding(dp2px(8));
                jdTips.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icon_dun), null, null, null);
                ViewGroup.LayoutParams params = jdSafeIcon.getLayoutParams();
                params.height = dp2px(44);
                jdSafeIcon.setLayoutParams(params);
                break;
        }

        //加载中动画初始化
        loadingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_loading);
        loadingAnim.setInterpolator(new LinearInterpolator());

        timer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                resend.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                resend.setEnabled(true);
                resend.setText("重新发送");
                canSend = true;
            }
        };

        close = (ImageView) view.findViewById(R.id.close);

        addView(view);      //必须要，不然不显示控件
    }

    /**
     * 释放键盘
     */
    public static void destroy(SecurityKeyboard... keyboards) {
        for (SecurityKeyboard keyboard : keyboards) {
            if (keyboard != null) {
                keyboard.dismiss();
                keyboard = null;
            }
        }
    }

    public void setTitle(String title) {
        setTitle(title, 0, 0);
    }

    public void setTitle(String title, int color) {
        setTitle(title, color, 0);
    }

    public void setTitle(String title, int color, int fontSize) {
        if (!TextUtils.isEmpty(title))
            this.title.setText(title);
        if (color != 0)
            this.title.setTextColor(color);
        if (fontSize != 0)
            this.title.setTextSize(fontSize);
    }

    /**
     * 设置中间文案显示内容
     *
     * @param tips
     */
    public void setMiddleTips(String tips) {
        if (TextUtils.isEmpty(tips)) {
            jdSafeIcon.setVisibility(GONE);
        } else {
            jdTips.setText(tips);
            jdSafeIcon.setVisibility(VISIBLE);
        }
    }

    public void setReSendOnClickListener(OnClickListener listener) {
        this.resend.setOnClickListener(listener);
    }

    public void setAnimListener(LogoView.LogoViewListener listener) {
        logoView.setListener(listener);
    }

    public void setData(SecurityKeyboardBuilder securityKeyboardBuilder, Context context, View rootView) {
        this.popWindow = securityKeyboardBuilder;
        this.context = context;
        this.rootView = rootView;
        this.window = ((Activity) context).getWindow();
    }

    public boolean isShowing() {
        return popWindow.isShowing();
    }

    public void setKeyboardListener(KeyboardListener listener) {
        this.listener = listener;
    }

    private void setView() {
        /* 初始化按钮上应该显示的数字 */
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<String, String>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", tenkey);
            } else if (i == 12) {
                map.put("name", showRight ? "收起" : "<");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            }
            valueList.add(map);
        }
        close.setOnClickListener(this);
        nums = new ArrayList<>();
        canSend = true;

        nums.add((TextView) view.findViewById(R.id.num1));
        nums.add((TextView) view.findViewById(R.id.num2));
        nums.add((TextView) view.findViewById(R.id.num3));
        nums.add((TextView) view.findViewById(R.id.num4));
        nums.add((TextView) view.findViewById(R.id.num5));
        nums.add((TextView) view.findViewById(R.id.num6));

        gridView.setAdapter(adapter);
    }

    public void clearTextBox() {
        for (TextView num : nums) {
            num.setVisibility(INVISIBLE);
        }
        inputNum.delete(0, inputNum.length());
        canInput = true;
    }

    public void sendVcode() {
        if (canSend) {
            Toast.makeText(context, "短信验证码已发送", Toast.LENGTH_SHORT).show();
            resend.setVisibility(VISIBLE);
            timer.cancel();
            timer.start();
            canSend = false;
            resend.setEnabled(false);
        }
    }

    /**
     * 获取输入区域键盘高度
     *
     * @return
     */
    public void getKeyboardHeight(final KeyboardHeightListener listener) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                listener.getHeight(rootView.getHeight() - rootView.findViewById(R.id.top_shadow).getMeasuredHeight());
            }
        });
    }

    public interface KeyboardHeightListener {
        void getHeight(int height);
    }

    /**
     * 执行动画
     */
    public void startAnim(final boolean isSuccess, String info) {
        //执行动画的时候不允许输入
        canInput = false;
        errorText.setText("");
        errorText.setVisibility(INVISIBLE);
        //隐藏发送验证码按钮
        resend.setVisibility(INVISIBLE);
        if (!isSuccess) info += "，请重试";
        final String tipText = info;
        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        trans.setDuration(500);
        trans.setFillAfter(true);
        final TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        translate.setDuration(500);

        gridView.startAnimation(trans);
        gridView.setVisibility(INVISIBLE);
        trans.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                resultRL.setVisibility(VISIBLE);
                logoView.startAnimation(translate);
                translate.setAnimationListener(new AnimListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        super.onAnimationStart(animation);
                        logoView.start(isSuccess);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        super.onAnimationEnd(animation);
                        tip.setText(tipText);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close || view.getId() == R.id.iv_shouqi || view.getId() == R.id.done) {
            //收起键盘
            popWindow.dismiss();
        } else if (view.getId() == R.id.del) {
            del();
        }
    }

    /**
     * 延时打开键盘
     */
    public void show(int milliseconds) {
        if (context instanceof Activity && ((Activity) context).isFinishing()) return;
        RxTask.doInUIThreadDelay(new RxTask.UITask() {
            @Override
            public void doInUIThread() {
                if (!popWindow.isShowing()) {
                    popWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                    //必须在setlistener之后调用show才会成功回调键盘的show监听
                    if (listener != null)
                        listener.show();
                    if (currentType == Type.Extend) {
                        RxTask.doInUIThreadDelay(new RxTask.UITask() {
                            @Override
                            public void doInUIThread() {
                                WindowManager.LayoutParams lp = window.getAttributes();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                lp.alpha = 0.5f;
                                window.setAttributes(lp);
                            }
                        }, 100, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }, milliseconds, TimeUnit.MILLISECONDS);
    }

    public void show() {
        show(50);
    }

    /**
     * 直接关闭键盘
     */
    public void dismiss() {
        if (popWindow != null && popWindow.isShowing())
            popWindow.dismiss();
    }

    /**
     * 延迟关闭键盘
     *
     * @param millseconds
     */
    public void dismiss(int millseconds) {
        RxTask.doInUIThreadDelay(new RxTask.UITask() {
            @Override
            public void doInUIThread() {
                popWindow.dismiss();
            }
        }, millseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 根据枚举来配置相应的键盘
     *
     * @param type
     */
    public void setType(Type type) {
        int rlHeight = 0;
        int color = 0;
        this.currentType = type;
        switch (type) {
            case Standard:
                rlHeight = 40;
                color = Color.parseColor("#ffffff");
                this.showTextBox = false;
                this.showRight = true;
                this.showError = false;
                this.showClose = false;
                this.showJDIcon = false;

                rootView.findViewById(R.id.bg).setVisibility(GONE);
                rootView.findViewById(R.id.top_corner).setVisibility(GONE);
                rootView.findViewById(R.id.top_shadow).setVisibility(VISIBLE);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //键盘隐藏状态的回调
                        listener.hide();
                    }
                });
                break;
            case Extend:
                rlHeight = 50;
                color = Color.parseColor("#eeeeee");
                this.showTextBox = true;
                this.showRight = false;
                this.showError = true;
                this.showClose = true;
                this.showJDIcon = true;

                rootView.findViewById(R.id.close).setPadding(dp2px(16), 0, dp2px(16), dp2px(6));
                rootView.findViewById(R.id.title).setPadding(0, 0, 0, dp2px(6));
                rootView.findViewById(R.id.reSend).setPadding(0, 0, 0, dp2px(6));

                rootView.findViewById(R.id.bg).setVisibility(VISIBLE);
                rootView.findViewById(R.id.top_corner).setVisibility(VISIBLE);
                rootView.findViewById(R.id.top_shadow).setVisibility(GONE);
                rootView.findViewById(R.id.iv_shouqi).setVisibility(GONE);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = window.getAttributes();
                        lp.alpha = 1f;
                        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        window.setAttributes(lp);
                        listener.hide();
                    }
                });
                break;
        }
        popWindow.setBackgroundDrawable(new BitmapDrawable());// 这样设置才能点击屏幕外dismiss窗口
        //可输入
        canInput = true;

        //改变RL高度
        ViewGroup.LayoutParams params = titleBar.getLayoutParams();
        params.height = dp2px(rlHeight);
        titleBar.setLayoutParams(params);

        ViewGroup.LayoutParams params2 = error.getLayoutParams();
        params2.height = dp2px(rlHeight);
        error.setLayoutParams(params2);

        //设置颜色
        titleBar.setBackgroundColor(color);

        //各控件显隐
        text_box.setVisibility(showTextBox ? VISIBLE : GONE);
        error.setVisibility(showError ? VISIBLE : GONE);
        close.setVisibility(showClose ? VISIBLE : GONE);
        jdSafeIcon.setVisibility(showJDIcon ? VISIBLE : GONE);
        setView();
    }

    /**
     * 退格键的方法
     */
    public void del() {
        listener.del();
        //不为零时减一
        if (showTextBox) {
            if (inputNum.length() > 0) {
                inputNum = inputNum.deleteCharAt(inputNum.length() - 1);
            }
            for (int i = nums.size() - 1; i >= 0; i--) {
                TextView num = nums.get(i);
                if (num.getVisibility() == View.VISIBLE) {
                    num.setVisibility(View.INVISIBLE);
                    break;
                }
            }
        }
    }

    /**
     * 显示忘记密码并设置点击事件
     *
     * @param listener
     */
    public void setForgetPwdOnClickListener(OnClickListener listener) {
        this.findPwd.setVisibility(VISIBLE);
        this.findPwd.setOnClickListener(listener);
    }

    /**
     * 隐藏忘记密码按钮
     */
    public void hideForgetPwd() {
        this.findPwd.setVisibility(GONE);
        this.findPwd.setOnClickListener(null);
    }

    /**
     * 设置tip显示文本
     *
     * @param tip
     */
    public void showTip(String tip) {
        hideLoading();
        this.errorText.setText(tip);
        this.errorText.setVisibility(VISIBLE);
    }

    /**
     * 隐藏tip
     */
    public void hideTip() {
        this.errorText.setText("");
        this.errorText.setVisibility(INVISIBLE);
    }

    public void showLoading() {
        //显示菊花转
        pb_loading.setVisibility(VISIBLE);
        pb_loading.startAnimation(loadingAnim);
        errorText.setVisibility(GONE);
        findPwd.setVisibility(GONE);
        //禁止输入
        canInput = false;
    }

    public void hideLoading() {
        //隐藏
        pb_loading.setVisibility(GONE);
        pb_loading.clearAnimation();
        //允许输入
        canInput = true;
    }

    /**
     * 设置第十个键
     *
     * @param key
     */
    public void setTenKey(String key, boolean tenkeyIsChar) {
        this.tenkey = key;
        this.tenkeyIsChar = tenkeyIsChar;
    }

    /**
     * 是否显示输入的字符 默认为false 不显示
     *
     * @param showInput
     */
    public void showInput(boolean showInput) {
        this.showInput = showInput;
    }

    public void hideTitle() {
        titleBar.setVisibility(GONE);
    }

    public enum Type {
        Standard, Extend
    }

    //dp转px
    public int dp2px(int dp) {
        if (context == null) return -1;
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public interface KeyboardListener {
        void input(String s);

        void del();

        void done();

        void inputComplete(String str);

        void show();

        void hide();
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView btnKey;
        public ImageView btnImg;
        public LinearLayout ll_img;
    }

    public enum STYLE {
        STYLE_1, STYLE_2
    }

    public static STYLE keyboardStyle;

    public static void setKeyboardStyle(STYLE style) {
        keyboardStyle = style;
    }
}