package kh.com.psnd.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;

import core.lib.base.BaseFragment;
import core.lib.dialog.DialogProgress;
import core.lib.listener.MyTextWatcher;
import core.lib.utils.ApplicationUtil;
import core.lib.utils.Log;
import kh.com.psnd.R;
import kh.com.psnd.databinding.FragmentSignupBinding;
import kh.com.psnd.eventbus.SingUpSuccessEventBus;
import kh.com.psnd.helper.ActivityHelper;
import kh.com.psnd.helper.CognitoHelper;
import kh.com.psnd.helper.LoginManager;
import kh.com.psnd.network.model.LoginProfile;
import kh.com.psnd.network.model.SignUpStep1;
import kh.com.psnd.network.model.Staff;
import kh.com.psnd.network.request.RequestQRCode;
import kh.com.psnd.network.request.RequestUserAdd;
import kh.com.psnd.network.request.RequestUserCheck;
import kh.com.psnd.network.response.ResponseLogin;
import kh.com.psnd.network.response.ResponseStaff;
import kh.com.psnd.network.response.ResponseUserAdd;
import kh.com.psnd.network.response.ResponseUserCheck;
import kh.com.psnd.network.task.TaskQRCode;
import kh.com.psnd.network.task.TaskUserAdd;
import kh.com.psnd.network.task.TaskUserCheck;
import lombok.val;
import retrofit2.Response;

public class SignUpFragmentBackup extends BaseFragment<FragmentSignupBinding> {
    private DialogProgress progress = null;
    private Staff          staff    = null;

    public static SignUpFragmentBackup newInstance(SignUpStep1 signUpStep1) {
        val fragment = new SignUpFragmentBackup();
        val bundle   = new Bundle();
        bundle.putSerializable(SignUpStep1.EXTRA, signUpStep1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setupUI() {
        progress = new DialogProgress(getContext(), true, dialogInterface -> getCompositeDisposable().clear());
        binding.btnSignUp.setEnabledWithAlpha(false);

        IntentIntegrator.forSupportFragment(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt(getString(R.string.qrcode_signup))
                .initiateScan();

        binding.passwordView.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.btnSignUp.setEnabledWithAlpha(binding.passwordView.isValidPassword());
                binding.msg.setText("");
            }
        });
        binding.btnSignUp.setOnClickListener(__ -> doSignUp());
    }

