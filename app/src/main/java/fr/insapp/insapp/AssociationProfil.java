package fr.insapp.insapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Association;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;

public class AssociationProfil extends Activity {

    private ImageView news = null;
    private ImageView events = null;
    private ImageView assos = null;
    private ImageView notifs = null;
    private ImageView profil = null;
    private ImageView img = null;
    private ImageLoaderCache imageLoader = null;
    private TextView event_title = null;

    private SwipeRefreshLayout swipeView = null;

    private int bgColor = 0;
    private int fgColor = 0;

    private Association mAsso = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("id", HttpGet.credentials.getId());
        outState.putString("userID", HttpGet.credentials.getUserID());
        outState.putString("username", HttpGet.credentials.getUsername());
        outState.putString("sessionToken", HttpGet.credentials.getSessionToken());
        outState.putString("info_user", HttpGet.info_user);

        outState.putParcelableArrayList("events", MemoryStorage.all_events);
        outState.putParcelableArrayList("assos", MemoryStorage.all_assos);
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

            String info_user = savedInstanceState.getString("info_user");
            if(!info_user.isEmpty())
                HttpGet.info_user = info_user;

            MemoryStorage.all_events = savedInstanceState.getParcelableArrayList("events");
            MemoryStorage.all_assos = savedInstanceState.getParcelableArrayList("assos");
        }
        setContentView(R.layout.activity_association_profil);

        // Create global configuration and initialize ImageLoaderCache with this config
        if(!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .build();
            ImageLoader.getInstance().init(config);
        }

        imageLoader = new ImageLoaderCache(this);

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeEvents);

        Intent intent = getIntent(); // on récupère les données de l'asso
        mAsso = intent.getParcelableExtra("asso");
        String name = mAsso.getName();
        String description = mAsso.getDescription();
        String coverURL = mAsso.getCover();

        bgColor = Color.parseColor("#" + mAsso.getBgColor());
        fgColor = Color.parseColor("#" + mAsso.getFgColor());

        news = (ImageView)findViewById(R.id.newsB);
        events = (ImageView)findViewById(R.id.eventsB);
        assos = (ImageView)findViewById(R.id.assosB);
        notifs = (ImageView)findViewById(R.id.notifB);
        profil = (ImageView)findViewById(R.id.profilB);

        // Color image and text in red
        int color_menu = getResources().getColor(R.color.theme_red);
        assos.setColorFilter(new LightingColorFilter(color_menu, color_menu));

        TextView assosText = (TextView) findViewById(R.id.assosText);
        assosText.setTextColor(color_menu);

        news.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(AssociationProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 0);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        events.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(AssociationProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 1);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        assos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(AssociationProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 2);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        notifs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(AssociationProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 3);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        profil.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(AssociationProfil.this, MainActivity.class);
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

        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssociationProfil.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        ImageView mail = (ImageView) findViewById(R.id.letter);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        refreshPage(true);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                refreshPage(false);
            }
        });

        final int HEIGHT_IMG = (int)(((float) getWindowManager().getDefaultDisplay().getHeight())/(float)3.0);

        DisplayMetrics displayMetrics = AssociationProfil.this.getResources().getDisplayMetrics();
        int dp = Math.round(HEIGHT_IMG / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        img = (ImageView)findViewById(R.id.imageView);
        img.setMinimumHeight(dp);

        ImageLoader imageLoader = ImageLoader.getInstance();
        // On charge l'image sans interrompre le processus
        imageLoader.loadImage(HttpGet.IMAGEURL + coverURL, new SimpleImageLoadingListener() {

            public void onLoadingComplete(String imageUri, View view, Bitmap result) {
                float height = (result.getHeight() * (((float) getWindowManager().getDefaultDisplay().getWidth()) / ((float) result.getWidth())));
                if (height > HEIGHT_IMG) // on limite la hauteur de l'image (avec le futur scale)
                    height = HEIGHT_IMG;


                DisplayMetrics displayMetrics = AssociationProfil.this.getResources().getDisplayMetrics();
                int dp = Math.round(height / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                img.setMinimumHeight(dp);

                height /= (((float) getWindowManager().getDefaultDisplay().getWidth()) / ((float) result.getWidth())); // on retourne à la hauteur sans le scale

                Matrix m = new Matrix(); // on scale l'image de sorte qu'elle remplisse en largeur et on garde les proportions en hauteur
                m.postScale(((float) getWindowManager().getDefaultDisplay().getWidth()) / ((float) result.getWidth()), ((float) getWindowManager().getDefaultDisplay().getWidth()) / ((float) result.getWidth()));

                // recreate the new Bitmap
                Bitmap resizedBitmap = Bitmap.createBitmap(result, 0, 0, result.getWidth(), (int) height, m, true);

                BitmapDrawable ob = new BitmapDrawable(getResources(), resizedBitmap);
                img.setBackgroundDrawable(ob);

                // On definie le degrade
                Shader shader = new LinearGradient(0, 0, 0, resizedBitmap.getHeight(), (bgColor & 0xFFFFFF), bgColor, Shader.TileMode.CLAMP);

                Paint p = new Paint();
                p.setShader(shader);

                Bitmap b = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                canvas.drawRect(new RectF(0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight()), p);

                img.setImageBitmap(b);
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.content);
        layout.setBackgroundColor(bgColor); // on met la couleur dominante en background

        TextView nameView = (TextView)findViewById(R.id.name);
        TextView descriptionView = (TextView)findViewById(R.id.description);
        event_title = (TextView) findViewById(R.id.title_events);

        if(fgColor == Color.WHITE) {
            arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_white));
            mail.setImageDrawable(getResources().getDrawable(R.drawable.letter_white));
        }

        nameView.setText("@" + name);
        nameView.setTextColor(fgColor);
        descriptionView.setText(description);
        descriptionView.setTextColor(fgColor);
        if(mAsso.getEvents().size() == 0) {
            event_title.setVisibility(View.GONE);
        }
        else
            event_title.setTextColor(fgColor);
    }

    public void refreshPage(final boolean firstTime){

        final LinearLayout content = (LinearLayout) findViewById(R.id.info);

        final List<Event> events = new ArrayList<Event>();

        final HttpGet asso = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {

                try {
                    JSONObject json = new JSONObject(output);
                    //final Association newAsso =
                    mAsso = new Association(json);

                    if(mAsso.getEvents().size() == 0) {
                        event_title.setVisibility(View.GONE);
                    }
                    else
                        event_title.setTextColor(fgColor);

                    swipeView.setRefreshing(false);

                    for (String id : mAsso.getEvents()) {

                        int id_event = MemoryStorage.findEvent(id);
                        if (id_event == -1) {
                            HttpGet request = new HttpGet(new AsyncResponse() {
                                @Override
                                public void processFinish(String output) {

                                    try {
                                        Event eTmp = new Event(new JSONObject(output));
                                        events.add(MemoryStorage.addEvent(eTmp));

                                    } catch (JSONException e) {

                                    }

                                    showEventInformation(mAsso.getEvents().size(), events, content);
                                }
                            });
                            request.execute(HttpGet.ROOTEVENT + "/" + id + "?token=" + HttpGet.credentials.getSessionToken());
                        }
                        else if(firstTime) {
                            events.add(MemoryStorage.getAll_events().get(id_event));
                            showEventInformation(mAsso.getEvents().size(), events, content);
                        }

                    }
                } catch(JSONException json){
                    Intent activity = new Intent(AssociationProfil.this, MainActivity.class);
                    startActivity(activity);
                }
            }
        });
        asso.execute(HttpGet.ROOTASSOCIATION + "/" + mAsso.getId() + "?token=" + HttpGet.credentials.getSessionToken());

    }


    public void showEventInformation(int size, List<Event> events, LinearLayout content){

        if(size == events.size()) {

            if (content.getChildCount() > 3) {
                content.removeViews(2, content.getChildCount() - 2 -1);
            }

            Date atm = Calendar.getInstance().getTime();

            int nb = 0;
            Collections.sort(events);
            for (final Event event : events) {

                if (event.getDateEnd().getTime() > atm.getTime()) {
                    LayoutInflater layoutInflater = (LayoutInflater) AssociationProfil.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View childLayout = layoutInflater.inflate(R.layout.content_event_associationprofil, (ViewGroup) findViewById(R.id.child_id), false);
                    content.addView(childLayout, 2 + nb);

                    childLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent activity = new Intent(AssociationProfil.this, EventProfil.class);
                            activity.putExtra("event", event);
                            activity.putExtra("asso", mAsso.getId());
                            startActivity(activity);
                        }
                    });

                    ImageView imageEvent = (ImageView) childLayout.findViewById(R.id.imageEvent);
                    TextView title = (TextView) childLayout.findViewById(R.id.title);
                    TextView date = (TextView) childLayout.findViewById(R.id.date);

                    imageLoader.DisplayImage(HttpGet.IMAGEURL + event.getPhotoURL(), imageEvent);

                    title.setText(event.getName());
                    title.setTextColor(fgColor);

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

                    date.setTextColor(fgColor);
                    nb++;
                }
            }
        }
    }

    public void sendEmail(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ mAsso.getEmail() });
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
