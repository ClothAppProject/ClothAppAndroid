package com.clothapp.parse.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.parse.notifications.NotificationsUtils;
import com.clothapp.profile.adapters.PeopleListAdapter;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.profile.fragments.ProfileFollowersFragment;
import com.clothapp.resources.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by niccolò on 24/02/2016.
 */
public class FollowUtil {

    private static ParseObject relazione;
    private static ParseObject found = null;

    public static ParseObject isFollower(String from, String to) {

        ParseQuery<ParseObject> queryfollow = new ParseQuery("Follow");
        queryfollow.whereEqualTo("from", from);
        queryfollow.whereEqualTo("to", to);
        queryfollow.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                found = object;
            }
        });
        return found;
    }

    public static void setFollowButton(final Button follow_edit, final String usernameTo) {
        ParseQuery<ParseObject> queryfollow = new ParseQuery("Follow");
        queryfollow.whereEqualTo("from", ParseUser.getCurrentUser().getUsername());
        queryfollow.whereEqualTo("to", usernameTo);
        queryfollow.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                relazione = object;
                if (relazione != null) {
                    //Seguo l'utente, posso smettere di seguirlo
                    follow_edit.setText(R.string.unfollow);
                } else {
                    //Non seguo l'utente, posso seguirlo
                    follow_edit.setText(R.string.follow);
                }
                follow_edit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (relazione != null) {
                            // Elimino l'oggetto
                            relazione.deleteInBackground();
                            relazione = null;
                            follow_edit.setText(R.string.follow);

                        } else {

                            // Creo una nuova relazione
                            relazione = new ParseObject("Follow");
                            relazione.put("from", ParseUser.getCurrentUser().getUsername());
                            relazione.put("to", usernameTo);
                            relazione.saveInBackground();

                            // Send "Like" notification to the user who posted the image
                            NotificationsUtils.sendNotification(usernameTo, "follow");

                            follow_edit.setText(R.string.unfollow);
                        }
                    }
                });
            }
        });
    }

    public static void getFollowing(final List<User> users, final View rootView, final RecyclerView view, String username, final TextView noFollowing) {
        //funzione ausiliare per la query
        final PeopleListAdapter adapter = (PeopleListAdapter) view.getAdapter();
        ParseQuery<ParseObject> queryUser = new ParseQuery<ParseObject>("Follow");
        queryUser.addDescendingOrder("createdAt");
        queryUser.setSkip(users.size());
        queryUser.setLimit(15);
        queryUser.whereEqualTo("from", username);
        queryUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //check if user has no followers and set noFollowers text
                    if (users.isEmpty() && objects.isEmpty()) {
                        noFollowing.setText(R.string.no_following);
                        noFollowing.setVisibility(View.VISIBLE);
                    }
                    for (ParseObject o : objects) {
                        final User u = new User(o.getString("to"), null, null);
                        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
                        queryFoto.whereEqualTo("username", u.getUsername());
                        queryFoto.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (object != null) {
                                    ParseFile foto = object.getParseFile("thumbnail");
                                    foto.getFileInBackground(new GetFileCallback() {
                                        @Override
                                        public void done(File file, ParseException e) {
                                            u.setProfilo(file);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                        users.add(u);
                        //adapter.add(u);
                        adapter.notifyDataSetChanged();
                    }
                } else check(e.getCode(), rootView, e.getMessage());
            }
        });
    }

    public static void getFollower(final List<User> users, final View rootView, final RecyclerView view, final String username, final TextView noFollowers) {
        //funzione ausiliare per la query
        final PeopleListAdapter adapter = (PeopleListAdapter) view.getAdapter();
        ParseQuery<ParseObject> queryUser = new ParseQuery<ParseObject>("Follow");
        queryUser.addDescendingOrder("createdAt");
        queryUser.setSkip(users.size());
        queryUser.setLimit(15);
        queryUser.whereEqualTo("to", username);
        queryUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //check if user has no followers and set noFollowers text
                    if (users.isEmpty() && objects.isEmpty()) {
                        noFollowers.setText(R.string.no_followers);
                        noFollowers.setVisibility(View.VISIBLE);
                    }
                    for (ParseObject o : objects) {
                        final User u = new User(o.getString("from"), null, null);
                        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
                        queryFoto.whereEqualTo("username", u.getUsername());
                        queryFoto.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (object != null) {
                                    ParseFile parseFile = object.getParseFile("thumbnail");
                                    parseFile.getFileInBackground(new GetFileCallback() {
                                        @Override
                                        public void done(File file, ParseException e) {
                                            u.setProfilo(file);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                        users.add(u);
                        //adapter.add(u);
                        adapter.notifyDataSetChanged();
                    }
                } else check(e.getCode(), rootView, e.getMessage());
            }
        });
    }

    public static ParseUser getParseUser(String username) {

        ParseUser user = null;

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("username", username);

        try {
            user = queryUser.find().get(0);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return user;
    }
}
