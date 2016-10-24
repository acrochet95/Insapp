package fr.insapp.insapp.utility;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.modeles.Association;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.modeles.Post;

/**
 * Created by Antoine on 28/09/2016.
 */
public class MemoryStorage {

    public static ArrayList<Post> all_posts = new ArrayList<Post>();
    public static ArrayList<Association> all_assos = new ArrayList<Association>();
    public static ArrayList<Event> all_events = new ArrayList<Event>();

    public static int findPost(String id){
        for(Post p : all_posts){
            if(p.getId().equals(id))
                return all_posts.lastIndexOf(p);
        }

        return -1;
    }

    public static Post addPost(Post post){
        int id = findPost(post.getId());
        if(id == -1) {
            all_posts.add(post);
            return post;
        }

        return all_posts.get(id);
    }

    public static int findAsso(String id){
        for(Association a : all_assos){
            if(a.getId().equals(id))
                return all_assos.lastIndexOf(a);
        }

        return -1;
    }

    public static Association addAsso(Association asso){
        int id = findAsso(asso.getId());
        if(id == -1) {
            all_assos.add(asso);
            return asso;
        }

        return all_assos.get(id);
    }

    public static int findEvent(String id){
        for(Event e : all_events){
            if(e.getId().equals(id))
                return all_events.lastIndexOf(e);
        }

        return -1;
    }

    public static Event addEvent(Event event){
        int id = findEvent(event.getId());
        if(id == -1) {
            all_events.add(event);
            return event;
        }
        return all_events.get(id);
    }

    public static List<Post> getAll_posts() {
        return all_posts;
    }

    public static List<Association> getAll_assos() {
        return all_assos;
    }

    public static List<Event> getAll_events() {
        return all_events;
    }
}
