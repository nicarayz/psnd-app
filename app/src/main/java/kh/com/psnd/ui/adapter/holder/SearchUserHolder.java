package kh.com.psnd.ui.adapter.holder;

import android.graphics.Color;

import java.util.List;

import core.lib.base.BaseViewHolder;
import kh.com.psnd.R;
import kh.com.psnd.databinding.ItemSearchUserBinding;
import kh.com.psnd.network.model.UserProfile;
import kh.com.psnd.ui.fragment.user.SearchUserFragment;
import lombok.val;

public class SearchUserHolder extends BaseViewHolder<SearchUserFragment, ItemSearchUserBinding, UserProfile> {

    public SearchUserHolder(SearchUserFragment fragment, ItemSearchUserBinding binding) {
        super(fragment, binding);
    }

    public void bind(List<UserProfile> items, int position) {
        super.bind(items.get(position));
        val username = item.getStaff() == null ? item.getUsername() : item.getStaff().getFullName();
        binding.username.setText(username);
        binding.image.setImageURI((item.getStaff() != null) ? item.getStaff().getPhoto() : null);
        binding.role.setText(item.getRole().getName());
        binding.privileges.setupUI(item);

        binding.status.setText(item.isActive() ? R.string.active : R.string.suspend);
        try {
            binding.status.setTextColor(Color.parseColor(item.getActiveColor()));
        } catch (Throwable e) {
        }

        binding.getRoot().setOnClickListener(__ -> fragment.showForm_UserInfoFragment(item));
    }
}
