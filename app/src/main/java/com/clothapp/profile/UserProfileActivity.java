package com.clothapp.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clothapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserProfileActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private DrawerLayout mDrawerLayout;

    static Context context;

    static String username;

    static RecyclerView viewProfileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        // Get username from the calling activity.
        username = getIntent().getExtras().getString("user");

        // Set context to current context.
        context = UserProfileActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_24dp_white);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_square);

        RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

        rounded.setCornerRadius(bitmap.getWidth());

        // ImageView drawerProfile = (ImageView) findViewById(R.id.menu_profile_side_drawer_image);
        View headerLayout = navigationView.getHeaderView(0);
        ImageView drawerProfile = (ImageView) headerLayout.findViewById(R.id.menu_profile_side_drawer_image);
        drawerProfile.setImageDrawable(rounded);

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.profile_viewpager);
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

        switch (item.getItemId()) {

            case android.R.id.home:
                // Log.d("UserProfileActivity", "android.R.id.home");
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_settings:
                // Log.d("UserProfileActivity", "R.id.action_settings");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });
    }

    // This fragment contains info about the user.
//    public static class ProfileInfoFragment extends Fragment {
//
//        private static final String PARSE_USERNAME = "username";
//
//        public ProfileInfoFragment() {
//
//        }
//
//        public static ProfileInfoFragment newInstance(String username) {
//            ProfileInfoFragment fragment = new ProfileInfoFragment();
//            Bundle args = new Bundle();
//            args.putString(PARSE_USERNAME, username);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_profile_info, container, false);
//
//            viewProfileInfo = (RecyclerView) rootView.findViewById(R.id.profile_info_recycler_view);
//
//            LinearLayoutManager llm = new LinearLayoutManager(context);
//            viewProfileInfo.setLayoutManager(llm);
//
//            ArrayList<ProfileInfoListItem> items = new ArrayList<>();
//
//            ProfileInfoListItem itemDummy = new ProfileInfoListItem("DUMMY", "Loading...");
//            ProfileInfoListItem itemName = new ProfileInfoListItem("NAME", "Loading...");
//            ProfileInfoListItem itemAge = new ProfileInfoListItem("AGE", "Loading...");
//            ProfileInfoListItem itemCity = new ProfileInfoListItem("CITY", "Loading...");
//            ProfileInfoListItem itemEmail = new ProfileInfoListItem("EMAIL", "Loading...");
//            ProfileInfoListItem itemDescription = new ProfileInfoListItem("DESCRIPTION", "Loading...");
//
//            items.add(itemDummy);
//            items.add(itemName);
//            items.add(itemAge);
//            items.add(itemCity);
//            items.add(itemEmail);
//            items.add(itemDescription);
//
//            ProfileInfoAdapter adapter = new ProfileInfoAdapter(items);
//            viewProfileInfo.setAdapter(adapter);
//
//            // Get user info from Parse
//            ProfileUtils.getParseInfo(UserProfileActivity.context, UserProfileActivity.username);
//
//            return rootView;
//        }
//
//    }
//
//    // PagerAdapter for tabs and associated fragments.
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//
//            switch (position) {
//                case 0:
//                    return ProfileInfoFragment.newInstance(username);
//                default:
//                    return PlaceholderFragment.newInstance(position + 1);
//            }
//        }
//
//        @Override
//        public int getCount() {
//            // Number of pages.
//            return 7;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "INFO";
//                case 1:
//                    return "UPLOADED PHOTOS";
//                case 2:
//                    return "FOLLOWERS";
//                case 3:
//                    return "FOLLOWING";
//                case 4:
//                    return "FAVORITE PHOTOS";
//                case 5:
//                    return "FAVORITE BRANDS";
//                case 6:
//                    return "FAVORITE SHOPS";
//            }
//            return null;
//        }
//    }
}


