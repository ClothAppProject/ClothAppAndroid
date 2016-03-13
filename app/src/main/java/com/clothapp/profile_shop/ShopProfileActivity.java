package com.clothapp.profile_shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.home.HomeActivity;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.parse.notifications.FollowUtil;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.profile_shop.fragments.ProfileShopUploadedPhotosFragment;
import com.clothapp.settings.EditShopProfileActivity;
import com.clothapp.upload.UploadProfilePictureActivity;
import com.clothapp.profile_shop.adapters.SectionsPagerAdapterShop;
import com.clothapp.resources.CircleTransform;
import com.clothapp.settings.SettingsActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;
import static com.clothapp.resources.RegisterUtil.setButtonTint;

public class ShopProfileActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    public static Context context;

    public static String username;
    private ParseObject relazione;
    public static RecyclerView viewProfileInfo;
    public static RecyclerView viewProfileUploadedPhotos;
    public static RecyclerView viewProfileShopFollowers;
    public static RecyclerView viewProfileShopFollowing;
    public static ViewPager viewPager;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_shop);

        // Get username from the calling activity.
        username = getIntent().getExtras().getString("user");

        // Set context to current context.
        context = ShopProfileActivity.this;

        // Set activity to current activity.
        activity = this;

        // Get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set toolbar title to empty string so that it won't overlap with the tabs.
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        initDrawer(toolbar);

        viewPager = (ViewPager) findViewById(R.id.profile_viewpager);
        if (viewPager != null) {
            setupViewPagerContent(viewPager);
        }

        loadProfilePicture();

        // Loading follow Button
        final Button follow_edit = (Button) findViewById(R.id.follow_edit);
        //coloro pulsanti twitter e facebook su API 21
        setButtonTint(follow_edit,getResources().getColorStateList(R.color.white));

        //tasto "segui" se profilo non tuo, "modifica profilo" se profilo tuo
        if (username.equals(ParseUser.getCurrentUser().getUsername())) {
            follow_edit.setText(R.string.edit_profile);
            follow_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ShopProfileActivity.this, EditShopProfileActivity.class);
                    startActivity(i);
                }
            });
        } else {
            FollowUtil.setFollowButton(follow_edit, username);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // Log.d("ShopProfileActivity", "android.R.id.home");
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        loadProfilePicture();
        if (ProfileShopUploadedPhotosFragment.adapter != null)
            ProfileShopUploadedPhotosFragment.adapter.notifyDataSetChanged();
    }

    private void initDrawer(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.open_navigation, R.string.close_navigation);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Setup OnClickListener for the navigation drawer.
        navigationView.setNavigationItemSelectedListener(new ProfileNavigationItemSelectedListener());

        // Get drawer header
        View headerLayout = navigationView.getHeaderView(0);

        // Get the image view containing the user profile photo
        final ImageView drawerProfile = (ImageView) headerLayout.findViewById(R.id.navigation_drawer_profile_photo);
        TextView drawerUsername = (TextView) headerLayout.findViewById(R.id.navigation_drawer_profile_username);
        TextView drawerRealName = (TextView) headerLayout.findViewById(R.id.navigation_drawer_profile_real_name);

        // Set the user profile photo to the just created rounded image
        Glide.with(context)
                .load(R.drawable.com_facebook_profile_picture_blank_square)
                .transform(new CircleTransform(context))
                .into(drawerProfile);

        ParseUser currentUser = ParseUser.getCurrentUser();
        drawerUsername.setText(capitalize(currentUser.getUsername()));
        drawerRealName.setText(capitalize(currentUser.getString("name")));

        if (HomeActivity.drawerProfilePhotoFile == null) {
            ParseQuery<ParseObject> query = new ParseQuery<>("UserPhoto");
            query.whereEqualTo("username", currentUser.getUsername());

            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject photo, ParseException e) {

                    if (e == null) {
                        Log.d("HomeActivity", "ParseObject for profile image found!");

                        ParseFile parseFile = photo.getParseFile("thumbnail");
                        parseFile.getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File file, ParseException e) {

                                if (e == null) {
                                    Log.d("HomeActivity", "File for profile image found!");

                                    HomeActivity.drawerProfilePhotoFile = file;

                                    // Set the user profile photo to the just created rounded image
                                    Glide.with(context)
                                            .load(file)
                                            .transform(new CircleTransform(context))
                                            .into(drawerProfile);

                                } else {
                                    Log.d("HomeActivity", "Error: " + e.getMessage());
                                }
                            }
                        });

                    } else {
                        Log.d("HomeActivity", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            // Set the user profile photo to the just created rounded image
            Glide.with(context)
                    .load(HomeActivity.drawerProfilePhotoFile)
                    .transform(new CircleTransform(context))
                    .into(drawerProfile);
        }
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private void setupViewPagerContent(ViewPager viewPager) {

        // Create new adapter for ViewPager
        SectionsPagerAdapterShop sectionsPagerAdapter = new SectionsPagerAdapterShop(getSupportFragmentManager(),getApplicationContext());

        // Set ViewPager adapter
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.setCurrentItem(1, false);

        // Set up TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadProfilePicture() {

        ImageView background = (ImageView) findViewById(R.id.profile_cover_image);
        ProfileUtils.getParseUserProfileImage(this, username, background, ShopProfileActivity.context, true);

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopProfileActivity.this);
                    builder.setTitle(R.string.choose_profile_picture)
                            //.set
                            .setItems(R.array.profile_picture_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getApplicationContext(), UploadProfilePictureActivity.class);
                                    switch (which) {
                                        case 0:
                                            // Redirect the user to the ProfilePictureActivity with camera
                                            i.putExtra("photoType", 2187);
                                            startActivity(i);
                                            break;
                                        case 1:
                                            // Redirect the user to the ProfilePictureActivity with galery
                                            i.putExtra("photoType", 1540);
                                            startActivity(i);
                                            break;
                                        case 2:
                                            //delete profile picture
                                            ParseQuery<ParseObject> queryFotoProfilo = new ParseQuery<ParseObject>("UserPhoto");
                                            queryFotoProfilo.whereEqualTo("username", username);
                                            queryFotoProfilo.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {
                                                    if (e == null) {
                                                        if (objects.size() > 0) {
                                                            objects.get(0).deleteInBackground();
                                                            activity.finish();
                                                            activity.startActivity(activity.getIntent());
                                                        }
                                                    } else {
                                                        check(e.getCode(), v, e.getMessage());
                                                    }
                                                }
                                            });
                                            break;
                                    }
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    // This class handles click to each item of the navigation drawer
    class ProfileNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            Intent intent;

            switch (item.getItemId()) {

                // Clicked on "Home" page button.
                case R.id.nav_home:

                    Log.d("UserProfileActivity", "Clicked on R.id.nav_home");

                    intent = new Intent(ShopProfileActivity.activity, HomeActivity.class);
                    startActivity(intent);

                    finish();
                    break;

                // Clicked on "My Profile" item.
                case R.id.nav_profile:

                    Log.d("UserProfileActivity", "Clicked on R.id.nav_profile");

                    String currentUser = ParseUser.getCurrentUser().getUsername();

                    if (!currentUser.equals(username)) {
                        Log.d("UserProfileActivity", currentUser + "!=" + username);
                        intent = ProfileUtils.goToProfile(ShopProfileActivity.context, currentUser);
                        intent.putExtra("user", currentUser);
                        startActivity(intent);
                    }

                    break;

                // Clicked on "Settings" item.
                case R.id.nav_settings:
                    Log.d("HomeActivity", "Clicked on R.id.nav_settings");

                    intent = new Intent(ShopProfileActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;

                // Clicked on "Logout" item.
                case R.id.nav_logout:

                    Log.d("UserProfileActivity", "Clicked on R.id.nav_logout");

                    final ProgressDialog dialog = ProgressDialog.show(ShopProfileActivity.this, "", "Logging out. Please wait...", true);
                    Thread logout = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ParseUser.logOut();
                            Log.d("UserProfileActivity", "Successfully logged out");
                        }
                    });
                    logout.start();

                    intent = new Intent(ShopProfileActivity.activity, MainActivity.class);
                    dialog.dismiss();
                    startActivity(intent);

                    finish();
                    break;

                // Clicked on "Feedback" item.
                case R.id.nav_feedback:
                    Log.d("ShopProfileActivity", "Clicked on R.id.nav_logout");


                    Intent mail = new Intent(Intent.ACTION_SENDTO);
                    mail.setData(Uri.parse("mailto:")); // only email apps should handle this
                    mail.putExtra(Intent.EXTRA_EMAIL, new String[]{"clothapp.project@gmail.com"});
                    mail.putExtra(Intent.EXTRA_SUBJECT, "ClothApp Feedback");
                    if (mail.resolveActivity(getPackageManager()) != null) {
                        startActivity(mail);
                    }
                    break;

            }

            // Close the navigation drawer after item selection.
            ShopProfileActivity.this.mDrawerLayout.closeDrawer(GravityCompat.START);

            return true;
        }
    }
}
