package fr.insapp.insapp;

import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.modeles.Association;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.modeles.Notification;
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.utility.File;
import fr.insapp.insapp.utility.MemoryStorage;

public class EventProfil extends Activity {

    private ImageView news = null;
    private ImageView events = null;
    private ImageView assos = null;
    private ImageView notifs = null;
    private ImageView profil = null;
    private ImageView img = null;
    private Button yes = null;
    private Button no = null;

    private String asso = null;
    private Event mEvent = null;
    private boolean userParticipates;

    private Notification notif = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("id", HttpGet.credentials.getId());
        outState.putString("userID", HttpGet.credentials.getUserID());
        outState.putString("username", HttpGet.credentials.getUsername());
        outState.putString("sessionToken", HttpGet.credentials.getSessionToken());
        outState.putString("info_user", HttpGet.info_user);

        outState.putParcelable("event", mEvent);
        outState.putParcelableArrayList("events", MemoryStorage.all_events);
        outState.putParcelableArrayList("assos", MemoryStorage.all_assos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String id = savedInstanceState.getString("id");
            String userID = savedInstanceState.getString("userID");
            String username = savedInstanceState.getString("username");
            String sessionToken = savedInstanceState.getString("sessionToken");
            HttpGet.credentials = new Credentials(id, userID, username, sessionToken);

            String info_user = savedInstanceState.getString("info_user");
            if(!info_user.isEmpty())
                HttpGet.info_user = info_user;

            mEvent = savedInstanceState.getParcelable("event");

            MemoryStorage.all_events = savedInstanceState.getParcelableArrayList("events");
            MemoryStorage.all_assos = savedInstanceState.getParcelableArrayList("assos");
        }
        setContentView(R.layout.activity_event_profil);

/*
        if(!TimeZone.getDefault().equals(TimeZone.getTimeZone("UTC"))) {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        }
*/
        // Create global configuration and initialize ImageLoaderCache with this config
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .build();
            ImageLoader.getInstance().init(config);
        }

        news = (ImageView) findViewById(R.id.newsB);
        events = (ImageView) findViewById(R.id.eventsB);
        assos = (ImageView) findViewById(R.id.assosB);
        notifs = (ImageView) findViewById(R.id.notifB);
        profil = (ImageView) findViewById(R.id.profilB);
        events.setColorFilter(R.color.red);

        // Color image and text in red
        int color_menu = getResources().getColor(R.color.theme_red);
        events.setColorFilter(new LightingColorFilter(color_menu, color_menu));

        TextView eventsText = (TextView) findViewById(R.id.eventsText);
        eventsText.setTextColor(color_menu);

        news.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(EventProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 0);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        events.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(EventProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 1);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        assos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(EventProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 2);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        notifs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(EventProfil.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 3);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        profil.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(EventProfil.this, MainActivity.class);
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

        Intent intent = getIntent();
        if (mEvent == null) {
            mEvent = intent.getParcelableExtra("event");
        }

        if(intent.hasExtra("notification")){
            notif = intent.getParcelableExtra("notification");

            asso = notif.getSender();

            if(HttpGet.credentials != null) {

                HttpGet event = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        try {
                            mEvent = new Event(new JSONObject(output));
                            MemoryStorage.addEvent(mEvent);
                            showEvent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                event.execute(HttpGet.ROOTEVENT + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());

            }
            else{

                HttpGet.info_user = File.readSettings(this);

                if(!HttpGet.info_user.equals("")) {

                    String[] params = HttpGet.info_user.split(" ");

                    JSONObject json = new JSONObject();
                    try {
                        json.put("Username", params[0]);
                        json.put("AuthToken", params[1]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final HttpPost login = new HttpPost(new AsyncResponse() {

                        public void processFinish(String output) {
                            JSONObject json = null;
                            if (output != null) {
                                try {
                                    json = new JSONObject(output);
                                    if (!json.has("error")) {
                                        HttpGet.credentials = new Credentials(json);

                                        HttpGet event = new HttpGet(new AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) {
                                                try {
                                                    mEvent = new Event(new JSONObject(output));
                                                    MemoryStorage.addEvent(mEvent);
                                                    showEvent();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        event.execute(HttpGet.ROOTEVENT + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    login.execute(HttpGet.ROOTLOGIN, json.toString());

                }

            }

            if(!notif.isSeen()) {

                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE);
                int nb = sharedPref.getInt("nb_notifs", 0) - 1;
                SharedPreferences.Editor sharedPref_edit2 = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                sharedPref_edit2.putInt("nb_notifs", nb);
                sharedPref_edit2.commit();
            }

        }
        else {
            asso = intent.getStringExtra("asso");
            if (mEvent == null) {
                mEvent = intent.getParcelableExtra("event");
            }
            showEvent();
        }
    }

    public void showEvent(){

        String name = mEvent.getName();
        String description = mEvent.getDescription();
        String coverURL = mEvent.getPhotoURL();

        final int bgColor = Color.parseColor("#" + mEvent.getBgColor());
        final int fgColor = Color.parseColor("#" + mEvent.getFgColor());

        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        if (fgColor == Color.WHITE)
            arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_white));

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTaskRoot()) {
                    Intent secondeActivite = new Intent(EventProfil.this, MainActivity.class);
                    secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    secondeActivite.putExtra("position", 1);
                    startActivity(secondeActivite);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    finish();
                }
                else{
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        final int HEIGHT_IMG = (int) (((float) getWindowManager().getDefaultDisplay().getHeight()) / (float) 3.0);

        DisplayMetrics displayMetrics = EventProfil.this.getResources().getDisplayMetrics();
        int dp = Math.round(HEIGHT_IMG / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        img = (ImageView) findViewById(R.id.imageView);
        img.setMinimumHeight(dp);

        ImageLoader imageLoader = ImageLoader.getInstance();
        // On charge l'image sans interrompre le processus
        imageLoader.loadImage(HttpGet.IMAGEURL + coverURL, new SimpleImageLoadingListener() {

            public void onLoadingComplete(String imageUri, View view, Bitmap result) {
                float height = (result.getHeight() * (((float) getWindowManager().getDefaultDisplay().getWidth()) / ((float) result.getWidth())));
                if (height > HEIGHT_IMG) // on limite la hauteur de l'image (avec le futur scale)
                    height = HEIGHT_IMG;

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

        userParticipates = false;
        for (String id : mEvent.getParticipants()) {
            if (HttpGet.credentials.getUserID().equals(id)) {
                userParticipates = true;
                break;
            }
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.content);
        layout.setBackgroundColor(bgColor); // on met la couleur dominante en background

        final TextView title = (TextView) findViewById(R.id.title);
        final TextView assoLink = (TextView) findViewById(R.id.asso);
        final TextView date = (TextView) findViewById(R.id.date);
        final TextView participants = (TextView) findViewById(R.id.participants);
        final TextView text = (TextView) findViewById(R.id.text);

        yes = (Button) findViewById(R.id.oui);
        no = (Button) findViewById(R.id.non);

        if (userParticipates) {

            yes.setBackgroundResource(R.drawable.button_rounded_white_left);
            yes.setTextColor(bgColor);

            no.setBackgroundResource(R.drawable.button_rounded_transparent_right);
            no.setTextColor(fgColor);

            GradientDrawable drawable_yes = (GradientDrawable) yes.getBackground();
            drawable_yes.setStroke(2, fgColor);
            drawable_yes.setColor(fgColor);

            GradientDrawable drawable_no = (GradientDrawable) no.getBackground();
            drawable_no.setStroke(2, fgColor);
            drawable_no.setColor(bgColor);
        } else {
            yes.setBackgroundResource(R.drawable.button_rounded_transparent_left);
            yes.setTextColor(fgColor);

            no.setBackgroundResource(R.drawable.button_rounded_white_right);
            no.setTextColor(bgColor);

            GradientDrawable drawable_yes = (GradientDrawable) yes.getBackground();
            drawable_yes.setStroke(2, fgColor);
            drawable_yes.setColor(bgColor);

            GradientDrawable drawable_no = (GradientDrawable) no.getBackground();
            drawable_no.setStroke(2, fgColor);
            drawable_no.setColor(fgColor);
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userParticipates) {
                    HttpPost request = new HttpPost(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            yes.setBackgroundResource(R.drawable.button_rounded_white_left);
                            no.setBackgroundResource(R.drawable.button_rounded_transparent_right);

                            GradientDrawable drawable_yes = (GradientDrawable) yes.getBackground();
                            GradientDrawable drawable_no = (GradientDrawable) no.getBackground();
                            drawable_yes.setStroke(2, fgColor);
                            drawable_yes.setColor(fgColor);
                            yes.setTextColor(bgColor);

                            drawable_no.setColor(bgColor);
                            drawable_no.setStroke(2, fgColor);
                            no.setTextColor(fgColor);

                            userParticipates = true;
                            refreshEvent(output, participants);


                            SharedPreferences prefs = getSharedPreferences(
                                    Signin.class.getSimpleName(), Signin.MODE_PRIVATE);

                            // if first time user join an event
                            if(prefs.getString("addEventToCalender", "").equals("")){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventProfil.this);

                                // set title
                                alertDialogBuilder.setTitle("Ajout au calendrier");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Voulez-vous ajouter les évènements auquels vous participer dans votre calendrier ?")
                                        .setCancelable(false)
                                        .setPositiveButton("OUI",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogAlert,int id) {

                                                SharedPreferences.Editor prefs = getSharedPreferences(
                                                        Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                                                prefs.putString("addEventToCalender", "true");
                                                prefs.commit();

                                                addEventToCalendar();
                                            }
                                        })
                                        .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogAlert, int id) {
                                                SharedPreferences.Editor prefs = getSharedPreferences(
                                                        Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                                                prefs.putString("addEventToCalender", "false");
                                                prefs.commit();
                                                dialogAlert.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();
                            }
                            else if(prefs.getString("addEventToCalender", "true").equals("true")){
                                addEventToCalendar();
                            }
                        }
                    });
                    request.execute(HttpGet.ROOTEVENT + "/" + mEvent.getId() + "/participant/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userParticipates) {
                    HttpDelete delete = new HttpDelete(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            yes.setBackgroundResource(R.drawable.button_rounded_transparent_left);
                            no.setBackgroundResource(R.drawable.button_rounded_white_right);

                            GradientDrawable drawable_yes = (GradientDrawable) yes.getBackground();
                            GradientDrawable drawable_no = (GradientDrawable) no.getBackground();

                            drawable_yes.setStroke(2, fgColor);
                            drawable_yes.setColor(bgColor);
                            yes.setTextColor(fgColor);

                            drawable_no.setColor(fgColor);
                            drawable_no.setStroke(2, fgColor);
                            no.setTextColor(bgColor);

                            userParticipates = false;
                            refreshEvent(output, participants);
                        }
                    });
                    delete.execute(HttpGet.ROOTEVENT + "/" + mEvent.getId() + "/participant/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                }
            }
        });

        title.setText(name);
        title.setTextColor(fgColor);

        int id = MemoryStorage.findAsso(asso);
        if (id != -1) {
            assoLink.setText("@" + MemoryStorage.all_assos.get(id).getName());
        } else {
            HttpGet assos = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    if (!output.isEmpty()) {
                        try {

                            Association a = new Association(new JSONObject(output));
                            MemoryStorage.addAsso(a);

                            assoLink.setText("@" + a.getName());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent i = new Intent(getBaseContext(), Login.class);
                        startActivity(i);
                    }
                }
            });
            assos.execute(HttpGet.ROOTASSOCIATION + "/" + asso + "?token=" + HttpGet.credentials.getSessionToken());
        }
        assoLink.setTextColor(fgColor);

        date.setTextColor(fgColor);

        SimpleDateFormat format = new SimpleDateFormat("EEEE dd/MM");
        SimpleDateFormat format_hours_minutes = new SimpleDateFormat("HH:mm");


        if (mEvent.getDateStart().getDay() == mEvent.getDateEnd().getDay() && mEvent.getDateStart().getMonth() == mEvent.getDateEnd().getMonth()) {
            String day = format.format(mEvent.getDateStart());

            date.setText(day.replaceFirst(".", (day.charAt(0) + "").toUpperCase()) + " de " + format_hours_minutes.format(mEvent.getDateStart()) + " à " + format_hours_minutes.format(mEvent.getDateEnd()));
        } else {
            String start = format.format(mEvent.getDateStart()) + " à " + format_hours_minutes.format(mEvent.getDateStart());
            String end = format.format(mEvent.getDateEnd()) + " à " + format_hours_minutes.format(mEvent.getDateEnd());
            date.setText("Du " + start.replaceFirst(".", (start.charAt(0) + "").toUpperCase()) + "\n"
                    + "Au " + end.replaceFirst(".", (end.charAt(0) + "").toUpperCase()));
        }

        int nb_participants = mEvent.getParticipants().size();
        if (nb_participants <= 1)
            participants.setText(nb_participants + " participant");
        else
            participants.setText(nb_participants + " participants");

        if (nb_participants > 0)
            participants.setPaintFlags(participants.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEvent.getParticipants().size() > 0) {
                    Intent i = new Intent(EventProfil.this, ParticipantsActivity.class);
                    i.putStringArrayListExtra("users", mEvent.getParticipants());
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            }
        });
        participants.setTextColor(fgColor);

        text.setText(description);
        text.setTextColor(fgColor);
    }

    public void refreshEvent(String output, final TextView participants) {

        try {
            JSONObject json = new JSONObject(output);

            if(mEvent != null) {
                if (MemoryStorage.findEvent(mEvent.getId()) == -1)
                    MemoryStorage.addEvent(mEvent);

                mEvent = new Event(json.getJSONObject("event"));
                // MemoryStorage.all_events.set(MemoryStorage.findEvent(mEvent.getId()), mEvent);
                int nb_participants = mEvent.getParticipants().size();

                if (nb_participants <= 1)
                    participants.setText(nb_participants + " participant");
                else
                    participants.setText(nb_participants + " participants");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addEventToCalendar() {
        /*Cursor cursor = null;
        int[] calIds = null;

        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,};

        ContentResolver cr = EventProfil.this.getContentResolver();
        cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"), projection, null, null, null);

        if (cursor.moveToFirst()) {
            final String[] calNames = new String[cursor.getCount()];
            calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
        }

        TimeZone tZone = TimeZone.getTimeZone("UTC");

        try {

            ContentValues values = new ContentValues();

            values.put(CalendarContract.Events.DTSTART, mEvent.getDateStart().getTime());
            values.put(CalendarContract.Events.DTEND, mEvent.getDateEnd().getTime());
            values.put(CalendarContract.Events.TITLE, mEvent.getName());
            values.put(CalendarContract.Events.DESCRIPTION, mEvent.getDescription());
            values.put(CalendarContract.Events.CALENDAR_ID, calIds[0]);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, tZone.toString());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    Toast.makeText(EventProfil.this, "L'accès au calendrier est désactivé", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Uri mInsert = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            Toast.makeText(EventProfil.this, "Évènement ajouté au calendrier", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(EventProfil.this, "Erreur d'ajout au calendrier", Toast.LENGTH_SHORT).show();
        }*/
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        //intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,cal.getTimeInMillis());
        //intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,cal.getTimeInMillis()+60*60*1000);

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, mEvent.getDateStart().getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, mEvent.getDateEnd().getTime());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.TITLE, mEvent.getName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, mEvent.getDescription());
        //intent.putExtra(CalendarContract.EVENT_LOCATION, "Event Address");
        //intent.putExtra(CalendarContract.RRULE, "FREQ=YEARLY");
        startActivity(intent);
    }
}
