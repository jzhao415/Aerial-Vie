package com.example.jeffzhao415.vie;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jeffzhao415.perferencedata.PreferenceData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.lang.reflect.Array;
import java.util.Locale;


public class MainActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    static private ArrayAdapter<String> fallLogAdapter;
    static private ArrayAdapter<String> contactAdapter;
    static private ArrayAdapter<String> heartRateLogAdapter;
    static private ArrayAdapter<String> pillReminderAdapter;
    static String[] fallLog = new String[]{
            "Fall Detected! \nTime: 05:18 PM 2015-09-06 \nLocation:39.808176,-75.5011308",
            "Fall Detected! \nTime: 02:23 AM 2015-09-03 \nLocation:39.808176,-75.5011308",
            "Fall Detected! \nTime: 01:04 PM 2015-09-02 \nLocation:39.808176,-75.5011308",
            "Fall Detected! \nTime: 03:30 PM 2015-09-01 \nLocation:39.808176,-75.5011308"
    };

    static String[] contacts = new String[]{
            "Michelle Jordan      (532)519-5101\n",
            "Sean Jacobs          (072)064-3613\n",
            "Mark Hudson          (652)298-6919\n",
            "Wayne Lawson         (479)492-9543\n",
            "Rebecca Hall         (893)099-7969\n",
            "Laura Mccoy          (748)121-4050"
    };

    static String[] heartRateLog = new String[]{
            "9/8/2015 1:48 PM   55 bpm",
            "9/7/2015 2:30 PM   58 bpm",
            "9/6/2015 1:48 PM   57 bpm",
            "9/6/2015 1:07 PM   52 bpm",
    };

    static String[] pillReminder = new String[]{
            "glucosamine HCI-msm\n1 Liquid 1,500-500 mg/3..   \n7:30AM",
            "Vitamin C  \n1 Tablet 500mg   \n7:45AM",
            "dietary Supplement \n1 Capsule    \n7:45AM"
    };

    // Preference Keys
    public static final String PREF_KEY_SMS_NUMBER = "_contact_phone_number";
    public static final String PREF_KEY_SMS_MESSAGE = "_sms_emergency_message";
    public static final String PREF_KEY_SMS_MESSAGE_LOCATION = "_sms_send_location";
    public static final String PREF_KEY_SHOW_NOTIFICATION = "_show_notification";
    public static final String PREF_KEY_USE_CONFIRMATION_BTN = "_use_confirmation_button";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        fallLogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, fallLog);
        contactAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contacts);
        heartRateLogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, heartRateLog);
        pillReminderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, pillReminder);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listView = (ListView)rootView.findViewById(R.id.listView);
            Bundle bundle = this.getArguments();
            if(bundle != null){
                int sectionNum = bundle.getInt(ARG_SECTION_NUMBER);
                switch(sectionNum){
                    case 1:
                        listView.setAdapter(fallLogAdapter);
                        break;
                    case 2:
                        listView.setAdapter(contactAdapter);
                        break;
                    case 3:
                        listView.setAdapter(heartRateLogAdapter);
                        break;
                    case 4:
                        listView.setAdapter(pillReminderAdapter);
                }

            }
            return rootView;
        }
    }

    public static class PreferenceFragement extends PreferenceFragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{


        // Members
        private GoogleApiClient mGoogleApiClient = null;

        // Preferences
        private CheckBoxPreference mmPrefUseConfirmationBtn = null;
        private EditTextPreference mmPrefSmsNumber = null;
        private EditTextPreference mmPrefSmsMessage = null;

        public static PreferenceFragement newInstance(){
            return new PreferenceFragement();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.perfs_config);

            mmPrefUseConfirmationBtn = (CheckBoxPreference) findPreference(
                    PREF_KEY_USE_CONFIRMATION_BTN);
            mmPrefUseConfirmationBtn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setUseConfirmationButtonPreference((Boolean) newValue);
                    return true;
                }
            });

            mmPrefSmsNumber = (EditTextPreference) findPreference(PREF_KEY_SMS_NUMBER);
            if (mmPrefSmsNumber != null) {
                if (!TextUtils.isEmpty(mmPrefSmsNumber.getText())) {
                    mmPrefSmsNumber.setSummary(mmPrefSmsNumber.getText());
                }
                mmPrefSmsNumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        String newProxyHostValue = (String) newValue;
                        mmPrefSmsNumber.setSummary(newProxyHostValue);
                        return true;
                    }
                });
            }

            mmPrefSmsMessage = (EditTextPreference) findPreference(PREF_KEY_SMS_MESSAGE);
            if (mmPrefSmsMessage != null) {
                if (!TextUtils.isEmpty(mmPrefSmsMessage.getText())) {
                    mmPrefSmsMessage.setSummary(mmPrefSmsMessage.getText());
                }
                mmPrefSmsMessage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        String newProxyPortValue = (String) newValue;
                        mmPrefSmsMessage.setSummary(newProxyPortValue);
                        return true;
                    }
                });
            }

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();

        }

        @Override
        public void onDestroy(){
            if((mGoogleApiClient != null) && mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
            }
            super.onDestroy();
        }
        @Override
        public void onConnected(Bundle bundle) {
            if(mmPrefUseConfirmationBtn != null){
                setUseConfirmationButtonPreference(mmPrefUseConfirmationBtn.isChecked());
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        private void setUseConfirmationButtonPreference(boolean newValue) {
            (new SetPreferenceTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newValue);
        }

        private class SetPreferenceTask extends AsyncTask<Boolean, Void, Boolean> {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Boolean doInBackground(Boolean... params) {
                if (params.length != 1) {
                    throw new IllegalArgumentException("At least one boolean value must be set, but only one.");
                }

                if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
                    PutDataMapRequest dataMap = PreferenceData.toDataMap(params[0]);
                    PutDataRequest request = dataMap.asPutDataRequest();
                    Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                    return true;
                }
                return false;
            }
        }
    }

}
