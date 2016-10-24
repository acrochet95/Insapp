package fr.insapp.insapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Comment;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.modeles.User;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.Operation;

public class ParticipantsActivity extends Activity {

    private ImageView news = null;
    private ImageView events = null;
    private ImageView assos = null;
    private ImageView notifs = null;
    private ImageView profil = null;

    private LinearLayout mainLayout = null;
    private ImageView arrow = null;
    private ArrayList<String> users = null;
    private Map<Integer, Drawable> user_icons = null;

    private ImageLoaderCache imageLoader = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("id", HttpGet.credentials.getId());
        outState.putString("userID", HttpGet.credentials.getUserID());
        outState.putString("username", HttpGet.credentials.getUsername());
        outState.putString("sessionToken", HttpGet.credentials.getSessionToken());
        outState.putString("info_user", HttpGet.info_user);
        super.onSaveInstanceState(outState);
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
            HttpGet.info_user = savedInstanceState.getString("info_user");
        }
        setContentView(R.layout.activity_participants);

        mainLayout = (LinearLayout) findViewById(R.id.contentParticipants);
        arrow = (ImageView) findViewById(R.id.arrow);

        user_icons = new HashMap<Integer, Drawable>();
        imageLoader = new ImageLoaderCache(this);

        Intent i = getIntent();
        users = i.getStringArrayListExtra("users");

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParticipantsActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        news = (ImageView)findViewById(R.id.newsB);
        events = (ImageView)findViewById(R.id.eventsB);
        assos = (ImageView)findViewById(R.id.assosB);
        notifs = (ImageView) findViewById(R.id.notifB);
        profil = (ImageView)findViewById(R.id.profilB);

        // Color image and text in red
        int color_menu = getResources().getColor(R.color.theme_red);
        events.setColorFilter(new LightingColorFilter(color_menu, color_menu));

        TextView eventsText = (TextView) findViewById(R.id.eventsText);
        eventsText.setTextColor(color_menu);

        news.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ParticipantsActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 0);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        events.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ParticipantsActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 1);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        assos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ParticipantsActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 2);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        notifs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ParticipantsActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 3);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        profil.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ParticipantsActivity.this, MainActivity.class);
                secondeActivite.putExtra("position", 4);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        SharedPreferences sharedPref_edit = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE);
        int nb_notif = sharedPref_edit.getInt("nb_notifs", 0);

        TextView nb_notifs = (TextView) findViewById(R.id.nb_notifs);

        if (nb_notif > 0) {
            nb_notifs.setText(Integer.toString(nb_notif));
            nb_notifs.setVisibility(View.VISIBLE);
        } else
            nb_notifs.setVisibility(View.INVISIBLE);


        showParticipants();
    }

    public void showParticipants(){
        final ArrayList<User> all_users = new ArrayList<User>();

        for(int i=0; i<users.size(); i++) {

            final int id = i;
            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(output);
                        final User user = new User(json);

                        LayoutInflater layoutInflater = (LayoutInflater) ParticipantsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View childLayout = layoutInflater.inflate(R.layout.content_participants, (ViewGroup) findViewById(R.id.child_id), false);
                        mainLayout.addView(childLayout, id);

                        final ImageView userImage = (ImageView) childLayout.findViewById(R.id.userImage);
                        final TextView username = (TextView) childLayout.findViewById(R.id.username);
                        final TextView name = (TextView) childLayout.findViewById(R.id.name);

                        showUser(user, username, name, userImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.execute(HttpGet.ROOTUSER + "/" + users.get(i) + "?token=" + HttpGet.credentials.getSessionToken());
        }
    }

    public void showUser(final User user, TextView username, TextView name, ImageView userImage) {
        Resources resources = ParticipantsActivity.this.getResources();
        int id = resources.getIdentifier(Operation.drawableProfilName(user), "drawable", ParticipantsActivity.this.getPackageName());

        if (user_icons.containsKey(id)) {
            userImage.setImageDrawable(user_icons.get(id));
        } else {
            Drawable dr = getResources().getDrawable(id);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
            userImage.setImageDrawable(d);
            user_icons.put(id, d);
        }

        username.setText("@" + user.getUsername());
        name.setText(user.getName());

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(ParticipantsActivity.this, ProfilActivity.class);
                activity.putExtra("user", user);
                startActivity(activity);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
    }
}
