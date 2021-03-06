package kh.com.psnd.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.amplifyframework.auth.AuthException;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import core.lib.base.BaseFragmentActivity;
import core.lib.dialog.DialogProgress;
import core.lib.utils.ApplicationUtil;
import core.lib.utils.Log;
import kh.com.psnd.R;
import kh.com.psnd.database.config.AppDatabase;
import kh.com.psnd.database.entities.DetailStaffEntity;
import kh.com.psnd.databinding.ActivityMainBinding;
import kh.com.psnd.databinding.NavHeaderMainBinding;
import kh.com.psnd.eventbus.CognitoFetchSessionFailureEventBus;
import kh.com.psnd.helper.ActivityHelper;
import kh.com.psnd.helper.LoginManager;
import kh.com.psnd.network.model.LoginProfile;
import kh.com.psnd.network.model.SearchStaff;
import kh.com.psnd.network.request.RequestQRCode;
import kh.com.psnd.network.response.ResponseLogin;
import kh.com.psnd.network.response.ResponseStaff;
import kh.com.psnd.network.task.TaskQRCode;
import kh.com.psnd.service.CognitoService;
import lombok.val;
import retrofit2.Response;

public class MainActivity extends BaseFragmentActivity<ActivityMainBinding> {
    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    private AppBarConfiguration mAppBarConfiguration;
    private NavController       navController;
    private MenuItem            iconQRCode;
    private DialogProgress      progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LoginManager.isLoggedIn()) {
            Log.i("User not logged-in yet");
            finish();
            ActivityHelper.openLoginActivity(this);
        }
        CognitoService.start(context);

        progress = new DialogProgress(context, false, dialogInterface -> getCompositeDisposable().clear());
        setSupportActionBar(binding.appBarMain.toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_search, R.id.nav_user_management, R.id.nav_history, R.id.nav_about)
                .setDrawerLayout(binding.drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.i(destination);
                if (iconQRCode != null) {
                    switch (destination.getId()) {
                        case R.id.nav_search:
                            iconQRCode.setVisible(true);
                            break;
                        default:
                            iconQRCode.setVisible(false);
                            break;
                    }
                }
            }
        });

        binding.logout.setOnClickListener(__ -> {
            new MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.ic_outline_exit_to_app_24)
                    .setTitle(R.string.logout)
                    .setMessage(R.string.msg_ask_logout)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.logout, (dialogInterface, i) -> LoginManager.logout(MainActivity.this)).show();
        });

        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                ApplicationUtil.dismissKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                ApplicationUtil.dismissKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        bindViewHeader();
    }

    private void bindViewHeader() {
        LoginProfile profile = LoginManager.getUserProfile();
        Log.i(profile);

        val staffId  = (profile.getStaff() == null ? "" : profile.getStaff().getStaffNumber());
        val fullName = (profile != null && profile.getStaff() != null) ? profile.getStaff().getFullName() : profile.getUsername();
        val image    = profile.getStaff() == null ? null : profile.getStaff().getPhoto();

        val header = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
        if (TextUtils.isEmpty(fullName)) {
            header.username.setVisibility(View.GONE);
        }
        else {
            header.username.setText(fullName);
        }
        /////////////////////////////////////
        if (TextUtils.isEmpty(staffId)) {
            header.userId.setVisibility(View.GONE);
        }
        else {
            header.userId.setText(getString(R.string.user_id, staffId));
        }
        header.imageProfile.setImageURI(image);
        header.getRoot().setOnClickListener(__ -> ActivityHelper.openProfileActivity(context));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Log.i("");
        if (binding.drawerLayout.isOpen()) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (navController.getCurrentDestination().getId() != R.id.nav_search) {
            navController.popBackStack(navController.getCurrentDestination().getId(), true);
            navController.navigate(R.id.nav_search);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        iconQRCode = menu.findItem(R.id.qrcode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qrcode:
                new IntentIntegrator(this)
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        .setPrompt(getString(R.string.qrcode_description))
                        .initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                loadDetailByQRCode(result.getContents());
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadDetailByQRCode(String qrcode) {
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
                if (result.isSuccessful()) {
                    val data   = (ResponseStaff) result.body();
                    val staff  = data.getResult();
                    val search = new SearchStaff(staff);

                    AppDatabase.getInstance().detailStaffDao().insertAll(new DetailStaffEntity(staff));

                    ActivityHelper.openDetailActivity(context, search);
                }
                progress.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(e);
                progress.dismiss();
            }
        }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCognitoFetchSessionFailureEventBus(CognitoFetchSessionFailureEventBus event) {
        Log.i(event);
        val error = event.getUserPoolTokens().getError();
        if (error instanceof AuthException.SessionExpiredException) {
            // todo logic session expired
        }
        Toast.makeText(context, R.string.session_expired, Toast.LENGTH_LONG).show();
        finish();
        ActivityHelper.openLoginActivity(this);
    }
}