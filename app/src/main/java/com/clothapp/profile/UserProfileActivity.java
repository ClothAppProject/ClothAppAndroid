package com.clothapp.profile;

import android.app.AlertDialog;
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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.clothapp.Menu;
import com.clothapp.R;
import com.clothapp.profile.adapters.SectionsPagerAdapter;
import com.clothapp.parse.notifications.FollowUtil;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.settings.EditProfileActivity;
import com.parse.ParseUser;
import static com.clothapp.resources.RegisterUtil.setButtonTint;

public class UserProfileActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context context;
    private String username;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        // Get username from the calling activity.
        username = getIntent().getExtras().getString("user");

        // Set context to current context.
        context = getApplicationContext();

        // Get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set toolbar title to empty string so that it won't overlap with the tabs.
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.open_navigation, R.string.close_navigation);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu.initMenu(mDrawerLayout, context, navigationView, toggle, "profilo", username, UserProfileActivity.this);

        // Get the view pager and setup its content
        viewPager = (ViewPager) findViewById(R.id.profile_viewpager);
        if (viewPager != null) {
            setupViewPagerContent(viewPager);
        }

        // Load profile picture
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
                    Intent i = new Intent(context, EditProfileActivity.class);
                    startActivity(i);

                }
            });
        } else {
            FollowUtil.setFollowButton(follow_edit, username, ParseUser.getCurrentUser().getObjectId() );
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // Log.d("UserProfileActivity", "android.R.id.home");
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
    }

    private void setupViewPagerContent(ViewPager viewPager) {

        // Create new adapter for ViewPager
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),getApplicationContext(), username);

        // Set ViewPager adapter
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.setCurrentItem(1, false);

        // Set up TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadProfilePicture() {

        final ImageView mainImageView = (ImageView) findViewById(R.id.profile_user_image);
        ProfileUtils.getParseUserProfileImage(username, mainImageView, context, false, UserProfileActivity.this);


    }

}

