package com.freethumbnailmaker.nowatermark.activity;

import android.os.Bundle;
import com.freethumbnailmaker.nowatermark.R;

public class TutorialActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView( R.layout.main_activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
