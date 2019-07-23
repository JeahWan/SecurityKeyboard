### MK-Keyboard - android 自定义键盘

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/jeahwan/maven/MK-Keyboard/images/download.svg) ](https://bintray.com/jeahwan/maven/MK-Keyboard/_latestVersion)

## 引用：

> implementation 'com.jeahwan.keyboard:MK-Keyboard:0.0.1'

## 使用步骤：

#### 0、初始化键盘样式（方便同产品内不同模块有不同键盘样式的需求，或者不同项目引用同一个库）

> SecurityKeyboard.setKeyboardStyle(SecurityKeyboard.STYLE);

#### 1、创建一个键盘

> SecurityKeyboard keyboard = new SecurityKeyboardBuilder(Context context).build();

#### 2、调用setType方法指定加载的键盘类型

> keyboard.setType(SecurityKeyboard.Type type);
>   - Standard：标准键盘类型（纯输入用，需绑定et）
>   - Extend：扩展键盘类型（含数字框，输入验证码、密码用）

#### 3、API介绍：

> setTitle(String title,int color,int fontSize)
>   - title：键盘显示的标题
>   - color：标题颜色
>   - fontSize：标题字号

> setKeyboardListener(KeyboardListener listener)
>   - show()：键盘显示时会回调的方法
>   - hide()：隐藏回调
>   - input(String s):键盘输入单个字符时的回调，已自动与et绑定
>   - del();点击删除键的回调，已自动与et绑定
>   - inputComplete(String str):键盘输入完成的回调，可得到六位数的字符串
>   - done();点击输入完成键的回调，一般直接调下一步按钮方法

> new KeyboardListener(SecurityKeyboard keyboard,EditText edit)
>   - 传入参数用于自动绑定keyboard与edittext的关联关系（EditText禁止弹出原生键盘、点击弹出小白键盘等会自动配置好），如扩展键盘不需要et的情况 需传入页面任一view用于键盘监听返回键
>   show(int millsenconds) 显示键盘，参数可选，用于延时打开键盘
>   - millsenconds：延时
>   dismiss(int millsenconds) 隐藏键盘，参数可选，用于延时隐藏键盘
>   -millsenconds：延时

       isShowing()

            - 返回键盘显隐状态

       showTip(String tip)

            - 显示提示信息

       hideTip()

            - 隐藏提示信息

       startAnim(boolean isSuccess,String info)

            - isSuccess：true为正确的对话、false为错误的动画

            - info：要显示的提示信息

       setAnimListener(LogoViewListener listener)

            - 动画监听，一般在此设置动画完成后要显示的文案及关闭键盘

       clearTextBox()

            - 清空文本框

       sendVcode()

            - 发送验证码，必须在验证码键盘下使用，可自动重置倒计时按钮（可发送情况下）

       setTenKey(String key)

            - 指定第十键的值，例如金额需要小数点 身份证需要X 必须在setType前调用

       setRedKey(String text, OnClickListener listener)

            - 指定红键的文本及事件

       setReSendOnClickListener(OnClickListener listener)

            - 设置重新发送按钮的事件

       setForgetPwdOnClickListener(OnClickListener listener)

            - 显示忘记密码并设置点击事件

       hideForgetPwd()

            - 隐藏忘记密码按钮

       showInput(boolean showInput)

            - 文本框已输入的字符是否明文显示，默认false

       showLoading()

            - 显示菊花转

       hideLoading()

            - 隐藏菊花转

       destroy(SecurityKeyboard... keyboards)

            - 静态方法，传入任意数量的键盘可自动释放

    4、扩展性：

       有新的键盘类型需求、逻辑需求，可在SecurityKeyboard中配置新的枚举并在setType(Type type)方法switch中增加新的case方案

       复杂的UI变动，可以在SecurityKeyboard类中配置新的STYLE枚举，并做相关的UI处理


#### 混淆：

```
####### RxJava RxAndroid ######

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontnote rx.internal.util.PlatformDependent

####### RxJava RxAndroid ######
```
