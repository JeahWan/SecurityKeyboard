package com.jeahwan.keyboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText et1;
    private EditText et2;
    private SecurityKeyboard keyboard, keyboard2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = findViewById(R.id.et_1);
        et2 = findViewById(R.id.et_2);

//        et2.addTextChangedListener(new text);

//        et2.addTextChangedListener(() {
//            @Override
//            public void afterTextChanged(Editable editable) {
//                keyboard2.sendVcode();
//            }
//        });

        SecurityKeyboard.setKeyboardStyle(SecurityKeyboard.STYLE.STYLE_2);

        keyboard = new SecurityKeyboardBuilder(this).build();
        keyboard.setTenKey("●",true);
        keyboard.setType(SecurityKeyboard.Type.Standard);
        keyboard.setTitle("请输入金额");
        keyboard.setKeyboardListener(new KeyboardListener(keyboard, et1) {
        });

        keyboard2 = new SecurityKeyboardBuilder(this).build();
        keyboard2.setType(SecurityKeyboard.Type.Extend);
        keyboard2.setTitle("请输入交易密码");
        keyboard2.setKeyboardListener(new KeyboardListener(keyboard2, et2) {
        });
    }
}
