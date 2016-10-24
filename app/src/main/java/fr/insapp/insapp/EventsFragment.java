package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Association;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Utils;

public class EventsFragment extends FragmentRefreshable {
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

    private static int nb_events_atm = 0;
    private static int nb_events_7days = 0;
    private static int nb_events_30days = 0;
    private static int nb_events_soon = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public EventsFragment() {
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
        view = inflater.inflate(R.layout.fragment_events, container, false);

        imageLoader = new ImageLoaderCache(getContext());

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.content);

        mainLayout = (LinearLayout) view.findViewById(R.id.eventsLayout);

        refreshRl = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        // Initialisation
        nb_events_atm = 0;
        nb_events_7days = 0;
        nb_events_30days = 0;
        nb_events_soon = 0;

        // Hide titles
        LinearLayout eventsNow = (LinearLayout) view.findViewById(R.id.today);
        eventsNow.setVisibility(View.GONE);

        LinearLayout eventsWeek = (LinearLayout) view.findViewById(R.id.week);
        eventsWeek.setVisibility(View.GONE);

        LinearLayout eventsMonth = (LinearLayout) view.findViewById(R.id.month);
        eventsMonth.setVisibility(View.GONE);

        LinearLayout eventsSoon = (LinearLayout) view.findViewById(R.id.soon);
        eventsSoon.setVisibility(View.GONE);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                refresh(true);
            }
        });

        return view;
    }

    public boolean isInitialised(){
        return init;
    }

    public void refresh(final boolean refreshFromSwipe){
        init = true;

        if(!refreshFromSwipe)
            view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        MemoryStorage.all_events.clear();
        final HttpGet events = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if(!output.isEmpty()) {
                    try {
                        JSONArray jsonarray = new JSONArray(output);

                        // refresh done
                        swipeView.setRefreshing(false);

                        if(view.findViewById(R.id.loadingPanel).getVisibility() == View.VISIBLE)
                            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        Date atm = Calendar.getInstance().getTime();


                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            final Event e = new Event(jsonobject);
                            MemoryStorage.addEvent(e);

                        }

                        //if(!firstTime) {
                        if (nb_events_atm > 0)
                            mainLayout.removeViews(1, nb_events_atm);
                        if (nb_events_7days > 0)
                            mainLayout.removeViews(2, nb_events_7days);
                        if (nb_events_30days > 0)
                            mainLayout.removeViews(3, nb_events_30days);
                        if (nb_events_soon > 0)
                            mainLayout.removeViews(4, nb_events_soon);

                        nb_events_atm = 0;
                        nb_events_7days = 0;
                        nb_events_30days = 0;
                        nb_events_soon = 0;
                        //}


                        Collections.sort(MemoryStorage.all_events);
                        for (int i = 0; i < MemoryStorage.all_events.size(); i++) {
                            final Event e = MemoryStorage.all_events.get(i);

                            if (e.getDateEnd().getTime() > atm.getTime()) {

                                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View childLayout = layoutInflater.inflate(R.layout.content_event, (ViewGroup) view.findViewById(R.id.child_id), false);

                                ImageView img = (ImageView) childLayout.findViewById(R.id.imageEvent);
                                TextView title = (TextView) childLayout.findViewById(R.id.title);
                                TextView date = (TextView) childLayout.findViewById(R.id.date);
                                TextView participants = (TextView) childLayout.findViewById(R.id.participants);
                                final TextView association = (TextView) childLayout.findViewById(R.id.association);

                                childLayout.setClickable(true);
                                childLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent activity = new Intent(getActivity(), EventProfil.class);
                                        activity.putExtra("event", e);
                                        activity.putExtra("asso", e.getAssociation());
                                        startActivity(activity);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                    }
                                });


                                // Event coming today
                                final long diff = e.getDateStart().getTime() - atm.getTime();
                                final float diffInDays = ((float)(diff) / (float)(1000 * 60 * 60 * 24));

                                if (diffInDays > 30) {
                                    mainLayout.addView(childLayout, 1 + nb_events_atm + 1 + nb_events_7days + 1 + nb_events_30days + 1 + nb_events_soon);
                                    nb_events_soon++;
                                } else if (diffInDays > 7) {
                                    mainLayout.addView(childLayout, 1 + nb_events_atm + 1 + nb_events_7days + 1 + nb_events_30days);
                                    nb_events_30days++;
                                } else if (diffInDays > 1) {
                                    // days > 1
                                    mainLayout.addView(childLayout, 1 + nb_events_atm + 1 + nb_events_7days);
                                    nb_events_7days++;
                                } else {
                                    mainLayout.addView(childLayout, 1 + nb_events_atm);
                                    nb_events_atm++;
                                }

                                imageLoader.DisplayImage(HttpGet.IMAGEURL + e.getPhotoURL(), img);

                                title.setText(e.getName());

                                // DATE
                                // Format 1 day
                                if (e.getDateStart().getDay() == e.getDateEnd().getDay() && e.getDateStart().getMonth() == e.getDateEnd().getMonth()) {
                                    DateFormat dateFormat_oneday = new SimpleDateFormat("'Le' dd/MM 'à' HH:mm");
                                    date.setText(dateFormat_oneday.format(e.getDateStart()));
                                } else {
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM");
                                    String dateStart = dateFormat.format(e.getDateStart());
                                    String dateEnd = dateFormat.format(e.getDateEnd());
                                    date.setText("Du " + dateStart + " au " + dateEnd);
                                }

                                int id = MemoryStorage.findAsso(e.getAssociation());
                                if (id == -1) {
                                    HttpGet asso = new HttpGet(new AsyncResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            try {
                                                JSONObject json = new JSONObject(output);

                                                Association a = new Association(json);
                                                MemoryStorage.addAsso(a); // add in memory

                                                association.setText("@" + a.getName());

                                            } catch (JSONException e) {
                                                HttpGet.credentials.getSessionToken();
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    asso.execute(HttpGet.ROOTASSOCIATION + "/" + e.getAssociation() + "?token=" + HttpGet.credentials.getSessionToken());
                                } else {
                                    association.setText("@" + MemoryStorage.getAll_assos().get(id).getName());
                                }

                                int nb_participants = e.getParticipants().size();
                                if (nb_participants <= 1)
                                    participants.setText(Integer.toString(nb_participants) + " participant");
                                else
                                    participants.setText(Integer.toString(nb_participants) + " participants");
                            }
                        }

                        // Hide/show title "Aujourd'hui"
                        LinearLayout eventsToday = (LinearLayout) view.findViewById(R.id.today);
                        if (nb_events_atm == 0)
                            eventsToday.setVisibility(View.GONE);
                        else
                            eventsToday.setVisibility(View.VISIBLE);

                        // Hide/show title "7jours"
                        LinearLayout eventsWeek = (LinearLayout) view.findViewById(R.id.week);
                        if (nb_events_7days == 0)
                            eventsWeek.setVisibility(View.GONE);
                        else
                            eventsWeek.setVisibility(View.VISIBLE);

                        // Hide/show title "30jours"
                        LinearLayout eventsMonth = (LinearLayout) view.findViewById(R.id.month);
                        if (nb_events_30days == 0)
                            eventsMonth.setVisibility(View.GONE);
                        else
                            eventsMonth.setVisibility(View.VISIBLE);

                        // Hide/show title "à venir"
                        LinearLayout eventsSoon = (LinearLayout) view.findViewById(R.id.soon);
                        if (nb_events_soon == 0)
                            eventsSoon.setVisibility(View.GONE);
                        else
                            eventsSoon.setVisibility(View.VISIBLE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{
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

        events.execute(HttpGet.ROOTEVENT + "?token=" + HttpGet.credentials.getSessionToken());
    }

}
