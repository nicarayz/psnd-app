package kh.com.psnd.ui.adapter.holder;

import java.util.List;

import core.lib.base.BaseViewHolder;
import kh.com.psnd.databinding.ItemUserSearchBinding;
import kh.com.psnd.network.model.UserSearch;
import kh.com.psnd.ui.fragment.UserManagementFragment;

public class UserSearchHolder extends BaseViewHolder<UserManagementFragment, ItemUserSearchBinding, UserSearch> {

    public UserSearchHolder(UserManagementFragment fragment, ItemUserSearchBinding binding) {
        super(fragment, binding);
    }

    public void bind(List<UserSearch> items, int position) {
        super.bind(items.get(position));
//        binding.image.setImageURI(item.getPhoto());
//        binding.name.setText(item.getFullName());
//        binding.department.setText(item.getDepartment());
//        binding.id.setText(item.getStaffNumber());
//        binding.getRoot().setOnClickListener(__ -> fragment.onClickedItem(items, position));
    }
}
