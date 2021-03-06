package kh.com.psnd.ui.fragment;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import core.lib.base.BaseFragment;
import core.lib.utils.ApplicationUtil;
import core.lib.utils.Log;
import kh.com.psnd.R;
import kh.com.psnd.database.dao.SearchHistoryDao;
import kh.com.psnd.databinding.FragmentSearchStaffBinding;
import kh.com.psnd.helper.ActivityHelper;
import kh.com.psnd.network.model.SearchStaff;
import kh.com.psnd.network.model.StaffFilter;
import kh.com.psnd.ui.fragment.user.SearchAddUserFragment;
import kh.com.psnd.ui.view.SearchStaffBarView;
import lombok.Setter;
import lombok.val;

public class SearchStaffFragment extends BaseFragment<FragmentSearchStaffBinding> {

    @Setter
    private Object parentCallBack = null;

    @Override
    public void setupUI() {
        binding.searchBar.setupUI(this, callback);
        binding.searchResult.setupUI(this, binding.searchBar);

//        new FancyShowCaseView.Builder(getActivity())
//                .focusOn(binding.searchBar.findViewById(R.id.text_input_start_icon))
//                .title("Focus on View")
//                .showOnce("text_input_start_icon")
//                .fitSystemWindows(true)
//                .dismissListener(new DismissListener() {
//                    @Override
//                    public void onDismiss(@Nullable String s) {
//                        Log.i(s);
//                    }
//
//                    @Override
//                    public void onSkipped(@Nullable String s) {
//                        Log.i(s);
//                    }
//                })
//                .build()
//                .show();


    }

    @Override
    public void onResume() {
        super.onResume();
        binding.searchBar.onResume();
    }

    private SearchStaffBarView.Callback callback = new SearchStaffBarView.Callback() {
        @Override
        public void doSearch(StaffFilter filter) {
            binding.searchBar.clearTextBox();
            binding.searchResult.doSearch(filter);
        }

        @Override
        public void doSearch(CharSequence search) {
            Log.i("Search : " + search);
            if (TextUtils.isEmpty(search)) {
                // todo do something here
                return;
            }
            if (!ApplicationUtil.isOnline()) {
                Snackbar.make(getView(), R.string.noInternetConnection, Snackbar.LENGTH_SHORT).show();
                return;
            }
            binding.searchResult.doSearch(search.toString());
        }

        @Override
        public void onClickedClear() {
            Log.i("onClickedClear");
            binding.searchResult.cleanList();
        }
    };

    @Override
    protected void initToolbar() {

    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_search_staff;
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.searchBar.onPause();
    }

    public void onClickedItem(@NonNull List<SearchStaff> items, int position) {
        SearchHistoryDao.addSearch(binding.searchBar.getSearchString());
        if (parentCallBack != null) {
            if (parentCallBack instanceof SearchAddUserFragment) {
                val searchAddUserFragment = ((SearchAddUserFragment) parentCallBack);
                searchAddUserFragment.getCallback().onSearch(items.get(position));
                searchAddUserFragment.dismiss();
            }
        }
        else {
            ActivityHelper.openDetailActivity(getContext(), items, position);
        }
    }
}