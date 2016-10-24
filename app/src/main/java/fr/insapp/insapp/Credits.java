package fr.insapp.insapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Credentials;

public class Credits extends Activity {
/*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("id", HttpGet.credentials.getId());
        outState.putString("userID", HttpGet.credentials.getUserID());
        outState.putString("username", HttpGet.credentials.getUsername());
        outState.putString("sessionToken", HttpGet.credentials.getSessionToken());
        outState.putString("info_user", HttpGet.info_user);
        super.onSaveInstanceState(outState);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            /*String id = savedInstanceState.getString("id");
            String userID = savedInstanceState.getString("userID");
            String username = savedInstanceState.getString("username");
            String sessionToken = savedInstanceState.getString("sessionToken");
            HttpGet.credentials = new Credentials(id, userID, username, sessionToken);*/
            String info_user = savedInstanceState.getString("info_user");
            if(!info_user.isEmpty())
                HttpGet.info_user = info_user;
        }
        setContentView(R.layout.activity_credits);

        ImageView cross = (ImageView) findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Credits.this.finish();
            }
        });

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("https://insapp.fr/api/v1/credit");
    }
}
