package com.example.navigate.ui.monitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.navigate.R;

public class MonitorFragment extends Fragment{

    private MonitorViewModel monitorViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        monitorViewModel =
                ViewModelProviders.of(this).get(MonitorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_monitor, container, false);
        return root;
    }
}