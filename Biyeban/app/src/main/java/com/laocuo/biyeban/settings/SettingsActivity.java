/*
 *
 *  * Copyright (C) 2017 laocuo <laocuo@163.com>
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.laocuo.biyeban.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.GraduClass;
import com.laocuo.biyeban.graduation.GraduationActivity;
import com.laocuo.biyeban.login.LoginActivity;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.FactoryInterface;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.biyeban.utils.UploadBmobFileListener;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class SettingsActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.setting);
        }
        if (savedInstanceState == null) {
            MeFragment meFragment = new MeFragment();
            getFragmentManager().beginTransaction().add(R.id.content_container, meFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_exit);
    }

    public static class MeFragment extends PreferenceFragment
            implements ExitClassDialogFragment.ExitClassListener{
        private static final int REQUEST_LOGIN = 1;
        private static final int SELECT_PORTRAIT = 2;
        private static final int CROP_PORTRAIT = 3;

        public static final String USERINFO = "pref_key_user";
        public static final String AVATAR = "pref_key_user_avatar";
        public static final String ALIAS = "pref_key_user_alias";
        public static final String LOGINSTATUS = "pref_key_login_status";
        public static final String EXITCLASS = "pref_key_exit_class";
        public static final String ABOUT = "pref_key_about";

        private SwitchPreference mLoginSwitch;
        private AvatarPreference mAvatar;
        private EditTextPreference mAlias;

        private ProgressDialog mWaittingDialog;
        private ExitClassDialogFragment mExitClassDialogFragment;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            mAvatar = (AvatarPreference) findPreference(AVATAR);
            mAlias = (EditTextPreference) findPreference(ALIAS);
            mLoginSwitch = (SwitchPreference) findPreference(LOGINSTATUS);

            mWaittingDialog = new ProgressDialog(getActivity());
            mWaittingDialog.setCancelable(false);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(mAlias);
            bindPreferenceSummaryToValue(mLoginSwitch);
            mExitClassDialogFragment = new ExitClassDialogFragment();
            mExitClassDialogFragment.setListener(this);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            final String key = preference.getKey();
            if (AVATAR.equals(key)) {
                selectPortrait();
                return true;
            } else if (EXITCLASS.equals(key)) {
//                exitClass();
                mExitClassDialogFragment.show(getFragmentManager(), "exitclass");
                return true;
            } else if (ABOUT.equals(key)) {
                showAbout();
                return true;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see #sBindPreferenceSummaryToValueListener
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
            Object newValue = null;
            if (preference.getKey().equalsIgnoreCase(ALIAS)) {
                if (TextUtils.isEmpty(user.getAlias())) {
                    newValue = getActivity().getResources().getString(R.string.unset);
                } else {
                    newValue = user.getAlias();
                }
                preference.setSummary((String) newValue);
                ((EditTextPreference) preference).setText((String) newValue);
                return;
            } else if (preference.getKey().equalsIgnoreCase(LOGINSTATUS)) {
                mLoginSwitch.setChecked(true);
                return;
            } else {
                newValue = PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "");
            }
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                String stringValue = value.toString();

                if (preference instanceof AvatarPreference) {

                } else if (preference instanceof EditTextPreference) {
                    saveAndUpdateAlias(stringValue);
                } else if (preference instanceof SwitchPreference) {
                    if (preference.getKey().equalsIgnoreCase(LOGINSTATUS)) {
                        if ((Boolean) value == true) {
//                            startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUEST_LOGIN);
                        } else {
                            BmobUtils.logOut();
                            getActivity().finish();
//                            mAlias.setSummary(getActivity().getResources().getString(R.string.unset));
                        }
                        LoginActivity.launch(getActivity());
                    }
                } else {
                    // For all other preferences, set the summary to the value's
                    // simple string representation.
                    preference.setSummary(stringValue);
                }
                return true;
            }
        };

        private void selectPortrait() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PORTRAIT);
        }

        private void saveAndUpdateAlias(final String alias) {
            L.d("saveAndUpdateAlias:"+alias);
            if (alias.contains("admin")) {
                SnackbarUtil.showShortSnackbar(getView(), "can not include \"admin\"");
                return;
            }
            BiyebanUser currentUser = BmobUtils.getCurrentUser();
            String current_alias = currentUser.getAlias();
            if (current_alias == null || !current_alias.equals(alias)) {
                showProgress(true);
                BiyebanUser user = new BiyebanUser();
                user.setAlias(alias);
                user.update(currentUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        } else {
                            mAlias.setSummary(alias);
                        }
                        showProgress(false);
                    }
                });
            }
        }

        private void deleteCurrentAvatar() {
            BmobFile avatar = BmobUtils.getCurrentUser().getAvatar();
            String current_url = avatar == null ? "" : avatar.getFileUrl();
            BmobUtils.deleteBmobFile(current_url);
        }

        private void uploadAvatar() {
            String path = FactoryInterface.getAvatarPath(getActivity());
            BmobUtils.uploadBmobFile(new File(path), new UploadBmobFileListener() {
                @Override
                public void success(BmobFile f) {
                    deleteCurrentAvatar();
                    updateCurrentAvatar(f);
                }

                @Override
                public void fail() {
                    showProgress(false);
                }
            });
        }

        private void saveAndUpdateAvatar(byte[] b) {
            L.d("saveAndUpdateAvatar");
            showProgress(true);
            if (FactoryInterface.saveAvatar(getActivity(), b)) {
                uploadAvatar();
            } else {
                showProgress(false);
            }
        }

        private void updateCurrentAvatar(final BmobFile f){
            BiyebanUser user = new BiyebanUser();
            user.setAvatar(f);
            user.update(BmobUtils.getCurrentUser().getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    } else {
                        L.d("bmob", "更新成功");
                        mAvatar.updateAvatar();
                    }
                    showProgress(false);
                }
            });
        }

        private void exitClass() {
            showProgress(true);
            final BiyebanUser currentUser = BmobUtils.getCurrentUser();
            BiyebanUser user = new BiyebanUser();
            user.setGraduClass("");
            user.update(currentUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        showProgress(false);
                        SnackbarUtil.showShortSnackbar(getView(), "退出失败");
                    } else {
                        BmobQuery<GraduClass> query = new BmobQuery<GraduClass>();
                        query.getObject(currentUser.getGraduClass(), new QueryListener<GraduClass>() {
                            @Override
                            public void done(GraduClass aClass, BmobException e) {
                                if (e == null) {
                                    ArrayList<String> classmates = aClass.getClassmates();
                                    L.d("classmates.size()=" + classmates.size());
                                    classmates.remove(currentUser.getObjectId());
                                    L.d("classmates.size()=" + classmates.size());
                                    GraduClass graduClass = new GraduClass();
                                    graduClass.setClassmates(classmates);
                                    graduClass.update(aClass.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            showProgress(false);
                                            if (e == null) {
                                                BmobUtils.clearCache();
                                                getActivity().finish();
                                                GraduationActivity.launch(getActivity());
                                            } else {
                                                L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                                SnackbarUtil.showShortSnackbar(getView(), "退出失败");
                                            }
                                        }
                                    });
                                } else {
                                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                    showProgress(false);
                                    SnackbarUtil.showShortSnackbar(getView(), "退出失败");
                                }
                            }
                        });
                    }
                }
            });
        }

        private void showAbout() {
//        Intent i = new Intent(mContext, HelpActivity.class);
//        startActivity(i);
        }

        private void showProgress(boolean show) {
            if (show == true) {
                if (mWaittingDialog.isShowing() == false) {
                    mWaittingDialog.show();
                }
            } else {
                if (mWaittingDialog.isShowing() == true) {
                    mWaittingDialog.dismiss();
                }
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case REQUEST_LOGIN:
                    if (resultCode != 1) {
                        //login fail
                        mLoginSwitch.setChecked(false);
                    } else {
                        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
                        mAlias.setSummary(user.getAlias());
                        mAlias.setText(user.getAlias());
                        mAvatar.updateAvatar();
                    }
                    break;

                case SELECT_PORTRAIT:
                    L.d("SELECT_PORTRAIT: data = " + data);
                    if (data != null) {
                        Intent i = new Intent("android.intent.action.biyeban.Crop");
                        i.setType("image/*");
                        i.putExtra("CROP_URI", data.getData().toString());
                        startActivityForResult(i,CROP_PORTRAIT);
                    }
                    break;

                case CROP_PORTRAIT:
                    L.d("CROP_PORTRAIT: data = " + data);
                    if (resultCode > 0 && data != null) {
                        byte[] b = data.getByteArrayExtra("CROP_BITMAP");
                        saveAndUpdateAvatar(b);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void confirm() {
            exitClass();
        }
    }
}
