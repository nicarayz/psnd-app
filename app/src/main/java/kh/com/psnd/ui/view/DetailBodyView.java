package kh.com.psnd.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import core.lib.base.BaseFragment;
import kh.com.psnd.databinding.LayoutDetailBodyBinding;
import kh.com.psnd.network.model.Staff;

public class DetailBodyView extends FrameLayout {

    private LayoutDetailBodyBinding binding = null;

    public DetailBodyView(@NonNull Context context) {
        super(context);
        init();
    }

    public DetailBodyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailBodyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        binding = LayoutDetailBodyBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void setupUI(@NonNull BaseFragment fragment, @NonNull Staff staff) {
        binding.valueLabel1.setText(staff.getRank());
        binding.valueLabel2.setText(staff.getPosition());
        binding.valueLabel3.setText(staff.getDepartment());
        binding.valueLabel4.setText(staff.getOffice());
        binding.valueLabel5.setText(staff.getSector());
        binding.valueLabel6.setText(staff.getStatus());
        binding.valueLabel7.setText(staff.getSex());
        binding.valueLabel8.setText(staff.getDateOfBirth());
        binding.valueLabel9.setText(staff.getEducation());
        binding.valueLabel10.setText(staff.getSkill());
        binding.valueLabel11.setText(staff.getTelephone());
        binding.valueLabel12.setText(staff.getAddress());

        try {
            binding.valueLabel6.setTextColor(Color.parseColor(staff.getStatusColor()));
        } catch (Throwable e) {
        }
    }

}