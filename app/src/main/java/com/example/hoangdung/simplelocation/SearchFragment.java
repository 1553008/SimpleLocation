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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private AppCompatActivity mContext;
    private Toolbar mToolbar;
    private TextView mSearchTextView;
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
        mToolbar = (Toolbar) inflater.inflate(R.layout.fragment_search,container,false);
        mSearchTextView = mToolbar.findViewById(R.id.search_textview);
        return mToolbar;
    }

    public void setText(String text){
        mSearchTextView.setText(text);
    }
}
