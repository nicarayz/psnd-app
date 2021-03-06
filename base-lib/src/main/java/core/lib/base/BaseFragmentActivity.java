package core.lib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import core.lib.BuildConfig;
import core.lib.R;
import core.lib.utils.Log;
import lombok.val;

public abstract class BaseFragmentActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected       T        binding         = null;
    protected       Context  context         = this;
    public          Fragment currentFragment = null;
    protected final String   MY_FRG          = "MY_FRG";
    public static   String   currentPage     = BaseFragment.PAGE_UNKNOWN;

    abstract protected int layoutId();

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.fade_in_quick, R.anim.fade_out_quick);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, layoutId());
    }

    public void showContentFragment(Fragment fragment, String tagFragment, String whatPage, boolean clearBackStack, boolean showAnimate) {
        Log.e("=================");
        Log.i("[oldFragment]" + currentFragment);
        Log.i("[newFragment]" + fragment.toString());
        Log.i("[whatTheNewPage]" + whatPage);
        val fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (showAnimate) {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in_quick, R.anim.fade_out_quick, R.anim.fade_in_quick, R.anim.fade_out_quick);
        }
        fragmentTransaction.add(R.id.contentFragment, fragment, tagFragment);
        //  fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (clearBackStack) {
            clearAllBackStack();
        }
        try {
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            currentPage = whatPage;
//            currentFragment = baseFragment;
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public void showContentFragment(Fragment fragment, String whatPage, boolean clearBackStack, boolean showAnimate) {
        showContentFragment(fragment, MY_FRG, whatPage, clearBackStack, showAnimate);
    }

    public void showContentFragment(Fragment fragment, String whatPage, boolean clearBackStack) {
        showContentFragment(fragment, MY_FRG, whatPage, clearBackStack, false);
    }

    public void clearAllBackStack() {
        val fm = getSupportFragmentManager();

        int backStackSize = fm.getBackStackEntryCount();
        if ((BuildConfig.DEBUG_MODE)) {
            Log.i("[clearAllBackStack][backStackSize]" + backStackSize);
        }
        for (int i = 0; i < backStackSize; i++) {
            try {
                int backStackId = fm.getBackStackEntryAt(i).getId();
                fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException e) {

            }
        }
    }
}
