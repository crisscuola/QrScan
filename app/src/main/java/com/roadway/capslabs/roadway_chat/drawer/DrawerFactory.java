package com.roadway.capslabs.roadway_chat.drawer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.roadway.capslabs.roadway_chat.R;
import com.roadway.capslabs.roadway_chat.activity.QrScannerActivity;
import com.roadway.capslabs.roadway_chat.auth.ActivityAuth;
import com.roadway.capslabs.roadway_chat.network.LoginHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kirill on 12.09.16
 */
public class DrawerFactory {

    public DrawerBuilder getDrawerBuilder(final Activity activity, Toolbar toolbar) {
        final DrawerBuilder drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(getAccountHeader(activity))
                .addDrawerItems(
                        getDrawerItems()
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Class<? extends Activity> toActivity = getActivity(position);
                        Intent intent = new Intent(activity, toActivity);

                        if (position == 1) {
                            getAlert(activity).show();
//                            try {
//                                new Logouter().execute(activity).get();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            } catch (ExecutionException e) {
//                                e.printStackTrace();
//                            }
                        } else
                            activity.startActivity(intent);
                        return true;
                    }
                });

        return drawer;
    }

    private AccountHeader getAccountHeader(Activity activity) {
        try {
            JSONObject profile = getProfile();
            String name = (String) profile.get("name");
            String email = (String) profile.get("email");
//            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
//            String email_s = null;
//            email_s = sharedPref.getString("email", "Guest");

            final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(activity);
            String email_s = (mSharedPreference.getString("email", "Default_Value"));

            //Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.drawer3);
            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(activity)
                    .addProfiles(new ProfileDrawerItem().withEmail(email_s))
                    .withTextColorRes(R.color.black)
                    .withProfileImagesVisible(false)
                    //.withHeaderBackground(R.drawable.drawer3)

                    .withSelectionListEnabledForSingleProfile(false)
                    .build();

            return headerResult;
        } catch (JSONException e) {
            throw new RuntimeException("Exception while parsing json", e);
        }
    }

    private IDrawerItem[] getDrawerItems() {
        List<IDrawerItem> items = new ArrayList<>();
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(5).withName("Выйти").withIcon(R.drawable.ic_logout_grey600_48dp)
                .withTextColorRes(R.color.red);
        items.add(logout);
        IDrawerItem[] array = new IDrawerItem[items.size()];

        return items.toArray(array);
    }


    private Class<? extends Activity> getActivity(int i) {
        switch (i) {
            case 1:
                return QrScannerActivity.class;
            default:
                return QrScannerActivity.class;
        }
    }

    private JSONObject getProfile() {
        try {
            return new JSONObject("{name:name, email:email}");
        } catch (JSONException e) {
            throw new RuntimeException("Exception while parsing json", e);
        }
    }

    private final class Logouter extends AsyncTask<Activity, Void, Activity> {
        @Override
        protected Activity doInBackground(Activity... params) {
            Activity context = params[0];
            new LoginHelper().logout(context);
            return params[0];
        }

        @Override
        protected void onPostExecute(Activity context) {
            super.onPostExecute(context);
            Intent intent = new Intent(context, ActivityAuth.class);
            context.startActivity(intent);
        }
    }

    private AlertDialog.Builder getAlert(final Activity context) {
        String title = context.getString(R.string.warning_alert_title);
        String message = context.getString(R.string.logout_alert_message);
        String button1String = context.getString(R.string.logout_alert_btn);
        String button2String = context.getString(R.string.cancel_alert_btn);

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    new Logouter().execute(context).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });

        return ad;
    }
}
