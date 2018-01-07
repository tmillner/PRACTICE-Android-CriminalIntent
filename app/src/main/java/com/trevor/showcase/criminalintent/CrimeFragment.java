package com.trevor.showcase.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;


// This Fragment is from support (see build.gradle library)
public class CrimeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Crime mCrime;
    private EditText mEditCrimeText;
    private CheckBox mCrimeCheckbox;
    private Button mCrimeTimestampButton;

    private static final String ARG_BUNDLE_CRIME_ID = "crime_id";
    private static final String DATE_PICKER_DIALOG_TAG = "DatePickerDialog";

    private static final int REQUEST_DATE = 0;


    public CrimeFragment() {
        // Required empty public constructor
    }

    // This is necessary and a commmon pattern (new Instance)
    // when creating fragments that use Extras, and not imposing this
    // from holding activity
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_BUNDLE_CRIME_ID, crimeId);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);
        return crimeFragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CrimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CrimeFragment newInstance(String param1, String param2) {
        CrimeFragment fragment = new CrimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2 ways to access intent data
        // 1, using shortcut from parent activity
        //UUID crimeId = (UUID) getActivity().getIntent().
        //        getSerializableExtra(CriminalActivity.EXTRA_CRIME_ID);

        // 2 use arguments bundle
        if (getArguments() != null) {
            mCrime = CrimeLab.get(getActivity()).getCrime((UUID)
                    getArguments().getSerializable(ARG_BUNDLE_CRIME_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mEditCrimeText = v.findViewById(R.id.crime_title);
        mEditCrimeText.setText(mCrime.getTitle());
        mEditCrimeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCrimeCheckbox = v.findViewById(R.id.crime_solved_checkbox);
        mCrimeCheckbox.setChecked(mCrime.isCrimeSolved());
        mCrimeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setCrimeSolved(b);
            }
        });

        mCrimeTimestampButton = v.findViewById(R.id.crime_timestamp_button);
        updateDate();
        mCrimeTimestampButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerDialogFragment = DatePickerFragment.newInstance(
                        mCrime.getCrimeDate()
                );
                datePickerDialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                datePickerDialogFragment.show(getFragmentManager(), DATE_PICKER_DIALOG_TAG);
            }
        });

        return v;
    }

    //  This is used to persist crime as oppose to just set it in this activity and lose it
    // on a transition
    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setCrimeDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        mCrimeTimestampButton.setText(mCrime.getCrimeDate().toString());

    }
}
