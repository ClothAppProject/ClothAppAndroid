package com.clothapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.home.HomeActivity;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.resources.CircleTransform;
import com.clothapp.settings.SettingsActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by giacomoceribelli on 26/04/16.
 */
public class Menu {
    private static String username;
    public static void initMenu(DrawerLayout mDrawerLayout, final Context context, NavigationView navigationView,
                                ActionBarDrawerToggle toggle, String activity, String user, Context dialogContext) {
        username = user;
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        // Setup OnClickListener for the navigation drawer.
        navigationView.setNavigationItemSelectedListener(
                new HomeNavigationItemSelectedListener(activity, context, mDrawerLayout, dialogContext));

        // Get drawer header
        View headerLayout = navigationView.getHeaderView(0);

        // Get the image view containing the user profile photo
        final ImageView drawerProfile = (ImageView) headerLayout.findViewById(R.id.navigation_drawer_profile_photo);
        TextView drawerUsername = (TextView) headerLayout.findViewById(R.id.navigation_drawer_profile_username);
        TextView drawerRealName = (TextView) headerLayout.findViewById(R.id.navigation_drawer_profile_real_name);

        ParseUser currentUser = ParseUser.getCurrentUser();
        drawerUsername.setText(capitalize(currentUser.getUsername()));
        drawerRealName.setText(capitalize(currentUser.getString("name")));

        ParseQuery<ParseObject> query = new ParseQuery<>("UserPhoto");
        query.whereEqualTo("username", currentUser.getUsername());

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject photo, ParseException e) {
                if (e == null) {
                    Log.d("HomeActivity", "ParseObject for profile image found!");

                    ParseFile parseFile = photo.getParseFile("thumbnail");
                    String url = parseFile.getUrl();
                    Glide.with(context)
                            .load(url)
                            .transform(new CircleTransform(context))
                            .into(drawerProfile);
                    }
            }
        });

        //Set actual tab for item selected
        if (activity.equals("home")) {
            navigationView.getMenu().getItem(0).setChecked(true);
        }else if (activity.equals("profilo") && username.equals(ParseUser.getCurrentUser().getUsername()))   {
            navigationView.getMenu().getItem(1).setChecked(true);
        }
    }

    private static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    // This class handles click to each item of the navigation drawer
    static class HomeNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        private Context context;
        private Context dialogContext;
        private DrawerLayout mDrawerLayout;
        private String activity;

        public HomeNavigationItemSelectedListener(String activity, Context context, DrawerLayout mDrawerLayout, Context dialogContext)  {
            this.activity = activity;
            this.context = context;
            this.mDrawerLayout = mDrawerLayout;
            this.dialogContext = dialogContext;
        }
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            Intent intent;

            switch (item.getItemId()) {

                // Clicked on "Home" page button.
                // Do nothing since we already are in the home page.
                case R.id.nav_home:

                    Log.d("Menu", "Clicked on R.id.nav_settings");
                    if (!activity.equals("home")) {
                        intent = new Intent(context, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    break;

                // Clicked on "My Profile" item.
                case R.id.nav_profile:

                    Log.d("Menu", "Clicked on R.id.nav_profile");
                    if (!(activity.equals("profilo") && username.equals(ParseUser.getCurrentUser().getUsername()))) {
                        if (ParseUser.getCurrentUser().get("flagISA").equals("Persona")) {
                            intent = new Intent(context, UserProfileActivity.class);
                        } else {
                            intent = new Intent(context, ShopProfileActivity.class);
                        }
                        intent.putExtra("user", ParseUser.getCurrentUser().getUsername());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    break;

                //Clicked on "Settings"
                case R.id.nav_settings:
                    Log.d("Menu", "Clicked on R.id.nav_settings");

                    intent = new Intent(context, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;

                // Clicked on "Logout" item.
                case R.id.nav_logout:

                    Log.d("Menu", "Clicked on R.id.nav_logout");
                    ProgressDialog dialog = ProgressDialog.show(dialogContext, "", "Logging out. Please wait...", true);
                    Thread logout = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ParseUser.logOut();
                            System.out.println("debug: logout eseguito");
                        }
                    });
                    logout.start();

                    intent = new Intent(context, MainActivity.class);
                    dialog.dismiss();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;

                // Clicked on "Feedback" item.
                case R.id.nav_feedback:
                    Log.d("Menu", "Clicked on R.id.nav_logout");


                    Intent mail = new Intent(Intent.ACTION_SENDTO);
                    mail.setData(Uri.parse("mailto:")); // only email apps should handle this
                    mail.putExtra(Intent.EXTRA_EMAIL, new String[]{"clothapp.project@gmail.com"});
                    mail.putExtra(Intent.EXTRA_SUBJECT, "ClothApp Feedback");
                    if (mail.resolveActivity(context.getPackageManager()) != null) {
                        mail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(mail);
                    }
                    break;

            }

            // Close the navigation drawer after item selection.
            mDrawerLayout.closeDrawer(GravityCompat.START);

            return true;
        }
    }
}
