package com.example.hoangdung.simplelocation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private AppCompatActivity mContext;
    public @BindView(R.id.toolbar_search)Toolbar mToolbar;
    //public @BindView(R.id.search_textview) TextView mSearchTextView;
    private Unbinder mUnbinder;
    public SearchFragment() {
        // Required empty public constructor
    }
    public interface SearchFragmentCallback{
         void onSearchFragmentViewCreated();
         void onSearchBarClicked() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException;
         void onSearchFragmentDestroy();
         void onSearchFragmentResume();
         void onSearchFragmentPause();
         void onSearchFragmentStart();
         void onSearchFragmentStop();
    }

    public static SearchFragment newInstance(AppCompatActivity context) {
        SearchFragment fragment = new SearchFragment();
        fragment.mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_search,container,false);
        mUnbinder = ButterKnife.bind(this,view);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ((SearchFragmentCallback)mContext).onSearchBarClicked();
                }
                catch (Exception e){

                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((SearchFragmentCallback)mContext).onSearchFragmentViewCreated();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

   /* public void setText(String text){
        mSearchTextView.setText(text);
    }*/
    public Toolbar getToolbar(){
        return mToolbar;
    }
}
