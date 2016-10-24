package fr.insapp.insapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPut;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.modeles.User;
import fr.insapp.insapp.utility.File;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Operation;

public class ProfilActivity extends Activity {

    private ImageView news = null;
    private ImageView events = null;
    private ImageView assos = null;
    private ImageView notifs = null;
    private ImageView profil = null;
    private ImageView arrow = null;

    private TextView username = null;
    private TextView name = null;
    private TextView email = null;
    private TextView promo = null;
    private TextView description = null;

    private ImageView img = null;

    private ImageLoaderCache imageLoader = null;

    private User user = null;


    @Override
    public void onResume() {

        refresh(false);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        imageLoader = new ImageLoaderCache(this);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        username = (TextView) findViewById(R.id.username);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        promo = (TextView) findViewById(R.id.promo);
        description = (TextView) findViewById(R.id.description);

        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        ImageView option = (ImageView) findViewById(R.id.option);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfilActivity.this);

                // set title
                alertDialogBuilder.setTitle("Signaler utilisateur");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Voulez-vous signaler cet utilisateur ?")
                        .setCancelable(false)
                        .setPositiveButton("OUI",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogAlert,int id) {
                                HttpPut signaler = new HttpPut(new AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {

                                        Toast.makeText(ProfilActivity.this, "Utilisateur correctement signalé", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                signaler.execute(HttpGet.ROOTURL + "/report/user/" + user.getId() + "?token=" + HttpGet.credentials.getSessionToken());
                            }
                        })
                        .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogAlert, int id) {
                                dialogAlert.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });

        news = (ImageView)findViewById(R.id.newsB);
        events = (ImageView)findViewById(R.id.eventsB);
        assos = (ImageView)findViewById(R.id.assosB);
        notifs = (ImageView)findViewById(R.id.notifB);
        profil = (ImageView)findViewById(R.id.profilB);

        // Color image and text in red
        int color_menu = getResources().getColor(R.color.theme_red);
        profil.setColorFilter(new LightingColorFilter(color_menu, color_menu));

        TextView profilText = (TextView) findViewById(R.id.profilText);
        profilText.setTextColor(color_menu);

        news.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ProfilActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 0);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        events.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ProfilActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 1);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        assos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ProfilActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 2);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        notifs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ProfilActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 3);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        profil.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(ProfilActivity.this, MainActivity.class);
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

    }

    public void refresh(boolean refreshFromSwipe){

        //TextView text_event = (TextView) findViewById(R.id.title_events);
        //text_event.setVisibility(View.GONE);

        img = (ImageView) findViewById(R.id.imageProfil);

        writeUserInformation(user);
    }

    public void writeUserInformation(final User user){
        final LinearLayout content = (LinearLayout) findViewById(R.id.content);

        showImageProfil(user, img);

        username.setText("@" + user.getUsername());
        name.setText(user.getName());
        email.setText(user.getEmail());
        promo.setText(user.getPromotion());
        description.setText(user.getDescription());

        if (user.getName().isEmpty())
            name.setVisibility(View.GONE);

        if (user.getEmail().isEmpty())
            email.setVisibility(View.GONE);

        if (user.getPromotion().isEmpty())
            promo.setVisibility(View.GONE);

        if (user.getDescription().isEmpty())
            description.setVisibility(View.GONE);

        TextView text_event = (TextView) findViewById(R.id.title_events);
        text_event.setVisibility(View.GONE);

        final List<Event> events = new ArrayList<Event>();

        for (String idEvent : user.getEvents()) {

            int id_event = MemoryStorage.findEvent(idEvent);
            if (id_event == -1) {
                HttpGet request = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                        try {
                            Event event = new Event(new JSONObject(output));
                            MemoryStorage.addEvent(event);
                            events.add(event);

                        } catch (JSONException e) {

                        }

                        showEventInformation(user.getEvents().size(), events, content);
                    }
                });
                request.execute(HttpGet.ROOTEVENT + "/" + idEvent + "?token=" + HttpGet.credentials.getSessionToken());
            } else {
                events.add(MemoryStorage.getAll_events().get(id_event));
                showEventInformation(user.getEvents().size(), events, content);
            }
        }

    }

    public void showEventInformation(int size, List<Event> events, LinearLayout content){

        if(content.getChildCount() > 6) {
            content.removeViews(5, content.getChildCount() - 6);
        }

        // On affiche que quand tous les events sont loaded
        if(size == events.size()) {

            TextView text_event = (TextView) findViewById(R.id.title_events);
            text_event.setVisibility(View.VISIBLE);

            Date atm = Calendar.getInstance().getTime();

            int nb = 0;
            Collections.sort(events);
            for (final Event event : events) {

                if (event.getDateEnd().getTime() > atm.getTime()) {
                    LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View childLayout = layoutInflater.inflate(R.layout.content_event_associationprofil, (ViewGroup) findViewById(R.id.child_id), false);
                    content.addView(childLayout, 5 + nb);

                    childLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent activity = new Intent(ProfilActivity.this, EventProfil.class);
                            activity.putExtra("event", event);
                            activity.putExtra("asso", event.getAssociation());
                            startActivity(activity);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                    });

                    ImageView imageEvent = (ImageView) childLayout.findViewById(R.id.imageEvent);
                    TextView title = (TextView) childLayout.findViewById(R.id.title);
                    TextView date = (TextView) childLayout.findViewById(R.id.date);

                    imageLoader.DisplayImage(HttpGet.IMAGEURL + event.getPhotoURL(), imageEvent);

                    title.setText(event.getName());
                    title.setTextColor(Color.BLACK);

                    // DATE
                    // Format 1 day
                    if (event.getDateStart().getDay() == event.getDateEnd().getDay() && event.getDateStart().getMonth() == event.getDateEnd().getMonth()) {
                        DateFormat dateFormat_oneday = new SimpleDateFormat("'Le' dd/MM 'à' HH:mm");
                        date.setText(dateFormat_oneday.format(event.getDateStart()));
                    } else {
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM");
                        String dateStart = dateFormat.format(event.getDateStart());
                        String dateEnd = dateFormat.format(event.getDateEnd());
                        date.setText("Du " + dateStart + " au " + dateEnd);
                    }

                    date.setTextColor(Color.BLACK);
                    nb++;
                }
            }
        }
    }

    public void showImageProfil(User user, ImageView imageView){
        Resources resources = this.getResources();
        imageView.setImageDrawable(resources.getDrawable(resources.getIdentifier(Operation.drawableProfilName(user), "drawable", this.getPackageName())));
    }
}
