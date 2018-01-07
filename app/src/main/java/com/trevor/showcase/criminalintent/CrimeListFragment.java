package com.trevor.showcase.criminalintent;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by trevormillner on 12/16/17.
 */

public class CrimeListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    // Used for updating selective items in recycler view
    private Integer mCurrentPosition;
    private boolean mSubtitleVisible;
    private LinearLayout mEmptyCrimesLinearView;
    private ImageButton mAddCrimeImageButton;

    private static final String SAVED_SUBTITLE_STATE = "subtitle";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRecyclerView = v.findViewById(R.id.crime_list_recycle_view);
        // Must not forget providing a LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mEmptyCrimesLinearView = v.findViewById(R.id.empty_crimes_view);
        mAddCrimeImageButton = v.findViewById(R.id.add_crime_button);

        mAddCrimeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCrime();
            }
        });

        if (savedInstanceState != null ){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_STATE);
        }

        updateUI();
        return v;
    }

    // This is necessary for when user navigates from
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        CrimeLab crimeLab = CrimeLab.get(getActivity());

        if (crimeLab.getCrimes().size() < 1) {
            mEmptyCrimesLinearView.setVisibility(View.VISIBLE);
        }
        else {
            mEmptyCrimesLinearView.setVisibility(View.GONE);
        }

        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimeLab.getCrimes());
            mRecyclerView.setAdapter(mCrimeAdapter);
        }
        else {
            mCrimeAdapter.updateCrimes(crimeLab.getCrimes());
            mCrimeAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
        int crimeCount = CrimeLab.get(getActivity()).getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem mShowSubtitleMenuItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            mShowSubtitleMenuItem.setTitle(R.string.hide_subtitle);
        }
        else {
            mShowSubtitleMenuItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_STATE, mSubtitleVisible);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mCrimeSolvedCheckbox;

        public CrimeHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.list_item_crime_title_text);
            mDateTextView = itemView.findViewById(R.id.list_item_crime_date_text);
            mCrimeSolvedCheckbox = itemView.findViewById(R.id.list_item_crime_solved_checkbox);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getCrimeDate().toString());
            mCrimeSolvedCheckbox.setChecked(mCrime.isCrimeSolved());
        }

        // mCrime will have value by the time it is clicked due to the
        //  sequence of events, createViewHolder -> bindViewHolder
        @Override
        public void onClick(View view) {
            mCurrentPosition = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getUUID());
            startActivity(intent);
            //Toast.makeText(getActivity(), "" + mCrime.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean addNewCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getUUID());
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case(R.id.menu_item_new_crime):
                addNewCrime();
            case(R.id.menu_item_show_subtitle):
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public void updateCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

    }
}
