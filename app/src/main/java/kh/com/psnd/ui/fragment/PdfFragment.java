package kh.com.psnd.ui.fragment;

import android.net.Uri;
import android.view.View;

import com.github.barteksc.pdfviewer.listener.OnErrorListener;

import java.io.File;

import core.lib.base.BaseFragment;
import core.lib.utils.Log;
import kh.com.psnd.R;
import kh.com.psnd.databinding.FragmentPdfBinding;
import kh.com.psnd.ui.activity.PdfActivity;
import lombok.val;

public class PdfFragment extends BaseFragment<FragmentPdfBinding> {

    @Override
    public void setupUI() {
        val path = getActivity().getIntent().getStringExtra(PdfActivity.EXTRA_PDF_URI);
        val uri  = Uri.fromFile(new File(path));
        Log.i("uri : " + uri);

        binding.pdfView
                .fromUri(uri)
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        getActivity().finish();
                    }
                })
                .onLoad(nbPages -> {
                    Log.i("nbPages : " + nbPages);
                    binding.progressBar.setVisibility(View.GONE);
                })
                .spacing(3)
                .load();
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_pdf;
    }
}