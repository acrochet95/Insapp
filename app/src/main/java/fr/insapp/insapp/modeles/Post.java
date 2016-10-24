package fr.insapp.insapp.modeles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import fr.insapp.insapp.utility.Operation;

/**
 * Created by Antoine on 19/09/2016.
 *
 * type Post struct {
     ID bson.ObjectId `bson:"_id,omitempty"`
     Title string `json:"title"`
     Association bson.ObjectId `json:"association"`
     Description string `json:"description"`
     Event bson.ObjectId `json:"event"`
     Date time.Time `json:"date"`
     Likes []bson.ObjectId `json:"likes"`
     Comments Comments `json:"comments"`
     PhotoURL string `json:"photourl"`
 }
 */
public class Post {

    private String id, title;
    private String association, description;
    private Date date;
    private ArrayList<String> likes;
    private ArrayList<Comment> comments;
    private String image;
    private int width;
    private int height;

    public Post(String id, String title, String association, String description, Date date, ArrayList<String> likes, ArrayList<Comment> comments, String photoURL, int width, int height) {
        this.id = id;
        this.title = title;
        this.association = association;
        this.description = description;
        this.date = date;
        this.likes = likes;
        this.comments = comments;
        this.image = photoURL;
    }

    public Post(JSONObject json) throws JSONException {
        this.id = json.getString("ID");
        this.title = json.getString("title");
        this.association = json.getString("association");
        this.description = json.getString("description");
        this.date = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", json.getString("date"), true);
        this.likes = new ArrayList<String>();

        JSONArray jsonarray = json.optJSONArray("likes");
        if (jsonarray != null) {
            for (int i = 0; i < jsonarray.length(); i++)
                likes.add(jsonarray.getString(i));
        }

        this.comments = new ArrayList<Comment>();

        JSONArray jsonarray2 = json.optJSONArray("comments");
        if (jsonarray2 != null) {
            for (int i = 0; i < jsonarray2.length(); i++)
                comments.add(new Comment(jsonarray2.getJSONObject(i)));
        }

        this.image = json.getString("image");
        this.width = json.getJSONObject("imageSize").getInt("width");
        this.height = json.getJSONObject("imageSize").getInt("width");
    }

    public boolean postLikedBy(String user){
        for(String idUser : likes){
            if(idUser.equals(user))
                return true;
        }

        return false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAssociation() {
        return association;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPhotoURL() {
        return image;
    }

}