//// This class helps keeping the code clean and modular.
//class ProfileUtils {
//
//    // Object to store info about the user (not necessarily the current user).
//    static ParseUser user;
//    // Object to store info about the "Persona" associated with the user above.
//    static ParseObject person;
//
//
//    // Get info about the user from Parse.
//    // Call getParseUser() and getParsePerson().
//    static void getParseInfo(final Context context, String username) {
//        getParseUser(context, username);
//        getParsePerson(context, username);
//    }
//
//    // Gets a ParseUser object for the given username.
//    // The context arguments is needed to show a dialog in case of success or failure.
//    private static void getParseUser(final Context context, final String username) {
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereEqualTo("username", username);
//
//        query.getFirstInBackground(new GetCallback<ParseUser>() {
//
//            @Override
//            public void done(ParseUser object, ParseException e) {
//                if (e == null) {
//                    user = object;
//                    // showDialog(context, "Success", "Successfully retrieved user info from Parse.");
//
//                    updateListItem(0, user.get("name").toString());
//                    updateListItem(3, user.getEmail());
//
//                } else {
//                    e.printStackTrace();
//                    // showDialog(context, "Error", "Failed to retrieve user info. Check your Internet connection.");
//                }
//            }
//        });
//    }
//
//    // Gets a ParseObject ("Persona") object for the given username.
//    // The context arguments is needed to show a dialog in case of success or failure.
//    private static void getParsePerson(final Context context, String username) {
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Persona");
//        query.whereEqualTo("username", username);
//
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    person = object;
//                    // showDialog(context, "Success", "Successfully retrieved person info from Parse.");
//
//                    long age = getAge(person.get("date").toString());
//
//                    if (age < 0) updateListItem(1, "Not found");
//                    else updateListItem(1, age + " years old");
//
//                    updateListItem(2, person.get("city").toString());
//
//                } else {
//                    e.printStackTrace();
//                    // showDialog(context, "Error", "Failed to retrieve person info. Check your Internet connection.");
//                }
//            }
//        });
//    }
//
//    private static void updateListItem(int position, String text) {
//
//        ProfileInfoAdapter adapter = (ProfileInfoAdapter) UserProfileActivity.viewProfileInfo.getAdapter();
//
//        ProfileInfoListItem item = adapter.items.get(position + 1);
//        item.setContent(text);
//
//        adapter.notifyDataSetChanged();
//    }
//
//    private static long getAge(String text) {
//
//        try {
//            DateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
//            Date birthday = format.parse(text);
//            Date now = new Date();
//
//            long diffInMillies = now.getTime() - birthday.getTime();
//            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;
//
//        }  catch (java.text.ParseException e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }
//
//    // Shows a simple dialog with a title, a message and two buttons.
//    // The context argument is needed to show the dialog inside the UserProfileActivity activity.
//    private static void showDialog(Context context, String title, String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(title);
//        builder.setMessage(message);
//
//        String positiveText = "OK";
//        builder.setPositiveButton(positiveText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // positive button logic
//                    }
//                });
//
//        String negativeText = "CANCEL";
//        builder.setNegativeButton(negativeText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // negative button logic
//                    }
//                });
//
//        AlertDialog dialog = builder.create();
//        // display dialog
//        dialog.show();
//    }
//}

//class ProfileInfoListItem {
//    private String title;
//    private String content;
//
//    public ProfileInfoListItem(String title, String content) {
//        this.title = title;
//        this.content = content;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//}
//
//class ProfileInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    List<ProfileInfoListItem> items;
//
//    private final static int ITEM_TYPE_HEADER = 0;
//    private final static int ITEM_TYPE_INFO = 1;
//
//    public ProfileInfoAdapter(List<ProfileInfoListItem> items) {
//        this.items = items;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        switch (viewType) {
//
//            case (ITEM_TYPE_HEADER): {
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_info_list_header, parent, false);
//                HeaderViewHolder headerViewHolder = new HeaderViewHolder(v);
//                return headerViewHolder;
//            }
//
//            case (ITEM_TYPE_INFO): {
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_info_list_item, parent, false);
//                InfoViewHolder infoViewHolder = new InfoViewHolder(v);
//                return infoViewHolder;
//            }
//
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//        switch (getItemViewType(position)) {
//
//            case ITEM_TYPE_HEADER:
//                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
//                String username = UserProfileActivity.username.substring(0, 1).toUpperCase() + UserProfileActivity.username.substring(1);
//                headerViewHolder.txtUsername.setText(username);
//
//                break;
//
//            case ITEM_TYPE_INFO:
//                InfoViewHolder infoViewHolder = (InfoViewHolder) holder;
//                infoViewHolder.txtTitle.setText(items.get(position).getTitle());
//                infoViewHolder.txtContent.setText(items.get(position).getContent());
//
//                break;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return (position == ITEM_TYPE_HEADER) ? ITEM_TYPE_HEADER : ITEM_TYPE_INFO;
//    }
//
//    public static class InfoViewHolder extends RecyclerView.ViewHolder {
//
//        TextView txtTitle;
//        TextView txtContent;
//
//        InfoViewHolder(View itemView) {
//            super(itemView);
//
//            txtTitle = (TextView) itemView.findViewById(R.id.profile_info_list_item_title);
//            txtContent = (TextView) itemView.findViewById(R.id.profile_info_list_item_content);
//        }
//    }
//
//    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
//
//        TextView txtUsername;
//
//        HeaderViewHolder(View itemView) {
//            super(itemView);
//
//            txtUsername = (TextView) itemView.findViewById(R.id.profile_info_list_header_title);
//        }
//    }
//
//}
