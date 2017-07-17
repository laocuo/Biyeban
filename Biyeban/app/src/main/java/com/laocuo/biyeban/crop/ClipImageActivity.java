package com.laocuo.biyeban.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.laocuo.biyeban.R;
import com.laocuo.biyeban.event.Event;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class ClipImageActivity extends AppCompatActivity {
    private Intent mIntent;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.id_clipImageLayout)
    ClipImageLayout mClipImageLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mIntent = getIntent();
        if (mIntent != null) {
            String s = mIntent.getStringExtra("CROP_URI");
            Uri mUri = Uri.parse(s);
            mClipImageLayout.setImageSrc(this, mUri);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_clip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clip:
                Bitmap bitmap = mClipImageLayout.clip();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                mIntent.putExtra("CROP_BITMAP", datas);
//                EventBus.getDefault().post(new Event.ClipEvent(datas));
                setResult(1, mIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
