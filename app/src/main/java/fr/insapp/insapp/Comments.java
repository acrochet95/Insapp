package fr.insapp.insapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.http.HttpPut;
import fr.insapp.insapp.modeles.Association;
import fr.insapp.insapp.modeles.Comment;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.modeles.Notification;
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.modeles.Tag;
import fr.insapp.insapp.modeles.User;
import fr.insapp.insapp.utility.File;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.view.EditText;

public class Comments extends Activity {

    private LinearLayout mainLayout = null;
    private ScrollView scrollView = null;
    private LinearLayout tagLayout = null;
    private LinearLayout tagUsers = null;
    private ImageView arrow = null;
    private Button write = null;
    private EditText commentBox = null;

    private String taggedCommentID = "";
    private Association a = null;
    private ArrayList<User> users = null;
    private Map<Integer, Drawable> user_icons = null;

    private ImageLoaderCache imageLoader = null;

    private ArrayList<Tag> tags = new ArrayList<Tag>();

    private boolean userWrittingTag = false;
    private int tagStartsAt = 0;
    private String tagWritting = "";
    private boolean autochange = false;
    private boolean deleteTag = false;
    private int last_count = 0;

    private int last_cursor_position = 0;

    private String id = null;
    private String asso = null;
    private String description = null;
    private Date date = null;
    private ArrayList<Comment> comments = null;

    private Notification notif = null;
    private View viewCommentID = null;
    private boolean viewCommentIDFocused = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(HttpGet.credentials != null) {
            outState.putString("id", HttpGet.credentials.getId());
            outState.putString("userID", HttpGet.credentials.getUserID());
            outState.putString("username", HttpGet.credentials.getUsername());
            outState.putString("sessionToken", HttpGet.credentials.getSessionToken());
            outState.putString("info_user", HttpGet.info_user);


            //outState.putParcelableArrayList("events", MemoryStorage.all_events);
            //outState.putParcelableArrayList("assos", MemoryStorage.all_assos);
        }

        if(notif != null) {
            outState.putParcelable("notif", notif);
            outState.putString("notif_content", notif.getContent());
        }

