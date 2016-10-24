package fr.insapp.insapp.modeles;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import fr.insapp.insapp.utility.Operation;

/**
 * Created by Antoine on 10/10/2016.
 *
 * type Notification struct {
 ID          bson.ObjectId   `bson:"_id,omitempty"`
 Sender      bson.ObjectId   `json:"sender"`
 Receiver    bson.ObjectId   `json:"receiver"`
 Content			bson.ObjectId		`json:"content"`
 Comment			Comment					`json:"comment,omitempty" bson:",omitempty"`
 Message			string					`json:"message"`
 Seen				bool						`json:"seen"`
 Date				time.Time				`json:"date"`
 Type				string					`json:"type"`
 }
 */
public class Notification implements Parcelable {
    private String id;
    private String sender, receiver;
    private String content;
    //private Comment comment;
    private String commentID;
    private String message;
    private boolean seen;
    private Date date;
    private String type;

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    protected Notification(Parcel in) {
        this.id = in.readString();
        this.sender = in.readString();
        this.receiver = in.readString();
        this.content = in.readString();
        this.commentID = in.readString();
/*
        System.out.println("id : " + id);
        System.out.println("sender : " + sender);
        System.out.println("rec : " + receiver);
        System.out.println("cont : " + content);
        //System.out.println("idcom : " + commentID);
*/

        this.message = in.readString();
        this.type = in.readString();
        this.seen = in.readByte() != 0;
        //this.date = new Date(in.readLong());
    }

    public Notification(JSONObject json) throws JSONException {
        //System.out.println(json.toString());
        this.id = json.getString("ID");
        this.sender = json.getString("sender");
        this.receiver = json.getString("receiver");
        this.content = json.getString("content");
        //this.comment = new Comment(json.getJSONObject("comment"));
        this.commentID = json.getJSONObject("comment").getString("ID");

        //System.out.println("ON RECOIT : " + comment.getContent());

        this.message = json.getString("message");
        this.seen = json.getBoolean("seen");
        // System.out.println(in.readString());//this.date = new Date(in.readLong());
        this.date = Operation.stringToDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", json.getString("date"), true);
        this.type = json.getString("type");
    }


    public Notification(Bundle extras) {

        this.id = extras.getString("ID");
        this.sender = extras.getString("sender");
        this.receiver = extras.getString("receiver");
        this.content = extras.getString("content");
        /*try {
            this.comment = new Comment(new JSONObject(extras.getString("comment")));//Comment.class.getClassLoader());
        } catch (JSONException e) {
        }
*/
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(extras.getString("comment"));
            this.commentID = jsonObject.getString("ID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println("COMMENTAIRE : " + extras.getString("comment"));
        //System.out.println("SON ID : " + commentID);

        this.message = extras.getString("message");
        this.seen = Boolean.getBoolean(extras.getString("seen"));

        this.date = new Date(extras.getLong("google.sent_time"));

        this.type = extras.getString("type");
    }

    /*public Notification(String sender, String receiver, String content, String commentID, String message, boolean seen, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.commentID = commentID;
        this.message = message;
        this.seen = seen;
        this.type = type;
    }*/

    public Notification() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id.toString());
        dest.writeString(sender.toString());
        dest.writeString(receiver.toString());
        dest.writeString(content.toString());

        dest.writeString(commentID);

        dest.writeString(message);
        dest.writeString(type);
        dest.writeByte((byte) (seen ? 1 : 0));
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }


    public String getContent() {
        return content;
    }
/*
    public void setContent(String content) {
        this.content = content;
    }*/

    public String getCommentID() {
        return commentID;
    }
/*
    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
*/
    public String getMessage() {
        return message;
    }

    public boolean isSeen() {
        return seen;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

}
