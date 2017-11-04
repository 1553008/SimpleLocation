package com.example.hoangdung.simplelocation;


import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private @BindView(R.id.toolbar_search)Toolbar mToolbar;
    private @BindView(R.id.search_textview) TextView mSearchTextView;
    private Unbinder mUnbinder;
    public SearchFragment() {
        // Required empty public constructor
    }
    public interface SearchFragmentCallback{
         void onSearchFragmentViewCreated();
         void onSearchBarClicked();
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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public void setText(String text){
        mSearchTextView.setText(text);
    }
}
