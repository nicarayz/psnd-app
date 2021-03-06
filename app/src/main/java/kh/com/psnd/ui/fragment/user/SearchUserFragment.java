package kh.com.psnd.ui.fragment.user;

import android.widget.PopupMenu;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import core.lib.base.BaseFragment;
import core.lib.dialog.DialogProgress;
import core.lib.utils.ApplicationUtil;
import core.lib.utils.Log;
import kh.com.psnd.R;
import kh.com.psnd.databinding.FragmentSearchUserBinding;
import kh.com.psnd.eventbus.CreateAccountSuccessEventBus;
import kh.com.psnd.eventbus.LoadRolePrivilegeEventBus;
import kh.com.psnd.eventbus.UpdateAccountSuccessEventBus;
import kh.com.psnd.network.model.UserFilter;
import kh.com.psnd.network.model.UserProfile;
import kh.com.psnd.network.model.UserRolePrivilege;
import kh.com.psnd.service.LoadRolePrivilegeService;
import kh.com.psnd.ui.view.SearchUserBarView;
import lombok.val;

public class SearchUserFragment extends BaseFragment<FragmentSearchUserBinding> {

    private DialogProgress    progress;
    private UserRolePrivilege rolePrivilege;
    private String            createUserType = "";

    @Override
    public void setupUI() {
        LoadRolePrivilegeService.start(getContext(), "SearchUserFragment");
        progress = new DialogProgress(getContext(), true, dialogInterface -> getCompositeDisposable().clear());
        binding.addNewUser.setOnClickListener(view -> {
            val popup    = new PopupMenu(getContext(), view);
            val inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_create_user, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.create_user:
                        showPopupCreateUser(UserRolePrivilege.UserType_normal);
                        break;
                    case R.id.create_user_vip:
                        showPopupCreateUser(UserRolePrivilege.UserType_vip);
                        break;
                }
                return false;
            });
            popup.show();
        });
        binding.searchBar.setupUI(this, rolePrivilege, callback);
        binding.searchBar.showFilter(false);
        binding.searchResult.setupUI(this, binding.searchBar);
    }

    private void showPopupCreateUser(String createUserType) {
        this.createUserType = createUserType;
        if (rolePrivilege == null) {
            progress.show();
            LoadRolePrivilegeService.start(getContext(), "AddUserFragment");
        }
        else {
            showForm_AddUserFragment(rolePrivilege);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateAccountSuccessEventBus(CreateAccountSuccessEventBus event) {
        Log.i(event);
        binding.searchResult.addNewUser(event.getUserProfile());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateAccountSuccessEventBus(UpdateAccountSuccessEventBus event) {
        Log.i(event);
        binding.searchResult.updateUser(event.getUserProfile());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadRolePrivilegeEventBus(LoadRolePrivilegeEventBus event) {
        Log.i(event);
        rolePrivilege = event.getRolePrivilege();
        switch (event.getClazzName()) {
            case "AddUserFragment":
                showForm_AddUserFragment(rolePrivilege);
                break;
            case "SearchUserFragment":
                break;
        }
        binding.searchBar.showFilter(rolePrivilege != null);
        progress.dismiss();
    }

    private void showForm_AddUserFragment(UserRolePrivilege rolePrivilege) {
        val fragment = AddUserFragment.newInstance(rolePrivilege, createUserType);
        fragment.show(getParentFragmentManager(), "");
    }

    public void showForm_UserInfoFragment(UserProfile userProfile) {
        val userInfoFragment = UserInfoFragment.newInstance(userProfile, rolePrivilege);
        userInfoFragment.show(getBaseFragmentActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.searchBar.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.searchBar.onPause();
    }

    private SearchUserBarView.Callback callback = new SearchUserBarView.Callback() {
        @Override
        public void doSearch(UserFilter filter) {
            binding.searchBar.clearTextBox();
            binding.searchResult.doSearch(filter);
        }

        @Override
        public void doSearch(CharSequence search) {
            if (!ApplicationUtil.isOnline()) {
                Snackbar.make(getView(), R.string.noInternetConnection, Snackbar.LENGTH_SHORT).show();
                return;
            }
            binding.searchResult.doSearch(search.toString());
        }

    };

    @Override
    protected void initToolbar() {

    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_search_user;
    }


}