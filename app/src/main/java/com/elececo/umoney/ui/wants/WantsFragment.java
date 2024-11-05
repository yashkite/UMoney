package com.elececo.umoney.ui.wants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.elececo.umoney.R;
import com.elececo.umoney.ui.base.BaseFragment;
import com.elececo.umoney.ui.wants.viewmodel.WantsViewModel;

public class WantsFragment extends BaseFragment<WantsViewModel> {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wants, container, false);
    }
    
    @Override
    protected Class<WantsViewModel> getViewModelClass() {
        return WantsViewModel.class;
    }
    
    @Override
    protected void setupObservers() {
        // TODO: Setup observers for wants data
    }
}
