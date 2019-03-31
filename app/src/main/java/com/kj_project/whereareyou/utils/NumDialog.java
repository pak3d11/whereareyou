package com.kj_project.whereareyou.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;

import com.kj_project.whereareyou.R;

public class NumDialog extends Dialog{

    private static final int LAYOUT = R.layout.num_dialog;
    private NumDialogListener dialogListener;

    private TextInputEditText inputNum;
    private Button btnDialogCancel;
    private Button btnDialogSave;
    private String number;
    private Context context;
    private SettingUtil setting;

    public NumDialog(Context context){
        super(context);
        this.context = context;
        this.setting = new SettingUtil(this.context);
    }

    public void setDialogListener(NumDialogListener numDialogListener){
        this.dialogListener = numDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        inputNum = findViewById(R.id.inputNum);
        btnDialogCancel = findViewById(R.id.btn_dialog_cancel);
        btnDialogSave = findViewById(R.id.btn_dialog_save);

        number = setting.getPhoneNumber();
        if(!number.equals("")){
            inputNum.setText(number);
        }

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        btnDialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = String.valueOf(inputNum.getText());
                setting.delPhoneNumber();
                setting.setPhoneNumber(number);
                dialogListener.onPositiveClick(number);
                dismiss();
            }
        });
    }
}
