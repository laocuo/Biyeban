package com.laocuo.biyeban.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.FactoryInterface;
import com.laocuo.biyeban.utils.L;


public class AvatarPreference extends Preference {
    private ImageView mAvatar;
    private Context mContext;
    private SharedPreferences sp;

    public AvatarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutResource(R.layout.pref_avatar);
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mAvatar = (ImageView) view.findViewById(R.id.userinfo_avatar);
        L.d("onBindView");
        updateAvatar();
    }

    public void updateAvatar() {
        L.d("updateAvatar");
        BiyebanUser user = BmobUtils.getCurrentUser();
        if (user != null) {
            FactoryInterface.setAvatar(mContext, user, mAvatar);
        }
    }
}
