package com.example.wellxiang.falldetecion;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.suke.widget.SwitchButton;


/**
 * Created by LiuWeixiang on 2017/2/22.
 */
public class HomeFragment extends Fragment {

    private SwitchButton switchButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,null);
        switchButton = (SwitchButton) view.findViewById(R.id.switchButton);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(view.isChecked()){
                    Intent startIntent = new Intent(getContext(), FallDetectionService.class);
                    getContext().startService(startIntent);
                }else{
                    Intent stopIntent = new Intent(getContext(), FallDetectionService.class);
                    getContext().stopService(stopIntent);
                }
            }
        });
        return view;
    }

}
