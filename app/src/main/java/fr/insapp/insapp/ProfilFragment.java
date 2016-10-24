package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.modeles.User;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Operation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFragment extends FragmentRefreshable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view = null;

    private SwipeRefreshLayout swipeLayout = null;

    private ImageLoaderCache imageLoader = null;

    private TextView username = null;
    private TextView name = null;
    private TextView email = null;
    private TextView promo = null;
    private TextView description = null;

    private ImageView img = null;

    private User user = null;
    private boolean ourProfil = true;
    private boolean userSent = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFragment newInstance(String param1, String param2) {
        ProfilFragment fragment = new ProfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if(view != null)
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
        view = inflater.inflate(R.layout.fragment_profil, container, false);

        imageLoader = new ImageLoaderCache(getContext());

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeView);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                refresh(true);
            }
        });

        username = (TextView) view.findViewById(R.id.username);
        name = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.email);
        promo = (TextView) view.findViewById(R.id.promo);
        description = (TextView) view.findViewById(R.id.description);

        refresh(false);

        return view;
    }

    @Override
    public boolean isInitialised() {
        return init;
    }

    public void refresh(boolean refreshFromSwipe){

        // on refresh tout le temps
        init = false;

        TextView text_event = (TextView) view.findViewById(R.id.title_events);
        text_event.setVisibility(View.GONE);

        ImageView settings = (ImageView) view.findViewById(R.id.update);
        img = (ImageView) view.findViewById(R.id.imageProfil);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Intent activity = new Intent(getActivity(), ProfilSettings.class);
                    activity.putExtra("user", user);
                    startActivity(activity);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Intent activity = new Intent(getActivity(), ProfilSettings.class);
                    activity.putExtra("user", user);
                    startActivity(activity);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            }
        });

        ImageView credits = (ImageView) view.findViewById(R.id.credits);
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Intent activity = new Intent(getActivity(), Credits.class);
                    startActivity(activity);
                }
            }
        });

        HttpGet request = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    swipeLayout.setRefreshing(false);
                    user = new User(new JSONObject(output));
                    writeUserInformation(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        request.execute(HttpGet.ROOTUSER + "/" + HttpGet.credentials.getUserID() + "?token=" + HttpGet.credentials.getSessionToken());
    }

    public void writeUserInformation(final User user){
        final LinearLayout content = (LinearLayout) view.findViewById(R.id.content);

        showImageProfil(user, img);

        username.setText("@" + user.getUsername());
        name.setText(user.getName());
        email.setText(user.getEmail());
        promo.setText(user.getPromotion());
        description.setText(user.getDescription());

        if (user.getName().isEmpty()) {
            name.setVisibility(View.GONE);
        }

        if (user.getEmail().isEmpty()){
            email.setVisibility(View.GONE);
        }

        if (user.getPromotion().isEmpty()){
            promo.setVisibility(View.GONE);
        }

        if (user.getDescription().isEmpty()){
            description.setVisibility(View.GONE);
        }

        TextView text_event = (TextView) view.findViewById(R.id.title_events);
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

            TextView text_event = (TextView) view.findViewById(R.id.title_events);
            text_event.setVisibility(View.VISIBLE);

            Date atm = Calendar.getInstance().getTime();

            int nb = 0;
            Collections.sort(events);
            for (final Event event : events) {

                if (event.getDateEnd().getTime() > atm.getTime()) {
                    LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View childLayout = layoutInflater.inflate(R.layout.content_event_associationprofil, (ViewGroup) view.findViewById(R.id.child_id), false);
                    content.addView(childLayout, 5 + nb);

                    childLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent activity = new Intent(getActivity(), EventProfil.class);
                            activity.putExtra("event", event);
                            activity.putExtra("asso", event.getAssociation());
                            startActivity(activity);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
        Resources resources = getActivity().getResources();
        imageView.setImageDrawable(resources.getDrawable(resources.getIdentifier(Operation.drawableProfilName(user), "drawable", getActivity().getPackageName())));
    }
}
