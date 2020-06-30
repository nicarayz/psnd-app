package kh.com.psnd.ui.activity;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import core.lib.base.BaseFragmentActivity;
import core.lib.utils.Log;
import kh.com.psnd.R;
import kh.com.psnd.databinding.ActivityMainBinding;
import kh.com.psnd.databinding.NavHeaderMainBinding;
import kh.com.psnd.helper.ActivityHelper;
import kh.com.psnd.helper.LoginManager;
import kh.com.psnd.network.model.UserProfile;
import lombok.val;

public class MainActivity extends BaseFragmentActivity<ActivityMainBinding> {
    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    private AppBarConfiguration mAppBarConfiguration;
    private NavController       navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LoginManager.isLoggedIn()) {
            finish();
            ActivityHelper.openLoginActivity(this);
        }
        setSupportActionBar(binding.appBarMain.toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_search, R.id.nav_history, R.id.nav_about)
                .setDrawerLayout(binding.drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.logout.setOnClickListener(__ -> {
            new MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.ic_outline_exit_to_app_24)
                    .setTitle(R.string.logout)
                    .setMessage(R.string.msg_ask_logout)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.logout, (dialogInterface, i) -> LoginManager.logout(MainActivity.this)).show();
        });

        UserProfile profile = LoginManager.getUserProfile();

        Log.i(profile);

        val header = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
        header.username.setText(profile.getUsername());
        header.userId.setText(profile.getId());
        header.imageProfile.setImageURI(profile.getImage());
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
}