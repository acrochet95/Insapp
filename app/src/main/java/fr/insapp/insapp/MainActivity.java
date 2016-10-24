package fr.insapp.insapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.modeles.Notification;
import fr.insapp.insapp.utility.MemoryStorage;

public class MainActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MyPageAdapter myPageAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private List<FragmentRefreshable> fragments = new ArrayList<>();

    private List<ImageView> menu = null;
    private List<TextView> text_menu = null;
    private int lastPosition = 0;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("id", HttpGet.credentials.getId());
        outState.putString("userID", HttpGet.credentials.getUserID());
        outState.putString("username", HttpGet.credentials.getUsername());
        outState.putString("sessionToken", HttpGet.credentials.getSessionToken());
        outState.putString("info_user", HttpGet.info_user);

        outState.putParcelableArrayList("events", MemoryStorage.all_events);
        outState.putParcelableArrayList("assos", MemoryStorage.all_assos);

        //for(int i=0; i<fragments.size(); i++)
        //        getSupportFragmentManager().putFragment(outState, "fragment"+i , fragments.get(i));

        //outState.putInt("viewpagerid", mViewPager.getId());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra("position"))
            mViewPager.setCurrentItem(intent.getIntExtra("position", 0));

        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            String id = savedInstanceState.getString("id");
            String userID = savedInstanceState.getString("userID");
            String username = savedInstanceState.getString("username");
            String sessionToken = savedInstanceState.getString("sessionToken");
            HttpGet.credentials = new Credentials(id, userID, username, sessionToken);

            String info_user = savedInstanceState.getString("info_user");
            if(!info_user.isEmpty())
                HttpGet.info_user = info_user;

            MemoryStorage.all_events = savedInstanceState.getParcelableArrayList("events");
            MemoryStorage.all_assos = savedInstanceState.getParcelableArrayList("assos");
/*
            fragments.clear();
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, NewsFragment.class.getName()));
            fragments.add((FragmentRefreshable)Fragment.instantiate(this, EventsFragment.class.getName()));
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, AssociationFragment.class.getName()));
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, NotificationsFragment.class.getName()));
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, ProfilFragment.class.getName()));
*/
            //for(int i=0; i<fragments.size(); i++)
            //    fragments.set(0, (FragmentRefreshable)getSupportFragmentManager().getFragment(savedInstanceState, "fragment"+i));
        }

        setContentView(R.layout.activity_main);

        menu = new ArrayList<>();
        menu.add((ImageView) findViewById(R.id.newsB));
        menu.add((ImageView) findViewById(R.id.eventsB));
        menu.add((ImageView) findViewById(R.id.assosB));
        menu.add((ImageView) findViewById(R.id.notifB));
        menu.add((ImageView) findViewById(R.id.profilB));

        text_menu = new ArrayList<>();
        text_menu.add((TextView) findViewById(R.id.newsText));
        text_menu.add((TextView) findViewById(R.id.eventsText));
        text_menu.add((TextView) findViewById(R.id.assosText));
        text_menu.add((TextView) findViewById(R.id.notifText));
        text_menu.add((TextView) findViewById(R.id.profilText));

        if(fragments.size() == 0) {
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, NewsFragment.class.getName()));
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, EventsFragment.class.getName()));
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, AssociationFragment.class.getName()));
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, NotificationsFragment.class.getName()));
            fragments.add((FragmentRefreshable) Fragment.instantiate(this, ProfilFragment.class.getName()));
        }

        // Color image and text in red
        int color_menu = getResources().getColor(R.color.theme_red);
        menu.get(0).setColorFilter(new LightingColorFilter(color_menu, color_menu));
        text_menu.get(0).setTextColor(color_menu);

        updateNotifications();

        myPageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(myPageAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {

                // Color image and text in red
                int color_menu = getResources().getColor(R.color.theme_red);
                menu.get(position).setColorFilter(new LightingColorFilter(color_menu, color_menu));
                text_menu.get(position).setTextColor(color_menu);

                // Remove color of the last position
                int color_menu_grey = getResources().getColor(R.color.grey);
                menu.get(lastPosition).setColorFilter(null);
                text_menu.get(lastPosition).setTextColor(color_menu_grey);

                // Refresh lastposition
                lastPosition = position;

                updateNotifications();

            }
        });

        for (int i=0; i<menu.size(); i++){

            final int id = i;
            menu.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(id);
                }
            });
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/
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

    public void updateNotifications(){

        SharedPreferences sharedPref_edit = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE);
        int nb_notif = sharedPref_edit.getInt("nb_notifs", 0);

        TextView nb_notifs = (TextView) findViewById(R.id.nb_notifs);

        if (nb_notif > 0) {
            nb_notifs.setText(Integer.toString(nb_notif));
            nb_notifs.setVisibility(View.VISIBLE);
        } else
            nb_notifs.setVisibility(View.INVISIBLE);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MyPageAdapter extends FragmentPagerAdapter {

        private final List<FragmentRefreshable> fragments;

        public MyPageAdapter(FragmentManager fm, List<FragmentRefreshable> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
