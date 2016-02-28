package com.clothapp.profile_shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.home.HomeActivity;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.profile.utils.FollowUtil;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.profile_shop.fragments.ProfileShopUploadedPhotosFragment;
import com.clothapp.upload.UploadProfilePictureActivity;
import com.clothapp.profile_shop.adapters.SectionsPagerAdapterShop;
import com.clothapp.resources.CircleTransform;
import com.clothapp.settings.SettingsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Loading follow Button
        final Button follow_edit = (Button) findViewById(R.id.follow_edit);

        // Set toolbar title to empty string so that it won't overlap with the tabs.
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Set up drawer button
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_24dp_white);
        ab.setDisplayHomeAsUpEnabled(true);

        // Get the drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        viewPager = (ViewPager) findViewById(R.id.profile_viewpager);
        if (viewPager != null) {
            setupViewPagerContent(viewPager);
        }

        loadProfilePicture(navigationView);

        //tasto "segui" se profilo non tuo, "modifica profilo" se profilo tuo
        if (username.equals(ParseUser.getCurrentUser().getUsername())) {
            follow_edit.setText(R.string.edit_profile);
        }else {
            FollowUtil.setFollowButton(follow_edit,username);
        }
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
                // Log.d("ShopProfileActivity", "android.R.id.home");
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_settings:
                // Log.d("ShopProfileActivity", "R.id.action_settings");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        /*
        // Get default bitmap for user profile photo
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_square);

        // Create a rounded bitmap from the user profile photo
        RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        rounded.setCornerRadius(bitmap.getWidth());
        */
        // Get drawer header
        View headerLayout = navigationView.getHeaderView(0);

        // Get the image view containing the user profile photo
        ImageView drawerProfile = (ImageView) headerLayout.findViewById(R.id.menu_profile_side_drawer_image);

        // Set the user profile photo to the just created rounded image
        Glide.with(context)
                .load(R.drawable.com_facebook_profile_picture_blank_square)
                .centerCrop()
                .transform(new CircleTransform(ShopProfileActivity.this))
                .into(drawerProfile);
        //drawerProfile.setImageDrawable(rounded);

        // Set the drawer username
        TextView drawerUsername = (TextView) headerLayout.findViewById(R.id.menu_profile_side_drawer_username);
        drawerUsername.setText(username);

        // Set up onClickListener for each drawer item
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        Intent intent;

                        switch (menuItem.getItemId()) {

                            case R.id.nav_home:
                                Log.d("ShopProfileActivity", "Clicked on R.id.nav_home");

                                intent = new Intent(ShopProfileActivity.activity, HomeActivity.class);
                                startActivity(intent);

                                finish();
                                break;

                            case R.id.nav_profile:
                                Log.d("ShopProfileActivity", "Clicked on R.id.nav_profile");

                                String currentUser = ParseUser.getCurrentUser().getUsername();

                                if (!currentUser.equals(username)) {
                                    Log.d("ShopProfileActivity", currentUser + "!=" + username);
                                    intent = ProfileUtils.goToProfile(ShopProfileActivity.context,currentUser);
                                    intent.putExtra("user", currentUser);
                                    startActivity(intent);
                                }

                                break;


                            case R.id.nav_settings:
                                Log.d("HomeActivity", "Clicked on R.id.nav_settings");

                                intent = new Intent(ShopProfileActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;

                            case R.id.nav_logout:
                                Log.d("ShopProfileActivity", "Clicked on R.id.nav_logout");

                                final ProgressDialog dialog = ProgressDialog.show(ShopProfileActivity.this, "", "Logging out. Please wait...", true);
                                Thread logout = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ParseUser.logOut();
                                        System.out.println("debug: logout eseguito");
                                    }
                                });
                                logout.start();

                                intent = new Intent(ShopProfileActivity.activity, MainActivity.class);
                                dialog.dismiss();
                                startActivity(intent);

                                finish();
                                break;
                        }

                        // menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void setupViewPagerContent(ViewPager viewPager) {

        // Create new adapter for ViewPager
        SectionsPagerAdapterShop sectionsPagerAdapter = new SectionsPagerAdapterShop(getSupportFragmentManager());

        // Set ViewPager adapter
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.setCurrentItem(1, false);

        // Set up TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadProfilePicture(NavigationView navigationView) {

        View headerLayout = navigationView.getHeaderView(0);
        ImageView drawerImageView = (ImageView) headerLayout.findViewById(R.id.menu_profile_side_drawer_image);

        ImageView background = (ImageView) findViewById(R.id.profile_cover_image);

        ProfileUtils.getParseUserCopertina(this, username, background, drawerImageView, getApplicationContext());
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (username.equals(ParseUser.getCurrentUser().getUsername().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopProfileActivity.this);
                    builder.setTitle(R.string.choose_profile_picture)
                            //.set
                            .setItems(R.array.profile_picture_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getApplicationContext(), UploadProfilePictureActivity.class);
                                    switch (which) {
                                        case 0:
                                            // Redirect the user to the ProfilePictureActivity with camera
                                            i.putExtra("photoType",2187);
                                            startActivity(i);
                                            break;
                                        case 1:
                                            // Redirect the user to the ProfilePictureActivity with galery
                                            i.putExtra("photoType",1540);
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

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (ProfileShopUploadedPhotosFragment.adapter!=null) ProfileShopUploadedPhotosFragment.adapter.notifyDataSetChanged();
    }
}
