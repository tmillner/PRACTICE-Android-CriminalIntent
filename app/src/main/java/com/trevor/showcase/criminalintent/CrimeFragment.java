package com.trevor.showcase.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
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
    private Button mCrimeSuspectButton;
    private Button mSendReportButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private File mPhoto;


    private static final String ARG_BUNDLE_CRIME_ID = "crime_id";
    private static final String DATE_PICKER_DIALOG_TAG = "DatePickerDialog";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_SUSPECT_FROM_CONTACTS = 1;
    private static final int REQUEST_PHOTO = 2;


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

            mPhoto = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
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

        mSendReportButton = v.findViewById(R.id.send_crime_report_button);
        mSendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Instead of the below can use ShareCompat.IntentBuilder builder to
                // create intent
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.crime_report_suspect);

                Intent intentWithChooser = Intent.createChooser(
                        intent, getString(R.string.send_crime_report));
                startActivity(intentWithChooser);
            }
        });

        final Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        // Test the logic for if pickContactIntent is found on device actually disabled
        // pickContactIntent.addCategory(Intent.CATEGORY_HOME);

        mCrimeSuspectButton = v.findViewById(R.id.choose_suspect_button);
        mCrimeSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContactIntent, REQUEST_SUSPECT_FROM_CONTACTS);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
                == null) {
            mCrimeSuspectButton.setEnabled(false);
        }

        // This should be set when coming back from a pickContactIntent (onActivityResult)
        // If we rotate or by default launch, this needs to be set.
        if(mCrime.getCrimeSuspect() != null) {
            mCrimeSuspectButton.setText(mCrime.getCrimeSuspect());
        }

        final Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Boolean canTakePhoto = mPhoto != null &&
                takePhotoIntent.resolveActivity(packageManager) != null;
        mPhotoButton = v.findViewById(R.id.crime_camera_image_button);
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhoto);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(takePhotoIntent, REQUEST_PHOTO);
            }
        });

        mPhotoView = v.findViewById(R.id.crime_image_photo_view);
        updatePhotoView();

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
        else if (requestCode == REQUEST_SUSPECT_FROM_CONTACTS && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields,
                    null, null, null);

            try {
                // pull first column out of first row of data (we only projected 1 column)
                c.moveToFirst();
                String crimeSuspect = c.getString(0);
                mCrime.setCrimeSuspect(crimeSuspect);
                mCrimeSuspectButton.setText(crimeSuspect);
            }
            finally {
                c.close();
            }
        }
        // Technically we don't need the null check since we fire the intent with a
        // EXTRA_OUTPUT which causes data to be stored there, this updatePhotoView method
        // will do the right thing (read the location for data as was set from firing intent)
        else if (requestCode == REQUEST_PHOTO && data != null) {
            updatePhotoView();
        }
    }

    private void updateDate() {
        mCrimeTimestampButton.setText(mCrime.getCrimeDate().toString());

    }

    private void updatePhotoView() {
        if (mPhoto == null || !mPhoto.exists()) {
            mPhotoView.setImageDrawable(null);
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhoto.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private String getCrimeReport() {
        String crimeSolvedText;
        if (mCrime.isCrimeSolved()) {
            crimeSolvedText = getString(R.string.crime_report_solved);
        }
        else {
            crimeSolvedText = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getCrimeDate()).toString();

        String crimeSuspectText;
        if (mCrime.getCrimeSuspect() == null) {
            crimeSuspectText = getString(R.string.crime_report_no_suspect);
        }
        else {
            crimeSuspectText = getString(R.string.crime_report_suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(),
                dateString, crimeSolvedText, crimeSuspectText);

        return report;
    }
}
