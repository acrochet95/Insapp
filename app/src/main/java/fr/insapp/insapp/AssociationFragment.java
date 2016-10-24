package fr.insapp.insapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Association;
import fr.insapp.insapp.utility.ImageLoaderCache;
import fr.insapp.insapp.utility.MemoryStorage;
import fr.insapp.insapp.utility.Utils;


public class AssociationFragment extends FragmentRefreshable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout assoLayout = null;
    private SearchView searchView = null;

    private ImageLoaderCache imageLoader = null;

    private RelativeLayout refreshRl = null;

    private static int config = -1;
    public final int NB_ASSO_PER_LINE = 3;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssociationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssociationFragment newInstance(String param1, String param2) {
        AssociationFragment fragment = new AssociationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AssociationFragment() {
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
        if(assoLayout != null) {
            refresh(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_association, container, false);

        imageLoader = new ImageLoaderCache(getContext());

        searchView = (SearchView) view.findViewById(R.id.searchView);

        assoLayout = (LinearLayout) view.findViewById(R.id.assoLayout); // layout principal

        refreshRl = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        //Applies white color on searchview text
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(getResources().getColor(R.color.search_text));

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.TRANSPARENT);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showAssos(newText);
                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        //refresh(false);

        return view;
    }

    @Override
    public boolean isInitialised() {
        return init;
    }

    @Override
    public void refresh(final boolean refreshFromSwipe) {

        init = true;

        if(!refreshFromSwipe)
            view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        assoLayout = (LinearLayout) view.findViewById(R.id.assoLayout);
        assoLayout.removeAllViews();

        HttpGet assos = new HttpGet(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if(!output.isEmpty()) {
                    try {
                        if(view.findViewById(R.id.loadingPanel).getVisibility() == View.VISIBLE)
                            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        JSONArray jsonArray = new JSONArray(output);

                        for(int i=0; i<jsonArray.length(); i++){
                            Association aTmp = new Association(jsonArray.getJSONObject(i));
                            if(!aTmp.getProfilPicture().equals(""))
                                MemoryStorage.addAsso(aTmp);
                        }

                        // for lines
                        int nb_line = (int)Math.ceil((double)MemoryStorage.all_assos.size()/(double)NB_ASSO_PER_LINE);
                        for(int i=0; i<nb_line; i++){

                            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View childLayout = layoutInflater.inflate(R.layout.assos_line, (ViewGroup) view.findViewById(R.id.child_id), false);
                            assoLayout.addView(childLayout, i);

                            // for column
                            for(int j=0; j<3 && i*NB_ASSO_PER_LINE+j<MemoryStorage.all_assos.size(); j++){

                                final Association a = MemoryStorage.all_assos.get(i*NB_ASSO_PER_LINE + j);

                                int id = 0, id_text = 0;
                                if(j==0) {
                                    id = R.id.img1;
                                    id_text = R.id.asso1;
                                }
                                else if(j==1) {
                                    id = R.id.img2;
                                    id_text = R.id.asso2;
                                }
                                else {
                                    id = R.id.img3;
                                    id_text = R.id.asso3;
                                }

                                TextView text = (TextView) childLayout.findViewById(id_text);
                                final ImageView ib = (ImageView) childLayout.findViewById(id);

                                text.setText("@" + a.getName());
                                imageLoader.DisplayImage(HttpGet.IMAGEURL + a.getProfilPicture(), ib);

                                ib.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent activity = new Intent(getActivity(), AssociationProfil.class);
                                        activity.putExtra("asso", a);
                                        startActivity(activity);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                    }
                                });
                            }
                        }

                    } catch (JSONException e) {
                    }
                }
                else{
                    if(Utils.isNetworkAvailable(getActivity())) {
                        Intent i = new Intent(getActivity(), Login.class);
                        startActivity(i);
                    }
                    else
                        Toast.makeText(getContext(), "Problème avec internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        assos.execute(HttpGet.ROOTASSOCIATION + "?token=" + HttpGet.credentials.getSessionToken());
    }

    public void showAssos(String textAsso) {
        assoLayout.removeAllViews();

        List<Association> assos = new ArrayList<Association>();
        if(textAsso.isEmpty()){
            assos = MemoryStorage.all_assos;
        }
        else {
            for (int i = 0; i < MemoryStorage.all_assos.size(); i++) {
                String name = MemoryStorage.all_assos.get(i).getName().toLowerCase();
                if (name.contains(textAsso.toLowerCase()))
                    assos.add(MemoryStorage.all_assos.get(i));
            }
        }

        // for lines
        int nb_line = (int)Math.ceil((double)assos.size()/(double)NB_ASSO_PER_LINE);
        for(int i=0; i<nb_line; i++){

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View childLayout = layoutInflater.inflate(R.layout.assos_line, (ViewGroup) view.findViewById(R.id.child_id), false);
            assoLayout.addView(childLayout, i);

            // for column
            for(int j=0; j<3 && i*NB_ASSO_PER_LINE+j<assos.size(); j++){

                final Association a = assos.get(i*NB_ASSO_PER_LINE + j);

                int id = 0, id_text = 0;
                if(j==0) {
                    id = R.id.img1;
                    id_text = R.id.asso1;
                }
                else if(j==1) {
                    id = R.id.img2;
                    id_text = R.id.asso2;
                }
                else {
                    id = R.id.img3;
                    id_text = R.id.asso3;
                }

                TextView text = (TextView) childLayout.findViewById(id_text);
                final ImageView ib = (ImageView) childLayout.findViewById(id);

                text.setText("@" + a.getName());
                imageLoader.DisplayImage(HttpGet.IMAGEURL + a.getProfilPicture(), ib);

                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent activity = new Intent(getActivity(), AssociationProfil.class);
                        activity.putExtra("asso", a);
                        startActivity(activity);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }
                });
            }
        }
    }

}
