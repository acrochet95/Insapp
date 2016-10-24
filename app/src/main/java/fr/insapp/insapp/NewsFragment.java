package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Operation;
import fr.insapp.insapp.utility.Utils;


public class NewsFragment extends FragmentRefreshable {
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
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsFragment() {
        // Required empty public constructor
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
    public void onResume() {
        super.onResume();

        if(mainLayout != null)
            refresh(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);

        if (savedInstanceState != null) {
            String id = savedInstanceState.getString("id");
            String userID = savedInstanceState.getString("userID");
            String username = savedInstanceState.getString("username");
            String sessionToken = savedInstanceState.getString("sessionToken");
            HttpGet.credentials = new Credentials(id, userID, username, sessionToken);
            HttpGet.info_user = savedInstanceState.getString("info_user");
        }

        imageLoader = new ImageLoaderCache(this.getContext());

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.content);

        mainLayout = (LinearLayout) view.findViewById(R.id.newsLayout);

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

    public void refresh(final boolean refreshFromSwipe){
        init = true;

        if(!refreshFromSwipe)
            view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        mainLayout.removeViews(0, mainLayout.getChildCount());
        MemoryStorage.all_posts.clear();

        if(HttpGet.credentials != null) {

            HttpGet request = new HttpGet(new AsyncResponse() {

                public void processFinish(String output) {
                    if (!output.isEmpty()) {
                        try {
                            JSONArray jsonarray = new JSONArray(output);

                            // refresh done
                            swipeView.setRefreshing(false);

                            if(view.findViewById(R.id.loadingPanel).getVisibility() == View.VISIBLE)
                                view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                            for (int i = 0; i < jsonarray.length(); i++) {
                                final JSONObject jsonobject = jsonarray.getJSONObject(i);

                                Post pTmp = new Post(jsonobject);
                                final Post post = MemoryStorage.addPost(pTmp);

                                // New post
                                //if (post == pTmp | firstTime) {

                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View childLayout = layoutInflater.inflate(R.layout.postnews, (ViewGroup) view.findViewById(R.id.child_id), false);
                                    mainLayout.addView(childLayout, i);

                                    TextView title = (TextView) childLayout.findViewById(R.id.titlePost);
                                    TextView date = (TextView) childLayout.findViewById(R.id.date);
                                    TextView description = (TextView) childLayout.findViewById(R.id.description);
                                    final TextView likes = (TextView) childLayout.findViewById(R.id.nbLikes);
                                    final TextView coms = (TextView) childLayout.findViewById(R.id.nbComments);

                                    int id = MemoryStorage.findAsso(post.getAssociation());
                                    if (id == -1) {
                                        final HttpGet asso = new HttpGet(new AsyncResponse() {
                                            @Override
                                            public void processFinish(String output) {
                                                try {
                                                    JSONObject json = new JSONObject(output);

                                                    final Association association = new Association(json);
                                                    MemoryStorage.addAsso(association); // add in memory

                                                    showAssoInformation(association, childLayout);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        asso.execute(HttpGet.ROOTASSOCIATION + "/" + post.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());
                                    } else {
                                        showAssoInformation(MemoryStorage.getAll_assos().get(id), childLayout);
                                    }

                                    // Go to comments if we click on image
                                    final ImageView image = (ImageView) childLayout.findViewById(R.id.image);
                                    image.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View v) {
                                            Intent secondeActivite = new Intent(getActivity(), Comments.class);
                                            secondeActivite.putExtra("id", post.getId());
                                            secondeActivite.putExtra("asso", post.getAssociation());
                                            secondeActivite.putExtra("description", post.getDescription());
                                            secondeActivite.putExtra("date", post.getDate().getTime());
                                            secondeActivite.putParcelableArrayListExtra("comments", post.getComments());
                                            startActivity(secondeActivite);
                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                        }

                                    });

                                    description.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View v) {
                                            Intent secondeActivite = new Intent(getActivity(), Comments.class);
                                            secondeActivite.putExtra("id", post.getId());
                                            secondeActivite.putExtra("asso", post.getAssociation());
                                            secondeActivite.putExtra("description", post.getDescription());
                                            secondeActivite.putExtra("date", post.getDate().getTime());
                                            secondeActivite.putParcelableArrayListExtra("comments", post.getComments());
                                            startActivity(secondeActivite);
                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                        }

                                    });


                                    LinearLayout comsBox = (LinearLayout) childLayout.findViewById(R.id.commentBox);
                                    comsBox.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View v) {
                                            Intent secondeActivite = new Intent(getActivity(), Comments.class);
                                            secondeActivite.putExtra("id", post.getId());
                                            secondeActivite.putExtra("asso", post.getAssociation());
                                            secondeActivite.putExtra("description", post.getDescription());
                                            secondeActivite.putExtra("date", post.getDate().getTime());
                                            secondeActivite.putParcelableArrayListExtra("comments", post.getComments());
                                            startActivity(secondeActivite);
                                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                        }

                                    });

                                    // LIKE BUTTON //
                                    final ImageView likeButton = (ImageView) childLayout.findViewById(R.id.like);
                                    if (post.postLikedBy(HttpGet.credentials.getUserID())) {
                                        likeButton.setImageDrawable(getResources().getDrawable(R.drawable.liked));
                                    }

                                    LinearLayout likeBox = (LinearLayout) childLayout.findViewById(R.id.likeBox);
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
                                                            //all_posts.get(i) = new Post(new JSONObject());
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
                                //}
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
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
            request.execute(HttpGet.ROOTPOST + "?token=" + HttpGet.credentials.getSessionToken());
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

    public void showAssoInformation(final Association association, View childLayout){
        final ImageView image = (ImageView) childLayout.findViewById(R.id.assoImage);

        //ImageLoaderCache il = new ImageLoaderCache(MainActivity.this);
        imageLoader.DisplayImage(HttpGet.IMAGEURL + association.getProfilPicture(), image);

        // Links to the asso's page
        TextView assoLink = (TextView) childLayout.findViewById(R.id.assoLink);
        assoLink.setText("@" + association.getName());

        assoLink.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(getActivity(), AssociationProfil.class);
                secondeActivite.putExtra("asso", association);
                startActivity(secondeActivite);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }

        });

        image.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent secondeActivite = new Intent(getActivity(), AssociationProfil.class);
                secondeActivite.putExtra("asso", association);
                startActivity(secondeActivite);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }

        });
    }

}
