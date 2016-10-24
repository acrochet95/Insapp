package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpDelete;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.http.HttpPost;
import fr.insapp.insapp.modeles.Association;
import fr.insapp.insapp.modeles.Credentials;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.modeles.Notification;
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.modeles.User;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;


public class NotificationsFragment extends FragmentRefreshable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout mainLayout = null;

    private SwipeRefreshLayout swipeView = null;

    private ImageLoaderCache imageLoader = null;

    private RelativeLayout refreshRl = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationsFragment() {
        // Required empty public constructor
    }


    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if(mainLayout != null)
            refresh(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);

        imageLoader = new ImageLoaderCache(getContext());

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.content);

        mainLayout = (LinearLayout) view.findViewById(R.id.notifLayout);

        refreshRl = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                refresh(true);
            }
        });

        //refresh(false);
        // Inflate the layout for this fragment
        return view;
    }

    public boolean isInitialised(){
        return init;
    }

    public void refresh(final boolean refreshFromSwipe) {

        view.findViewById(R.id.nonotification).setVisibility(View.GONE);

        init = true;

        if(!refreshFromSwipe)
            view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        if(mainLayout.getChildCount() > 1)
            mainLayout.removeViews(1, mainLayout.getChildCount()-1);

        SharedPreferences.Editor sharedPref_edit = getContext().getSharedPreferences(Signin.class.getSimpleName(), Signin.MODE_PRIVATE).edit();
        sharedPref_edit.putInt("nb_notifs", 0);
        sharedPref_edit.commit();

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if(!output.isEmpty()){
                    swipeView.setRefreshing(false);

                    if(view.findViewById(R.id.loadingPanel).getVisibility() == View.VISIBLE)
                        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                    try {
                        JSONObject json = new JSONObject(output);
                        JSONArray jsonarray = json.optJSONArray("notifications");
                        if(jsonarray != null) {

                            if (!refreshFromSwipe)
                                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);


                            for (int i = 0; i < jsonarray.length(); i++) {
                                final JSONObject jsonobject = jsonarray.getJSONObject(i);

                                final Notification notif = new Notification(jsonobject);

                                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View childLayout = layoutInflater.inflate(R.layout.content_notification, (ViewGroup) view.findViewById(R.id.child_id), false);
                                mainLayout.addView(childLayout, i + 1);

                                if (!notif.isSeen()) {
                                    childLayout.setBackgroundColor(getResources().getColor(R.color.not_seen_notif));
                                    HttpDelete seen = new HttpDelete(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {

                                        }
                                    });
                                    seen.execute(HttpGet.ROOTNOTIFICATION + "/" + HttpGet.credentials.getUserID() + "/" + notif.getId() + "?token=" + HttpGet.credentials.getSessionToken());
                                }

                                final ImageView img = (ImageView) childLayout.findViewById(R.id.image);
                                final ImageView img_notif = (ImageView) childLayout.findViewById(R.id.img_notif);
                                TextView text = (TextView) childLayout.findViewById(R.id.description);

                                text.setText(Html.fromHtml("<FONT color='BLACK'>" + notif.getMessage() + "</FONT> <FONT color='GREY'>" + Operation.displayedDate(notif.getDate()) + "</FONT>"));

                                if (notif.getType().equals("tag") || notif.getType().equals("post")) {

                                    HttpGet post = new HttpGet(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            try {
                                                final Post post = new Post(new JSONObject(output));

                                                imageLoader.DisplayImage(HttpGet.IMAGEURL + post.getPhotoURL(), img_notif);

                                                if (notif.getType().equals("tag")) {

                                                    childLayout.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            Intent i = new Intent(getActivity(), Comments.class);
                                                            i.putExtra("id", post.getId());
                                                            i.putExtra("asso", post.getAssociation());
                                                            i.putExtra("description", post.getDescription());
                                                            i.putExtra("date", post.getDate().getTime());
                                                            i.putParcelableArrayListExtra("comments", post.getComments());
                                                            i.putExtra("taggedCommentID", notif.getCommentID());
                                                            startActivity(i);
                                                        }
                                                    });
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    post.execute(HttpGet.ROOTPOST + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
                                }

                                if (notif.getType().equals("post") || notif.getType().equals("event")) {
                                    int id = MemoryStorage.findAsso(notif.getSender());
                                    if (id == -1) {
                                        HttpGet request = new HttpGet(new AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) {
                                                try {
                                                    final Association asso = new Association(new JSONObject(output));
                                                    MemoryStorage.addAsso(asso);

                                                    imageLoader.DisplayImage(HttpGet.IMAGEURL + asso.getProfilPicture(), img);

                                                    img.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(getActivity(), AssociationProfil.class);
                                                            i.putExtra("asso", asso);
                                                            startActivity(i);
                                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                                        }
                                                    });

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        request.execute(HttpGet.ROOTASSOCIATION + "/" + notif.getSender() + "?token=" + HttpGet.credentials.getSessionToken());
                                    } else {
                                        final Association asso = MemoryStorage.all_assos.get(id);
                                        imageLoader.DisplayImage(HttpGet.IMAGEURL + asso.getProfilPicture(), img);

                                        img.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent i = new Intent(getActivity(), AssociationProfil.class);
                                                i.putExtra("asso", asso);
                                                startActivity(i);
                                            }
                                        });

                                    }
                                }


                                if (notif.getType().equals("tag")) {
                                    HttpGet request = new HttpGet(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            try {
                                                final User user = new User(new JSONObject(output));

                                                Resources resources = getActivity().getResources();
                                                int id = resources.getIdentifier(Operation.drawableProfilName(user), "drawable", getActivity().getPackageName());

                                                Drawable dr = getResources().getDrawable(id);
                                                Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

                                                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
                                                img.setImageDrawable(d);

                                                img.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent i = new Intent(getActivity(), ProfilActivity.class);
                                                        i.putExtra("user", user);
                                                        startActivity(i);
                                                    }
                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    request.execute(HttpGet.ROOTUSER + "/" + notif.getSender() + "?token=" + HttpGet.credentials.getSessionToken());
                                } else if (notif.getType().equals("post")) {

                                    HttpGet post = new HttpGet(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            try {
                                                Post post = new Post(new JSONObject(output));

                                                imageLoader.DisplayImage(HttpGet.IMAGEURL + post.getPhotoURL(), img_notif);

                                                childLayout.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Intent i = new Intent(getActivity(), PostActivity.class);
                                                        i.putExtra("notification", notif);
                                                        startActivity(i);
                                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                                                    }
                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    post.execute(HttpGet.ROOTPOST + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
                                } else if (notif.getType().equals("event")) {

                                    HttpGet event = new HttpGet(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            try {
                                                final Event event = new Event(new JSONObject(output));

                                                imageLoader.DisplayImage(HttpGet.IMAGEURL + event.getPhotoURL(), img_notif);

                                                childLayout.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Intent i = new Intent(getActivity(), EventProfil.class);
                                                        i.putExtra("event", event);
                                                        i.putExtra("asso", event.getAssociation());
                                                        startActivity(i);
                                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                                    }
                                                });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    event.execute(HttpGet.ROOTEVENT + "/" + notif.getContent() + "?token=" + HttpGet.credentials.getSessionToken());
                                }
                            }
                        }
                        else{
                            view.findViewById(R.id.nonotification).setVisibility(View.VISIBLE);

                            ImageView refreshImg = (ImageView) view.findViewById(R.id.refresh);
                            refreshImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    refresh(false);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if(Utils.isNetworkAvailable(getActivity())) {
                        Intent i = new Intent(getActivity(), Login.class);
                        startActivity(i);
                    }
                    else
                        Toast.makeText(getContext(), "Problème avec internet", Toast.LENGTH_LONG).show();

                    swipeView.setRefreshing(false);
                }
            }
        });
        request.execute(HttpGet.ROOTNOTIFICATION + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());


    }

}
