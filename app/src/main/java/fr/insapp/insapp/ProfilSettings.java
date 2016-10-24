package fr.insapp.insapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPut;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.modeles.User;
import fr.insapp.insapp.utility.File;

public class ProfilSettings extends Activity {

    private User user = null;
    private int position_promo = 0;
    private int position_genre = 0;

    private EditText nom = null;
    private EditText email = null;
    private EditText description = null;
    private Spinner promo = null;
    private Spinner genre = null;

    private final int NB_WORDS_MAX = 120;

    private String[] genre_drawable_name = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(HttpGet.credentials != null) {
            outState.putString("id", HttpGet.credentials.getId());
            outState.putString("userID", HttpGet.credentials.getUserID());
            outState.putString("username", HttpGet.credentials.getUsername());
            outState.putString("sessionToken", HttpGet.credentials.getSessionToken());
            outState.putString("info_user", HttpGet.info_user);
            super.onSaveInstanceState(outState);
        }
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
        }
        setContentView(R.layout.activity_profil_settings);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        ImageView cross = (ImageView) findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilSettings.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        ImageView submit = (ImageView) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfil(user);
            }
        });

        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        final ImageView avatar = (ImageView) findViewById(R.id.imageProfil);
        TextView username = (TextView) findViewById(R.id.username);
        nom = (EditText) findViewById(R.id.nom);
        email = (EditText) findViewById(R.id.email);
        final TextView nb_caractere = (TextView) findViewById(R.id.nb_caractere);
        description = (EditText) findViewById(R.id.description);

        username.setText("@" + user.getUsername());
        nom.setText(user.getName());
        email.setText(user.getEmail());
        description.setText(user.getDescription());
        nb_caractere.setText(Integer.toString(NB_WORDS_MAX - user.getDescription().length()));

        promo = (Spinner) findViewById(R.id.promo);
        genre = (Spinner) findViewById(R.id.genre);

        // Initializing a String Array (PROMO)
        final String[] all_promos = new String[]{
                "Inconnu",
                "1STPI",
                "2STPI",
                "3EII",
                "4EII",
                "5EII",
                "3GCU",
                "4GCU",
                "5GCU",
                "3GM",
                "4GM",
                "5GM",
                "3GMA",
                "4GMA",
                "5GMA",
                "3INFO",
                "4INFO",
                "5INFO",
                "3SGM",
                "4SGM",
                "5SGM",
                "3SRC",
                "4SRC",
                "5SRC",
                "Personnel/Enseignant"
        };

        final String[] promo_drawable_name = new String[]{
                "",
                "1stpi",
                "2stpi",
                "eii",
                "eii",
                "eii",
                "gcu",
                "gcu",
                "gcu",
                "gm",
                "gm",
                "gm",
                "gma",
                "gma",
                "gma",
                "info",
                "info",
                "info",
                "sgm",
                "sgm",
                "sgm",
                "src",
                "src",
                "src",
                "worker"
        };

        // Initializing a String Array (GENRE)
        String[] all_genre = new String[]{
                "Inconnu",
                "Féminin",
                "Masculin"
        };

        genre_drawable_name = new String[]{
                "",
                "female",
                "male"
        };

        // Initializing an ArrayAdapter (PROMO)
        ArrayAdapter<String> spinnerArrayAdapterPromo = new ArrayAdapter<String>(this,R.layout.spinner_item, all_promos);
        spinnerArrayAdapterPromo.setDropDownViewResource(R.layout.spinner_item);
        promo.setAdapter(spinnerArrayAdapterPromo);

        promo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Resources resources = ProfilSettings.this.getResources();
                position_promo = position;

                String drawable = "avatar_" + promo_drawable_name[position_promo] + "_" + genre_drawable_name[position_genre];
                if(position_promo == 0 || position_genre == 0)
                    drawable = "avatar_default";
                avatar.setImageDrawable(resources.getDrawable(resources.getIdentifier(drawable, "drawable", ProfilSettings.this.getPackageName())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        if(!user.getPromotion().isEmpty() && !user.getPromotion().equals(all_promos[0])) {
            for(int i=0; i<all_promos.length;i++) {
                if(user.getPromotion().equals(all_promos[i])) {
                    promo.setSelection(i);
                    position_promo = i;
                    break;
                }
            }
        }

        // Initializing an ArrayAdapter (GENRE)
        ArrayAdapter<String> spinnerArrayAdapterGenre = new ArrayAdapter<String>(this,R.layout.spinner_item, all_genre);
        spinnerArrayAdapterGenre.setDropDownViewResource(R.layout.spinner_item);
        genre.setAdapter(spinnerArrayAdapterGenre);

        genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Resources resources = ProfilSettings.this.getResources();
                position_genre = position;

                String drawable = "avatar_" + promo_drawable_name[position_promo] + "_" + genre_drawable_name[position_genre];
                if (position_promo == 0 || position_genre == 0)
                    drawable = "avatar_default";

                avatar.setImageDrawable(resources.getDrawable(resources.getIdentifier(drawable, "drawable", ProfilSettings.this.getPackageName())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        if(!user.getGender().isEmpty()) {
            for(int i=0; i<all_genre.length;i++) {
                if(user.getGender().equals(genre_drawable_name[i])) {
                    genre.setSelection(i);
                    position_genre = i;
                    break;
                }
            }
        }

        description.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                nb_caractere.setText(Integer.toString(NB_WORDS_MAX - s.length()));
            }
        });

        ToggleButton notif = (ToggleButton)findViewById(R.id.notifs);
        ToggleButton addEvents = (ToggleButton)findViewById(R.id.addEvents);

        SharedPreferences prefs = getSharedPreferences(
                Signin.class.getSimpleName(), Signin.MODE_PRIVATE);

        notif.setChecked(prefs.getBoolean("notifications", false));
        addEvents.setChecked(prefs.getString("addEventToCalender", "false").equals("true"));

        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefs = getSharedPreferences(
                        Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                if (isChecked) {
                    prefs.putBoolean("notifications", true);
                } else {
                    prefs.putBoolean("notifications", false);
                }
                prefs.commit();
            }
        });

        addEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefs = getSharedPreferences(
                        Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                if (isChecked) {
                    prefs.putString("addEventToCalender", "true");
                } else {
                    prefs.putString("addEventToCalender", "false");
                }
                prefs.commit();
            }
        });
    }

    public void updateProfil(User user){
        JSONObject json = new JSONObject();
        try {
            json.put("name", nom.getText().toString());
            json.put("username", user.getUsername());
            json.put("description", description.getText().toString());
            json.put("email", email.getText().toString());
            json.put("emailpublic", user.isEmailPublic());
            if(!promo.getSelectedItem().equals("Inconnu"))
                json.put("promotion", promo.getSelectedItem());
            else
                json.put("promotion", "");
            json.put("gender", genre_drawable_name[position_genre]);

            JSONArray events = new JSONArray();
            for(String s : user.getEvents())
                events.put(s);

            json.put("events", events);

            JSONArray likes = new JSONArray();
            for(String s : user.getPostsLiked())
                likes.put(s);

            json.put("postsliked", likes);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpPut put = new HttpPut(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if(output != null) {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
                else
                    Toast.makeText(ProfilSettings.this, "Erreur lors de la modification de profil ...", Toast.LENGTH_LONG).show();
            }
        });
        put.execute(HttpGet.ROOTUSER + "/" + user.getId() + "?token=" + HttpGet.credentials.getSessionToken(), json.toString());
    }

    public void deleteAccount(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfilSettings.this);

        // set title
        alertDialogBuilder.setTitle("Suppression de compte");

        // set dialog message
        alertDialogBuilder
                .setMessage("Es-tu sûr de vouloir supprimer ton compte ?")
                .setCancelable(false)
                .setPositiveButton("OUI",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogAlert,int id) {
                        HttpDelete delete = new HttpDelete(new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                HttpGet.credentials = null;
                                File.writeSettings(ProfilSettings.this, "");

                                Intent activity = new Intent(ProfilSettings.this, TutoActivity.class);
                                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(activity);
                                finish();
                            }
                        });
                        delete.execute(HttpGet.ROOTUSER + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
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
}