    public void updateUI(@NonNull Staff staff) {
        this.staff = staff;
        binding.staffName.setText(staff.getFullName());
        binding.office.setText(staff.getOffice());
        binding.department.setText(staff.getDepartment());
        binding.imageProfile.setImageURI(staff.getPhoto());
        binding.username.setText(staff.getIdEn());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.i(result);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                loadDetailByQRCode(result.getContents());
                return;
            }
        }
        finish();
        // super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isValidateUsername() {
        binding.msg.setText("");
        if (TextUtils.isEmpty(binding.username.getText())) {
            binding.textInputLayoutUserName.setError(getString(R.string.username_error));
            return false;
        }
        binding.textInputLayoutUserName.setError(null);
        return true;
    }

    private boolean isValidatePhoneNumber() {
        binding.msg.setText("");
        if (TextUtils.isEmpty(binding.phoneNumber.getText())) {
            binding.textInputLayoutPhoneNumber.setError(getString(R.string.phone_number_error));
            return false;
        }
        binding.textInputLayoutPhoneNumber.setError(null);
        return true;
    }

    private boolean isFormValid() {
        return isValidateUsername() && isValidatePhoneNumber() && binding.passwordView.isValidPassword();
    }

    /**
     * 1 : check username from server
     * 2 : check username from cognito server
     */
    private void doSignUp() {
        val username = binding.username.getText().toString();
        val pwd      = binding.passwordView.getPassword();
        if (isFormValid()) {
            if (!ApplicationUtil.isOnline()) {
                Toast.makeText(getContext(), R.string.noInternetConnection, Toast.LENGTH_LONG).show();
                return;
            }
            {
                // Check existing user in database server
                progress.show();
                val task = new TaskUserCheck(new RequestUserCheck(username));
                getCompositeDisposable().add(task.start(task.new SimpleObserver() {

                    @Override
                    public Class<?> clazzResponse() {
                        return null;
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Response result) {
                        Log.i("LOG >> onNext >> result : " + result);
                        if (result.isSuccessful()) {
                            val data = (ResponseUserCheck) result.body();
                            if (data.isFound()) {
                                binding.msg.setText(R.string.user_already_exists);
                                progress.dismiss();
                            }
                            else {
                                // signup user
                                signUpWitCognito();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(e);
                        progress.dismiss();
                    }
                }));
            }
        }
    }

    private void signUpWitCognito() {
        val username = binding.username.getText().toString();
        val pwd      = binding.passwordView.getPassword();
        binding.msg.setText("");
        val signUpOption = CognitoHelper.getAuthSignUpOptions(staff);
        Amplify.Auth.signUp(username, pwd, signUpOption, new Consumer<AuthSignUpResult>() {
            @Override
            public void accept(@NonNull AuthSignUpResult value) {
                Log.i("Amplify : " + value);
                if (value.isSignUpComplete()) {
                    // todo save user profile, open main screen, fetch user's token
                    createUserInServer();
                }
                else {
                    progress.dismiss();
                }
            }
        }, new Consumer<AuthException>() {
            @Override
            public void accept(@NonNull AuthException value) {
                Log.i("Amplify : " + value);
                if (value.getCause() instanceof UsernameExistsException) {
                    binding.msg.setText(R.string.user_already_exists);
                }
                else if (value.getCause() instanceof InvalidPasswordException) {
                    binding.msg.setText(value.getCause().getMessage());
                }
                else {
                    binding.msg.setText(value.getMessage());
                }
                progress.dismiss();
            }
        });
    }

    private void createUserInServer() {
        val username = binding.username.getText().toString();
        val task     = new TaskUserAdd(new RequestUserAdd(staff.getStaffId(), username));
        getCompositeDisposable().add(task.start(task.new SimpleObserver() {

            @Override
            public Class<?> clazzResponse() {
                return null;
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Response result) {
                Log.i("LOG >> onNext >> result : " + result);
                if (result.isSuccessful()) {
                    val data = (ResponseUserAdd) result.body();
                    if (data.isAccessDenied()) {
                        binding.msg.setText(data.getMessage());
                        progress.dismiss();
                    }
                    else {
                        val profile = new LoginProfile(data.getResult());
                        doAutoSignIn(profile);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(e);
                progress.dismiss();
            }
        }));
    }

    private void doAutoSignIn(LoginProfile profile) {
        val username = binding.username.getText().toString();
        val pwd      = binding.passwordView.getPassword();
        Amplify.Auth.signIn(username, pwd, new Consumer<AuthSignInResult>() {
            @Override
            public void accept(@NonNull AuthSignInResult value) {
                Log.i("result : " + value);
                if (value.isSignInComplete()) {
                    fetchAuthSession(profile);
                }
                else {
                    progress.dismiss();
                }
            }
        }, new Consumer<AuthException>() {
            @Override
            public void accept(@NonNull AuthException value) {
                Log.e("result : " + value);
            }
        });
    }

    private void fetchAuthSession(LoginProfile profile) {
        Amplify.Auth.fetchAuthSession(
                result -> {
                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                    Log.i("result : " + result);

                    try {
                        profile.setTokens(AWSMobileClient.getInstance().getTokens());
                        LoginManager.loggedIn(profile);
                        EventBus.getDefault().post(new SingUpSuccessEventBus());

                        progress.dismiss();
                        finish();
                        ActivityHelper.openMainActivity(getContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("AuthQuickStart " + error.toString());
                    progress.dismiss();
                }
        );
    }

    private void loadDetailByQRCode(String qrcode) {
        if (!ApplicationUtil.isOnline()) {
            Toast.makeText(getContext(), R.string.noInternetConnection, Toast.LENGTH_LONG).show();
            getBaseFragmentActivity().finish();
            return;
        }
        progress.show();
        val task = new TaskQRCode(new RequestQRCode(qrcode), this);
        getCompositeDisposable().add(task.start(task.new SimpleObserver() {

            @Override
            public Class<?> clazzResponse() {
                return ResponseLogin.class;
            }

            @Override
            public void onNext(Response result) {
                Log.i("LOG >> onNext >> result : " + result);
                progress.dismiss();
                if (result.isSuccessful()) {
                    val data  = (ResponseStaff) result.body();
                    val staff = data.getResult();
                    Log.i(staff);
                    if (staff != null) {
                        updateUI(staff);
                        return;
                    }
                }
                getActivity().finish();
                Toast.makeText(getContext(), R.string.msg_scan_wrong_qrcode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(e);
                progress.dismiss();
                finish();
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }));
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_signup;
    }

}