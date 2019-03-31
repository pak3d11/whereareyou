package com.kj_project.whereareyou.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kj_project.whereareyou.R;
import com.kj_project.whereareyou.utils.NumDialog;
import com.kj_project.whereareyou.utils.NumDialogListener;
import com.kj_project.whereareyou.utils.SettingUtil;

public class SettingFragment extends Fragment{

    SettingUtil setting;
    Context con;
    EditText number;
    Button btnEditNumber;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState){

        con = getActivity();
        setting = new SettingUtil(con);

        view = inflater.inflate(R.layout.fmt_setting, parentViewGroup, false);
        number = view.findViewById(R.id.number);
        btnEditNumber = view.findViewById(R.id.btn_edit_number);

        if (!setting.getPhoneNumber().equals("")) number.setText(setting.getPhoneNumber());

        btnEditNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //@TODO 전화번호 입력받는 Dialog 출력
                NumDialog numDialog = new NumDialog(con);
                numDialog.setDialogListener(new NumDialogListener() {
                    @Override
                    public void onPositiveClick(String number) {
                        setResult(number);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                numDialog.show();
            }
        });

        return view;
    }

    public void setResult(String num){
        number.setText(num);
    }

}
