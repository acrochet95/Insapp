package fr.insapp.insapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Operation;

public class PostActivity extends Activity {

    private ImageView news = null;
    private ImageView events = null;
    private ImageView assos = null;
    private ImageView notifs = null;
    private ImageView profil = null;
    private ImageView arrow = null;

    private ImageLoaderCache imageLoader = null;

    private Notification notif = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageLoader = new ImageLoaderCache(PostActivity.this);

        news = (ImageView)findViewById(R.id.newsB);
        events = (ImageView)findViewById(R.id.eventsB);
        assos = (ImageView)findViewById(R.id.assosB);
        notifs = (ImageView)findViewById(R.id.notifB);
        profil = (ImageView)findViewById(R.id.profilB);

        // Color image and text in red
        int color_menu = getResources().getColor(R.color.theme_red);
        news.setColorFilter(new LightingColorFilter(color_menu, color_menu));

        TextView newsText = (TextView) findViewById(R.id.newsText);
        newsText.setTextColor(color_menu);

        Intent i = getIntent();

        arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 0);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }
        });


        news.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 0);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        events.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 1);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }

        });

        assos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 2);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        notifs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 3);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
            }

        });

        profil.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, MainActivity.class);
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
        if(intent.hasExtra("notification")) {
            notif = intent.getParcelableExtra("notification");

            if (HttpGet.credentials != null) {

                HttpGet request = new HttpGet(new AsyncResponse() {

                    public void processFinish(String output) {
                        if (!output.isEmpty()) {
                            try {
                                final JSONObject jsonobject = new JSONObject(output);

                                Post pTmp = new Post(jsonobject);
                                final Post post = MemoryStorage.addPost(pTmp);

                                showPost(post);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent i = new Intent(getBaseContext(), Login.class);
                            startActivity(i);
                        }

                    }
                });
                request.execute(HttpGet.ROOTPOST + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
            } else {

                HttpGet.info_user = File.readSettings(this);

                if (!HttpGet.info_user.equals("")) {

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

                                        HttpGet request = new HttpGet(new AsyncResponse() {

                                            public void processFinish(String output) {
                                                try {
                                                    final JSONObject jsonobject = new JSONObject(output);

                                                    Post pTmp = new Post(jsonobject);
                                                    final Post post = MemoryStorage.addPost(pTmp);

                                                    showPost(post);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        request.execute(HttpGet.ROOTPOST + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    login.execute(HttpGet.ROOTLOGIN, json.toString());
                }

                if (!notif.isSeen()) {
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE);
                    int nb = sharedPref.getInt("nb_notifs", 0) - 1;
                    SharedPreferences.Editor sharedPref_edit2 = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                    sharedPref_edit2.putInt("nb_notifs", nb);
                    sharedPref_edit2.commit();
                }
            }
        }
    }

    public void showPost(final Post post){
        TextView title = (TextView) findViewById(R.id.titlePost);
        TextView date = (TextView) findViewById(R.id.date);
        TextView description = (TextView) findViewById(R.id.description);
        final TextView likes = (TextView) findViewById(R.id.nbLikes);
        final TextView coms = (TextView) findViewById(R.id.nbComments);

        int id = MemoryStorage.findAsso(post.getAssociation());

        if (id == -1) {
            final HttpGet asso = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        JSONObject json = new JSONObject(output);

                        final Association association = new Association(json);
                        MemoryStorage.addAsso(association); // add in memory

                        showAssoInformation(association);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            asso.execute(HttpGet.ROOTASSOCIATION + "/" + post.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());
        } else {
            showAssoInformation(MemoryStorage.getAll_assos().get(id));
        }

        // Go to comments if we click on image
        final ImageView image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, Comments.class);
                secondeActivite.putExtra("id", post.getId());
                secondeActivite.putExtra("asso", post.getAssociation());
                secondeActivite.putExtra("description", post.getDescription());
                secondeActivite.putExtra("date", post.getDate().getTime());
                secondeActivite.putParcelableArrayListExtra("comments", post.getComments());
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }

        });

        description.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, Comments.class);
                secondeActivite.putExtra("id", post.getId());
                secondeActivite.putExtra("asso", post.getAssociation());
                secondeActivite.putExtra("description", post.getDescription());
                secondeActivite.putExtra("date", post.getDate().getTime());
                secondeActivite.putParcelableArrayListExtra("comments", post.getComments());
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }

        });


        LinearLayout comsBox = (LinearLayout) findViewById(R.id.commentBox);
        comsBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, Comments.class);
                secondeActivite.putExtra("id", post.getId());
                secondeActivite.putExtra("asso", post.getAssociation());
                secondeActivite.putExtra("description", post.getDescription());
                secondeActivite.putExtra("date", post.getDate().getTime());
                secondeActivite.putParcelableArrayListExtra("comments", post.getComments());
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }

        });

        // LIKE BUTTON //
        final ImageView likeButton = (ImageView) findViewById(R.id.like);
        if (post.postLikedBy(HttpGet.credentials.getUserID())) {
            likeButton.setImageDrawable(getResources().getDrawable(R.drawable.liked));
        }

        LinearLayout likeBox = (LinearLayout) findViewById(R.id.likeBox);
        likeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final boolean liked = post.postLikedBy(HttpGet.credentials.getUserID());

                // Liked so we want to dislike
                if (liked) {
                    HttpDelete hpp = new HttpDelete(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            if (!output.isEmpty()) {
                                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                refreshPost(output, post, likes, coms);
                            }
                        }
                    });
                    hpp.execute(HttpGet.ROOTURL + "/post/" + post.getId() + "/like/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                } else { // We want to like
                    HttpPost hpp = new HttpPost(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            if (!output.isEmpty()) {
                                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.liked));
                                refreshPost(output, post, likes, coms);
                            }
                        }
                    });
                    hpp.execute(HttpGet.ROOTURL + "/post/" + post.getId() + "/like/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
                }
            }
        });
        // END LIKE BUTTON //

        imageLoader.DisplayImage(HttpGet.IMAGEURL + post.getPhotoURL(), image);

        title.setText(post.getTitle());
        date.setText(Operation.displayedDate(post.getDate()));
        description.setText(post.getDescription());
        likes.setText("(" + Integer.toString(post.getLikes().size()) + ")");
        coms.setText("(" + Integer.toString(post.getComments().size()) + ")");

        int c1 = getResources().getColor(R.color.gradient_start);
        int c2 = getResources().getColor(R.color.gradient_end);
        description.getPaint().setShader(new LinearGradient(0, 0, 0, 115, c1, c2, Shader.TileMode.CLAMP));
    }

    public void refreshPost(String output, final Post formerPost, final TextView likes, final TextView coms){

        try {

            JSONObject json = new JSONObject(output);

            Post tmp = new Post(json.getJSONObject("post"));

            int id = MemoryStorage.findPost(formerPost.getId());
            //MemoryStorage.addPost(tmp);
            MemoryStorage.getAll_posts().get(id).setLikes(tmp.getLikes());
            MemoryStorage.getAll_posts().get(id).setComments(tmp.getComments());

            likes.setText("(" + Integer.toString(tmp.getLikes().size()) + ")");
            coms.setText("(" + Integer.toString(tmp.getComments().size()) + ")");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showAssoInformation(final Association association){
        final ImageView image = (ImageView) findViewById(R.id.assoImage);

        imageLoader.DisplayImage(HttpGet.IMAGEURL + association.getProfilPicture(), image);

        // Links to the asso's page
        TextView assoLink = (TextView) findViewById(R.id.assoLink);
        assoLink.setText("@" + association.getName());

        assoLink.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, AssociationProfil.class);
                secondeActivite.putExtra("asso", association);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }

        });

        image.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(PostActivity.this, AssociationProfil.class);
                secondeActivite.putExtra("asso", association);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }

        });
    }

}
