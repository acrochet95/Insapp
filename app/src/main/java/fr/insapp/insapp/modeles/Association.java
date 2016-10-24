package fr.insapp.insapp.modeles;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Antoine on 25/02/2016.
 *

 type Association struct {
     ID          bson.ObjectId   `bson:"_id,omitempty"`
     Name        string          `json:"name"`
     Email       string          `json:"email"`
     Description string          `json:"description"`
     Events      []bson.ObjectId `json:"events"`
     Posts       []bson.ObjectId `json:"posts"`
     Palette			[][]int					`json:"palette"`
     SelectedColor int						`json:"selectedcolor"`
     Profile    	string          `json:"profile"`
     Cover	    	string          `json:"cover"`
     BgColor     string          `json:"bgcolor"`
     FgColor string `json:"fgcolor"`
 }

 */
public class Association implements Parcelable {

    private String id;
    private String name, email, description;
    private ArrayList<String> events;
    private ArrayList<String> posts;
    //private int[][] palette;
    //private int selectedColor;
    private String profilPicture;
    private String cover;
    private String bgColor;
    private String fgColor;

    public static final Parcelable.Creator<Association> CREATOR = new Parcelable.Creator<Association>() {

        @Override
        public Association createFromParcel(Parcel source) {
            return new Association(source);
        }

        @Override
        public Association[] newArray(int size) {
            return new Association[size];
        }

    };

    public Association(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.description = in.readString();

        this.events = new ArrayList<String>();
        int nb_events = in.readInt();
        if(nb_events > 0) {
            in.readStringList(this.events);
        }

        this.posts = new ArrayList<String>();
        int nb_posts = in.readInt();
        if(nb_posts > 0) {
            in.readStringList(this.posts);
        }
/*
        int nb_palette = in.readInt();
        if(nb_palette > 0) {
            this.palette = new int[nb_palette][3];
            for (int i = 0; i < palette.length; i++)
                in.readIntArray(palette[i]);
        }
*/
        //this.selectedColor = in.readInt();
        this.profilPicture = in.readString();
        this.cover = in.readString();
        this.bgColor = in.readString();
        this.fgColor = in.readString();
    }

    public Association(String id, String name, String email, String description, ArrayList<String> events, ArrayList<String> posts, String profilPicture, String cover, String bgColor, String fgColor) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.events = events;
        this.posts = posts;
        /*if(palette.length != 0){
            this.palette = new int[palette.length][3];
            for(int i=0;i<palette.length;i++)
                for(int j=0;j<3;j++)
                    this.palette[i][j] = palette[i][j];
        }

        this.selectedColor = selectedColor;*/
        this.profilPicture = profilPicture;
        this.cover = cover;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }

    public Association(JSONObject json) throws JSONException
    {
        this.id = json.getString("ID");
        this.name = json.getString("name");
        this.email = json.getString("email");
        this.description = json.getString("description");

        this.events = new ArrayList<String>();
        JSONArray jsonarray = json.optJSONArray("events");
        if(jsonarray != null) {
            for (int i = 0; i < jsonarray.length(); i++) {
                events.add(jsonarray.getString(i));
            }
        }

        this.posts = new ArrayList<String>();
        JSONArray jsonarray2 = json.optJSONArray("posts");
        if(jsonarray2 != null) {
            for (int i = 0; i < jsonarray2.length(); i++) {
                posts.add(jsonarray2.getString(i));
            }
        }
/*
        JSONArray jsonarray3 = json.optJSONArray("palette");
        if(jsonarray3 != null) {
            this.palette = new int[jsonarray3.length()][3];
            for (int i = 0; i < jsonarray3.length(); i++) {
                JSONArray ja = jsonarray3.getJSONArray(i);
                for(int j=0;j<ja.length();j++)
                    this.palette[i][j] = ja.getInt(j);
            }
        }

        this.selectedColor = json.getInt("selectedcolor");*/
        this.profilPicture = json.getString("profile");
        this.cover = json.getString("cover");
        this.bgColor = json.getString("bgcolor");
        this.fgColor = json.getString("fgcolor");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }
/*
    public int[][] getPalette() {
        return palette;
    }

    public int getSelectedColor() {
        return selectedColor;
    }
*/
    public String getProfilPicture() {
        return profilPicture;
    }

    public String getCover() {
        return cover;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getFgColor() {
        return fgColor;
    }

    public int describeContents() {
        return 0; //On renvoie 0, car notre classe ne contient pas de FileDescriptor
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(description);
        dest.writeInt(events.size());
        if(events.size() > 0)
            dest.writeStringList(events);
        dest.writeInt(posts.size());
        if(posts.size() > 0)
            dest.writeStringList(posts);
/*
        dest.writeInt(palette.length);
        for(int i=0;i<palette.length;i++)
            dest.writeIntArray(palette[i]);

        dest.writeInt(selectedColor);*/
        dest.writeString(profilPicture);
        dest.writeString(cover);
        dest.writeString(bgColor);
        dest.writeString(fgColor);
    }
}
