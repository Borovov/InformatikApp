package com.example.informatika;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class VariantFragment  extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_variant, container, false);
        Button button = view.findViewById(R.id.butvar_gen);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VariantPoolActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_show, R.anim.translate_hide);
            }
        });

        return view;
    }
}
