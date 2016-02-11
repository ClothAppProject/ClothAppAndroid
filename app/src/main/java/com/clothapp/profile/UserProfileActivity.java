package com.clothapp.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clothapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class UserProfileActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    static Context context;

    static String username;

    static TextView txtName;
    static TextView txtAge;
    static TextView txtCity;
    static TextView txtEmail;
    static TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        // Get username from the calling activity.
        username = getIntent().getExtras().getString("user");

        // Set context to current context.
        context = UserProfileActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_user, menu);
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


    // This is a placeholder fragment for unimplemented sections.
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_placeholder, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    // This fragment contains info about the user.
    public static class ProfileInfoFragment extends Fragment {

        private static final String PARSE_USERNAME = "username";

        public ProfileInfoFragment() {

        }

        public static ProfileInfoFragment newInstance(String username) {
            ProfileInfoFragment fragment = new ProfileInfoFragment();
            Bundle args = new Bundle();
            args.putString(PARSE_USERNAME, username);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_info, container, false);

            TextView txtUsername = (TextView) rootView.findViewById(R.id.profile_card_username);
            txtUsername.setText(getArguments().getString(PARSE_USERNAME));

            UserProfileActivity.txtName = (TextView) rootView.findViewById(R.id.profile_card_name);
            UserProfileActivity.txtAge = (TextView) rootView.findViewById(R.id.profile_card_age);
            UserProfileActivity.txtCity = (TextView) rootView.findViewById(R.id.profile_card_city);
            UserProfileActivity.txtEmail = (TextView) rootView.findViewById(R.id.profile_card_email);
            UserProfileActivity.txtDescription = (TextView) rootView.findViewById(R.id.profile_card_description);

            txtName.setText("Loading...");
            txtAge.setText("Loading...");
            txtCity.setText("Loading...");
            txtEmail.setText("Loading...");
            txtDescription.setText("Loading...");

            // Get user info from Parse
            ProfileUtils.getParseInfo(UserProfileActivity.context, UserProfileActivity.username);

            return rootView;
        }


    }

    // PagerAdapter for tabs and associated fragments.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            switch (position) {
                case 0:
                    return ProfileInfoFragment.newInstance(username);
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Number of pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INFO";
                case 1:
                    return "UPLOADED PHOTOS";
                case 2:
                    return "FOLLOWERS";
                case 3:
                    return "FOLLOWING";
                case 4:
                    return "FAVORITE PHOTOS";
                case 5:
                    return "FAVORITE BRANDS";
                case 6:
                    return "FAVORITE SHOPS";
            }
            return null;
        }
    }
}


// This class helps keeping the code clean and modular.
class ProfileUtils {

    // Object to store info about the user (not necessarily the current user).
    static ParseUser user;
    // Object to store info about the "Persona" associated with the user above.
    static ParseObject person;


    // Get info about the user from Parse.
    // Call getParseUser() and getParsePerson().
    static void getParseInfo(final Context context, String username) {
        getParseUser(context, username);
        getParsePerson(context, username);
    }

    // Gets a ParseUser object for the given username.
    // The context arguments is needed to show a dialog in case of success or failure.
    private static void getParseUser(final Context context, final String username) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseUser>() {

            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    user = object;
                    showDialog(context, "Success", "Successfully retrieved user info from Parse.");

                    UserProfileActivity.txtName.setText("NAME: " + user.get("name"));
                    UserProfileActivity.txtEmail.setText("EMAIL: " + user.get("email"));

                } else {
                    e.printStackTrace();
                    showDialog(context, "Error", "Failed to retrieve user info. Check your Internet connection.");
                }
            }
        });
    }

    // Gets a ParseObject ("Persona") object for the given username.
    // The context arguments is needed to show a dialog in case of success or failure.
    private static void getParsePerson(final Context context, String username) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Persona");
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    person = object;
                    showDialog(context, "Success", "Successfully retrieved person info from Parse.");

                    UserProfileActivity.txtAge.setText("AGE: " + person.get("date"));
                    UserProfileActivity.txtCity.setText("CITY: " + person.get("city"));

                } else {
                    e.printStackTrace();
                    showDialog(context, "Error", "Failed to retrieve person info. Check your Internet connection.");
                }
            }
        });
    }

    // Shows a simple dialog with a title, a message and two buttons.
    // The context argument is needed to show the dialog inside the UserProfileActivity activity.
    private static void showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        String positiveText = "OK";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = "CANCEL";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
}
