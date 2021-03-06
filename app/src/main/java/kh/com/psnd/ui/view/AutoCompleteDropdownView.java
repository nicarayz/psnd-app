package kh.com.psnd.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import java.util.List;

import core.lib.utils.Log;
import kh.com.psnd.R;
import kh.com.psnd.databinding.LayoutAutocompleteDropdownBinding;
import lombok.val;

public class AutoCompleteDropdownView extends FrameLayout {

    private LayoutAutocompleteDropdownBinding binding  = null;
    private Callback                          callback = null;

    public AutoCompleteDropdownView(@NonNull Context context) {
        super(context);
        init();
    }

    public AutoCompleteDropdownView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoCompleteDropdownView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        binding = LayoutAutocompleteDropdownBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public <T> void setupUI(@NonNull List<T> list, @NonNull Callback callback) {
        this.callback = callback;
        val adapter = new ArrayAdapter<T>(getContext(), R.layout.dropdown_menu_popup_item, R.id.item, list);
        binding.dropdown.setText("");
        binding.dropdown.setAdapter(adapter);
        binding.textInputLayout.invalidate();
        enabledItemClickListener(true);
    }

    public void setInputType(int type) {
        binding.dropdown.setInputType(type);
//        binding.dropdown.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
//        binding.textInputLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
    }

    public void selectItem(int position) {
        val item = binding.dropdown.getAdapter().getItem(position);
        Log.i(item);
        binding.dropdown.setText(item.toString(), false);
//        binding.dropdown.setSelection(position);
//        binding.dropdown.setListSelection(position);
        setTag(item);
    }

    public void selectItem(@NonNull Object obj) {
        Log.i(obj);
        binding.dropdown.setText(obj.toString(), false);
        setTag(obj);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            callback.onSelected(adapterView.getItemAtPosition(position));
        }
    };

    public void enabledItemClickListener(boolean enabled) {
        binding.dropdown.setOnItemClickListener(enabled ? onItemClickListener : null);
    }

    @BindingAdapter("hint")
    public static void hint(AutoCompleteDropdownView view, String hint) {
        view.binding.textInputLayout.setHint(hint);
    }


    public interface Callback {
        void onSelected(Object object);
    }

}