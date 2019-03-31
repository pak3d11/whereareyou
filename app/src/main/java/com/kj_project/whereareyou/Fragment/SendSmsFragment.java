package com.kj_project.whereareyou.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kj_project.whereareyou.Activitiy.MainActivity;
import com.kj_project.whereareyou.R;

public class SendSmsFragment extends Fragment {

    View view;
    Button btnSendSms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fmt_send_sms, parentViewGroup, false);

        btnSendSms = view.findViewById(R.id.btn_send_sms);

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).send("전송잘되나 테스트", null);
            }
        });

        return view;
    }

}
