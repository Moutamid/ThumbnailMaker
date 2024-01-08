package com.freethumbnailmaker.nowatermark.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.freethumbnailmaker.nowatermark.R;

public class SizeSelection extends Fragment implements View.OnClickListener {
    GetSelectSize getSizeOptions;
    View view;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.view = layoutInflater.inflate(R.layout.size_options_grid, viewGroup, false);
        this.getSizeOptions = (GetSelectSize) getActivity();
        this.view.findViewById(R.id.size_btn01).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn02).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn03).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn04).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn05).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn06).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn1).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn2).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn3).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn4).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn5).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn6).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn7).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn8).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn9).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn10).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn11).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn12).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn13).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn14).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn15).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn16).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn17).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn18).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn19).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn20).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn21).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn22).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn23).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn24).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn25).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn26).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn27).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn28).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn29).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn30).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn31).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn32).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn33).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn34).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn35).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn36).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn37).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn38).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn39).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn40).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn41).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn42).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn43).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn44).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn45).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn46).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn47).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn48).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn49).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn50).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn51).setOnClickListener(this);
        this.view.findViewById(R.id.size_btn52).setOnClickListener(this);
        this.view.findViewById(R.id.size_btnwattpad).setOnClickListener(this);
        return this.view;
    }

    public void onClick(View view2) {
        switch (view2.getId()) {
            case R.id.size_btn01:
            case R.id.size_btn02:
            case R.id.size_btn03:
            case R.id.size_btn04:
            case R.id.size_btn05:
            case R.id.size_btn06:
                this.getSizeOptions.ratioOptions(view2.getTag().toString());
                return;
            case R.id.size_btn1:
            case R.id.size_btn10:
            case R.id.size_btn11:
            case R.id.size_btn12:
            case R.id.size_btn13:
            case R.id.size_btn14:
            case R.id.size_btn15:
            case R.id.size_btn16:
            case R.id.size_btn17:
            case R.id.size_btn18:
            case R.id.size_btn19:
            case R.id.size_btn2:
            case R.id.size_btn20:
            case R.id.size_btn21:
            case R.id.size_btn22:
            case R.id.size_btn23:
            case R.id.size_btn24:
            case R.id.size_btn25:
            case R.id.size_btn26:
            case R.id.size_btn27:
            case R.id.size_btn28:
            case R.id.size_btn29:
            case R.id.size_btn3:
            case R.id.size_btn30:
            case R.id.size_btn31:
            case R.id.size_btn32:
            case R.id.size_btn33:
            case R.id.size_btn34:
            case R.id.size_btn35:
            case R.id.size_btn36:
            case R.id.size_btn37:
            case R.id.size_btn38:
            case R.id.size_btn39:
            case R.id.size_btn4:
            case R.id.size_btn40:
            case R.id.size_btn41:
            case R.id.size_btn42:
            case R.id.size_btn43:
            case R.id.size_btn44:
            case R.id.size_btn45:
            case R.id.size_btn46:
            case R.id.size_btn47:
            case R.id.size_btn48:
            case R.id.size_btn49:
            case R.id.size_btn5:
            case R.id.size_btn50:
            case R.id.size_btn51:
            case R.id.size_btn52:
            case R.id.size_btn6:
            case R.id.size_btn7:
            case R.id.size_btn8:
            case R.id.size_btn9:
            case R.id.size_btnwattpad:
                this.getSizeOptions.sizeOptions(view2.getTag().toString());
                return;
            default:
                return;
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        freeMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        freeMemory();
    }

    public void freeMemory() {
        try {
            new Thread(() -> {
                try {
                    Glide.get(SizeSelection.this.getActivity()).clearDiskCache();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            Glide.get(getActivity()).clearMemory();
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
}