        outState.putParcelable("asso", a);
        super.onSaveInstanceState(outState);
    }

    public void onBackPressed() {
        // It's expensive, if running turn it off.
        if(tagLayout.getVisibility() == View.VISIBLE) {

            userWrittingTag = false;
            tagLayout.setVisibility(View.GONE);

            tagWritting = "";
            tagStartsAt = 0;
        }
        else
            super.onBackPressed();
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first

        tagLayout.setVisibility(View.GONE);
        findViewById(R.id.loadingTagUsers).setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            String id = savedInstanceState.getString("id");
            String userID = savedInstanceState.getString("userID");
            String username = savedInstanceState.getString("username");
            String sessionToken = savedInstanceState.getString("sessionToken");
            if(!userID.isEmpty() && !sessionToken.isEmpty())
                HttpGet.credentials = new Credentials(id, userID, username, sessionToken);

            notif = savedInstanceState.getParcelable("notif");
            /*if(savedInstanceState.getBoolean("notif", false)){
                notif = new Notification(savedInstanceState.getString("notif_sender"),
                                        savedInstanceState.getString("notif_receiver"),
                                        savedInstanceState.getString("notif_content"),
                                        savedInstanceState.getString("notif_commentID"),
                                        savedInstanceState.getString("notif_message"),
                                        savedInstanceState.getBoolean("notif_seen"),
                                        savedInstanceState.getString("notif_type"));
            }
*/

            //MemoryStorage.all_events = savedInstanceState.getParcelableArrayList("events");
            //MemoryStorage.all_assos = savedInstanceState.getParcelableArrayList("assos");

            HttpGet.info_user = savedInstanceState.getString("info_user");
            a = savedInstanceState.getParcelable("asso");
        }
        setContentView(R.layout.activity_comments);

        Intent intent = getIntent();

        users = new ArrayList<User>();
        user_icons = new HashMap<Integer, Drawable>();

        imageLoader = new ImageLoaderCache(this);

        mainLayout = (LinearLayout) findViewById(R.id.commentsLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        tagLayout = (LinearLayout) findViewById(R.id.tagLayout);
        tagUsers = (LinearLayout) findViewById(R.id.tagUsers);
        arrow = (ImageView) findViewById(R.id.arrow);
        write = (Button) findViewById(R.id.poster);
        commentBox = (EditText) findViewById(R.id.writeComment);

        tagLayout.setVisibility(View.GONE);
        findViewById(R.id.loadingTagUsers).setVisibility(View.GONE);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent secondeActivite = new Intent(Comments.this, MainActivity.class);
                secondeActivite.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                secondeActivite.putExtra("position", 0);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
            }
        });

        viewCommentIDFocused = false;
        userWrittingTag = false;
        last_count = 0;
        commentBox.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(viewCommentID != null && !viewCommentIDFocused) {
                    scrollView.requestChildFocus(viewCommentID, viewCommentID);
                    viewCommentIDFocused = true;
                }

                if (autochange) { // skip execution if triggered by code
                    //last_count = s.length();
                    autochange = false; // next change is not triggered by code
                    return;
                }

                if(s.length() == 0){
                    last_count = 0;
                    userWrittingTag = false;
                    tagWritting = "";
                    tagStartsAt = 0;
                }

                // If we delete
                if (s.length() - last_count < 0) {
                    if (userWrittingTag) {
                        String str = commentBox.getText().toString();

                        String n = str.substring(0, tagStartsAt) + str.substring(tagStartsAt + tagWritting.length(), str.length());

                        autochange = true;
                        commentBox.setText(n);
                        last_count = n.length();
                        commentBox.setSelection(tagStartsAt);
                        userWrittingTag = false;
                        tagWritting = "";
                        tagStartsAt = 0;

                        deleteTag = true;

                        tagLayout.setVisibility(View.GONE);
                    }
                }
                else {
                    deleteTag = false;
                    int position = commentBox.getSelectionStart()-1;
                    if(position >= 0) {
                        char c = s.charAt(position);

                        if (userWrittingTag) {

                            if (c == ' ' || (position <= tagStartsAt || position - 1 > tagStartsAt + tagWritting.length())) {
                                userWrittingTag = false;
                                tagLayout.setVisibility(View.GONE);
                            } else {
                                tagWritting += s.toString().charAt(position);
                                showUserstoTag(tagWritting);
                            }
                        } else {
                            if (c == '@') {
                                userWrittingTag = true;
                                tagStartsAt = position;
                                tagWritting = "";
                            }
                        }
                    }
                }
                if(!deleteTag)
                    last_count = s.length();
            }

            public void afterTextChanged(Editable s) {

                if (s.length() > 0)
                    write.setTextColor(getResources().getColor(R.color.text_button_comment));
                else
                    write.setTextColor(getResources().getColor(R.color.barre_division));

            }
        });

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!commentBox.getText().toString().isEmpty()) {

                    String text = commentBox.getText().toString();
                    JSONObject json = new JSONObject();
                    try {
                        json.put("user", HttpGet.credentials.getUserID());
                        json.put("content", text);

                        JSONArray jsonArray = new JSONArray();
                        List<String> already_tagged = new ArrayList<>();
                        for (Tag tag : tags) {
                            // if the user didn't delete it
                            if (text.contains(tag.getName()) && already_tagged.lastIndexOf(tag.getName()) == -1) {
                                JSONObject jsonTag = new JSONObject();
                                jsonTag.put("user", tag.getUser());
                                jsonTag.put("name", tag.getName());

                                jsonArray.put(jsonTag);
                                already_tagged.add(tag.getName());
                            }
                        }
                        json.put("tags", jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    HttpPost request = new HttpPost(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {

                            commentBox.setText("");

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(commentBox.getWindowToken(), 0);

                            try {
                                Post p = new Post(new JSONObject(output));
                                showComments(id, p.getComments());
                                scrollView.fullScroll(View.FOCUS_DOWN);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    request.execute(HttpGet.ROOTPOST + "/" + id + "/comment?token=" + HttpGet.credentials.getSessionToken(), json.toString());
                }
            }
        });

        notif = intent.getParcelableExtra("notification");
        if(notif != null){

            taggedCommentID = notif.getCommentID();

            if(HttpGet.credentials != null) {

                HttpGet post = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        try {
                            Post p = new Post(new JSONObject(output));
                            id = p.getId();
                            description = p.getDescription();
                            date = p.getDate();
                            comments = p.getComments();

                            HttpGet asso = new HttpGet(new AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    try {
                                        a = new Association(new JSONObject(output));
                                        showAssociation();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            asso.execute(HttpGet.ROOTASSOCIATION + "/" + p.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());

                            showComments(id, comments);

                            showPostInformation();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                post.execute(HttpGet.ROOTPOST + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());

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

                                        HttpGet post = new HttpGet(new AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) {
                                                try {
                                                    Post p = new Post(new JSONObject(output));
                                                    id = p.getId();
                                                    description = p.getDescription();
                                                    date = p.getDate();
                                                    comments = p.getComments();

                                                    HttpGet asso = new HttpGet(new AsyncResponse() {
                                                        @Override
                                                        public void processFinish(String output) {
                                                            try {
                                                                a = new Association(new JSONObject(output));
                                                                showAssociation();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                    asso.execute(HttpGet.ROOTASSOCIATION + "/" + p.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());

                                                    showComments(id, comments);

                                                    showPostInformation();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        post.execute(HttpGet.ROOTPOST + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());

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
                SharedPreferences.Editor sharedPref_edit = getApplicationContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
                sharedPref_edit.putInt("nb_notifs", nb);
                sharedPref_edit.commit();
            }
        }
        else {
            id = intent.getStringExtra("id");
            asso = intent.getStringExtra("asso");
            description = intent.getStringExtra("description");
            date = new Date(intent.getLongExtra("date", 0));

            if (intent.hasExtra("taggedCommentID"))
                taggedCommentID = intent.getStringExtra("taggedCommentID");

            comments = intent.getParcelableArrayListExtra("comments");

            for (Association aTmp : MemoryStorage.getAll_assos()) {
                if (aTmp.getId().equals(asso)) {
                    a = aTmp;
                    break;
                }
            }

            showAssociation();
            showPostInformation();
            showComments(id, comments);
        }
    }

    public void showAssociation(){
        TextView association = (TextView) findViewById(R.id.association);
        ImageView image = (ImageView) findViewById(R.id.image);

        imageLoader.DisplayImage(HttpGet.IMAGEURL + a.getProfilPicture(), image);
        association.setText("@" + a.getName());


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(Comments.this, AssociationProfil.class);
                activity.putExtra("asso", a);
                startActivity(activity);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
    }

    public void showPostInformation(){
        final TextView descriptionPost = (TextView) findViewById(R.id.description);
        TextView datePost = (TextView) findViewById(R.id.date);
        descriptionPost.setText(description);
        datePost.setText(Operation.displayedDate(date));
    }

    public void showComments(final String idPost, ArrayList<Comment> comments){

        // there are comments
        if(mainLayout.getChildCount() > 1) {
            mainLayout.removeViews(1, mainLayout.getChildCount() - 1);
        }

        for(int i=0; i<comments.size(); i++) {
            final Comment comment = comments.get(i);

            LayoutInflater layoutInflater = (LayoutInflater) Comments.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View childLayout = layoutInflater.inflate(R.layout.content_comments, (ViewGroup) findViewById(R.id.child_id), false);
            mainLayout.addView(childLayout, 1 + i);

            if(taggedCommentID.equals(comment.getId())) {
                viewCommentID = childLayout;
                childLayout.setBackgroundColor(getResources().getColor(R.color.not_seen_notif));
                scrollView.requestChildFocus(childLayout, childLayout);
            }

            final ImageView userImage = (ImageView) childLayout.findViewById(R.id.userImage);
            final TextView username = (TextView) childLayout.findViewById(R.id.username);
            TextView content = (TextView) childLayout.findViewById(R.id.description);
            TextView dateComment = (TextView) childLayout.findViewById(R.id.date);

            User userTmp = null;
            for(User u : users) {
                if (comment.getUser().equals(u.getId())) {
                    userTmp = u;
                    break;
                }
            }

            final User user = userTmp;
            if(user == null) {
                HttpGet get = new HttpGet(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(output);
                            final User user = new User(json);
                            users.add(user);

                            showUser(user, username, userImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                get.execute(HttpGet.ROOTUSER + "/" + comment.getUser() + "?token=" + HttpGet.credentials.getSessionToken());
            }
            else
                showUser(user, username, userImage);

            // TAGGING //
            String str = comment.getContent();

            Spannable spannable = new SpannableString(str);
            SpannableString ssText = new SpannableString(spannable);

            for(int j=0; j<comment.getTags().size(); j++){
                final Tag tag = comment.getTags().get(j);
                int id_start = str.indexOf(tag.getName());

                int id_end = id_start + tag.getName().length();

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        User user = null;
                        for(User u : users){
                            if(u.getId().equals(tag.getUser())) {
                                user = u;
                                break;
                            }
                        }

                        final Intent i = new Intent(Comments.this, ProfilActivity.class);
                        if(user != null) {
                            i.putExtra("user", user);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                        else {
                            HttpGet get = new HttpGet(new AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(output);
                                        User u = new User(json);
                                        i.putExtra("user", u);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            get.execute(HttpGet.ROOTUSER + "/" + tag.getUser() + "?token=" + HttpGet.credentials.getSessionToken());
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.tag));
                    }
                };
                ssText.setSpan(clickableSpan, id_start, id_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            content.setText(ssText);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setEnabled(true);

            dateComment.setText(Operation.displayedDate(comment.getDate()));


            LinearLayout content_comment = (LinearLayout) childLayout.findViewById(R.id.content);
            final LinearLayout delete_area = (LinearLayout) childLayout.findViewById(R.id.delete_comment);
            ImageView imageDelete = (ImageView) childLayout.findViewById(R.id.imageDelete);
            delete_area.setVisibility(View.GONE);
            if (!HttpGet.credentials.getUserID().equals(comment.getUser())) {
                imageDelete.setImageResource(R.drawable.warning);
                delete_area.setBackgroundColor(getResources().getColor(R.color.barre_division));
            }

            //if (HttpGet.credentials.getUserID().equals(comment.getUser())) {

            content_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delete_area.getVisibility() == View.GONE)
                        delete_area.setVisibility(View.VISIBLE);
                    else
                        delete_area.setVisibility(View.GONE);

                        delete_area.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (HttpGet.credentials.getUserID().equals(comment.getUser())) {
                                    HttpDelete delete = new HttpDelete(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            try {
                                                Post p = new Post(new JSONObject(output));
                                                showComments(idPost, p.getComments());

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    delete.execute(HttpGet.ROOTPOST + "/" + idPost + "/comment/" + comment.getId() + "?token=" + HttpGet.credentials.getSessionToken());
                                } else {

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Comments.this);

                                    // set title
                                    alertDialogBuilder.setTitle("Signaler utilisateur");

                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage("Voulez-vous signaler cet utilisateur ?")
                                            .setCancelable(true)
                                            .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogAlert, int id) {
                                                    HttpPut signaler = new HttpPut(new AsyncResponse() {
                                                        @Override
                                                        public void processFinish(String output) {

                                                            Toast.makeText(Comments.this, "Commentaire correctement signalé", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    signaler.execute(HttpGet.ROOTURL + "/report/" + idPost + "/comment/" + comment.getId() + "?token=" + HttpGet.credentials.getSessionToken());
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
                        });
                }
            });

        }
    }

    public void showUser(final User user, TextView username, ImageView userImage){
        Resources resources = Comments.this.getResources();
        int id = resources.getIdentifier(Operation.drawableProfilName(user), "drawable", Comments.this.getPackageName());

        if(user_icons.containsKey(id)){
            userImage.setImageDrawable(user_icons.get(id));
        }
        else {
            Drawable dr = getResources().getDrawable(id);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
            userImage.setImageDrawable(d);
            user_icons.put(id, d);
        }

        username.setText("@" + user.getUsername());

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(Comments.this, ProfilActivity.class);
                activity.putExtra("user", user);
                startActivity(activity);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
    }

    public void showUserstoTag(String username){
        tagUsers.removeAllViews();

        if(username.length() > 0) {
            tagLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.loadingTagUsers).setVisibility(View.VISIBLE);

            HttpGet request = new HttpGet(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    tagUsers.removeAllViews();
                    try {
                        findViewById(R.id.loadingTagUsers).setVisibility(View.GONE);

                        JSONObject json = new JSONObject(output);
                        JSONArray jsonArray = json.getJSONArray("users");
                        if(jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                final User user = new User(jsonArray.getJSONObject(i));

                                LayoutInflater layoutInflater = (LayoutInflater) Comments.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View childLayout = layoutInflater.inflate(R.layout.content_participants, (ViewGroup) findViewById(R.id.child_id), false);
                                tagUsers.addView(childLayout, i);

                                final ImageView userImage = (ImageView) childLayout.findViewById(R.id.userImage);
                                final TextView username = (TextView) childLayout.findViewById(R.id.username);
                                final TextView name = (TextView) childLayout.findViewById(R.id.name);

                                showUser(user, username, userImage);
                                name.setText(user.getName());

                                childLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String str = commentBox.getText().toString();
                                        String text = str.substring(0, tagStartsAt) + "@" + user.getUsername() + " " + str.substring(tagStartsAt + tagWritting.length() + 1, str.length());
                                        tags.add(new Tag("", user.getId(), "@" + user.getUsername()));

                                        tagLayout.setVisibility(View.GONE);

                                        userWrittingTag = false;
                                        autochange = true;
                                        commentBox.setText(text + " ");
                                        commentBox.setSelection(tagStartsAt + user.getUsername().length() + 2); // + 2 for "@" and " "

                                        tagWritting = "";
                                        tagStartsAt = 0;
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {

                        userWrittingTag = false;
                        tagLayout.setVisibility(View.GONE);

                        tagWritting = "";
                        tagStartsAt = 0;

                        e.printStackTrace();
                    }
                }
            });
            request.execute(HttpGet.ROOTSEARCHUSER + "/" + username + "?token=" + HttpGet.credentials.getSessionToken());
        }
        else
            tagLayout.setVisibility(View.GONE);
    }
}
