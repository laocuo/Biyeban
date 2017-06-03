package com.laocuo.biyeban.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.main.MainActivity;
import com.laocuo.biyeban.R;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    AutoCompleteTextView mUsernameView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_register_button)
    Button mRegisterButton;
    @BindView(R.id.login_login_in_button)
    Button mLoginButton;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;

//    private Handler mHandler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void doInject() {

    }

    @Override
    protected void doInit() {
        initToolBar(mToolbar, true, R.string.login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mPasswordView.setOnEditorActionListener(this);
    }

    @Override
    protected void doLoadData() {
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        if (user != null) {
            L.d("onStart getCurrentUser=" + user.getUsername());
            mUsernameView.setText(user.getUsername());
            mPasswordView.setText(user.getUsername());
            mPasswordView.requestFocus();
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    attemptLogin();
//                }
//            }, 1000);
        } else {
            L.d("onStart getCurrentUser=null");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BiyebanUser isValidUserInfo() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return null;
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            mAuthTask = new UserLoginTask(username, password);
//            mAuthTask.execute((Void) null);
            BiyebanUser user = new BiyebanUser();
            user.setUsername(username);
            user.setPassword(password);
            return user;
        }
    }

    @OnClick(R.id.login_register_button)
    void attemptRegister() {
        L.d("attemptRegister");
        BiyebanUser user = isValidUserInfo();
        if (user != null) {
            user.signUp(new SaveListener<BiyebanUser>() {
                @Override
                public void done(BiyebanUser u, BmobException e) {
                    showProgress(false);
                    if (e == null) {
                        //TODO success
                        SnackbarUtil.showShortSnackbar(mLoginFormView, "register success!");
                        L.d("register ok u=" + u.getUsername());
                    } else {
                        mPasswordView.requestFocus();
                        SnackbarUtil.showShortSnackbar(mLoginFormView, e.getMessage());
                        L.d(e.toString());
                    }
                }
            });
        }
    }

    @OnClick(R.id.login_login_in_button)
    void attemptLogin() {
        BiyebanUser user = isValidUserInfo();
        if (user != null) {
            login(user);
        }
    }

    private void login(BiyebanUser user) {
        user.login(new SaveListener<BiyebanUser>() {
            @Override
            public void done(BiyebanUser u, BmobException e) {
                if (e == null) {
                    //TODO success
//                    SnackbarUtil.showShortSnackbar(mLoginFormView, "login success!");
                    L.d("login ok u=" + u.getUsername());
                    enterMainMenu();
                } else {
                    showProgress(false);
                    mPasswordView.requestFocus();
                    SnackbarUtil.showShortSnackbar(mLoginFormView, e.getMessage());
                    L.d(e.toString());
                }
            }
        });
    }

    private void enterMainMenu() {
        L.d("login success");
        setResult(1);
        finish();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsernameView.setAdapter(adapter);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
    }

    @Override
    public boolean onEditorAction(TextView view, int i, KeyEvent event) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            attemptLogin();
        }
        return false;
    }

    @Override
    public void updateUI() {

    }
}

