package com.example.imagesasync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
Updates to work on
Detail Screen ----> Ad Free version toggling
Likes and Dislikes stats for now...........
Mentioning Dog Api For the free pics
 */


public class MainActivity extends AppCompatActivity {
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private int likes =0;
    private int dislikes =0;
    private ItemModel itemModel = new ItemModel("","","","");

    private static final String TAG = "MainActivity";
    private String[] links = {"https://images.dog.ceo/breeds/eskimo/n02109961_135.jpg","https://images.dog.ceo/breeds/terrier-scottish/n02097298_998.jpg",
            "https://images.dog.ceo/breeds/cotondetulear/IMAG1063.jpg","https://images.dog.ceo/breeds/bulldog-english/jager-1.jpg","https://images.dog.ceo/breeds/terrier-norfolk/n02094114_2008.jpg",
    "https://images.dog.ceo/breeds/spaniel-brittany/n02101388_183.jpg","https://images.dog.ceo/breeds/newfoundland/n02111277_2885.jpg","https://images.dog.ceo/breeds/poodle-toy/n021136_4087.jpg"};
    private List<String> strList = new ArrayList<String>(Arrays.asList(links));
    FirebaseAnalytics firebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Collections.shuffle(strList);
        links = strList.toArray(new String[strList.size()]);
        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        DialogFactory dialogFragment = new DialogFactory();
        dialogFragment.show(getSupportFragmentManager(),"Welcome Dialog");
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    try{
//                        Toast.makeText(MainActivity.this, "Direction Right", Toast.LENGTH_SHORT).show();
                        likes++;
                        System.out.println(likes);
                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
//

                }
                if (direction == Direction.Top){
//                    Toast.makeText(MainActivity.this, "Direction Top", Toast.LENGTH_SHORT).show();
                    System.out.println(links[0] + "TOP");
                }
                if (direction == Direction.Left){
                    try{
//                        Toast.makeText(MainActivity.this, "Direction l", Toast.LENGTH_SHORT).show();
                        dislikes++;
                        System.out.println(dislikes);
                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                }
                if (direction == Direction.Bottom){
//                    Toast.makeText(MainActivity.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
                }

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
                System.out.println(links[0] +"REQIUND ");
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
                apiCall();
                apiCall2();
                apiCall3();
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
                apiCall();
                apiCall2();
                apiCall3();
            }
        });
        if(!isUserOnline(this)){
            toast("PLEASE CONNECT TO THE INTERNET TO PROCEED");
        }
        apiCall();
        apiCall2();
        apiCall3();


        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(1);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }
    private void paginate() {
        List<ItemModel> old = adapter.getItems();
        List<ItemModel> baru = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, baru);
        DiffUtil.DiffResult hasil = DiffUtil.calculateDiff(callback);
        adapter.setItems(baru);
        hasil.dispatchUpdatesTo(adapter);
    }


    private List<ItemModel> addList() {
        List<ItemModel> items = new ArrayList<>();
        String firstPart = links[0].substring(30);
        int index2  = links[0].substring(30).indexOf("/");
        String secondPart = firstPart.substring(index2);
        System.out.println(firstPart + " ----> First part");
        System.out.println(index2 + " ---->Index to start");
        System.out.println(secondPart+" ----->SECOND PART");
        items.add(new ItemModel("Breed", links[0].substring(30).substring(0,index2), "", links[0]));
        items.add(new ItemModel("Breed", links[1].substring(30).substring(0,index2), "", links[1]));
        items.add(new ItemModel("Breed", links[2].substring(30).substring(0,index2), "", links[2]));
        items.add(new ItemModel("Breed", links[3].substring(30).substring(0,index2), "", links[3]));
        items.add(new ItemModel("Breed", links[4].substring(30).substring(0,index2), "", links[4]));
        items.add(new ItemModel("Breed", links[5].substring(30).substring(0,index2), "", links[5]));
        items.add(new ItemModel("Breed", links[6].substring(30).substring(0,index2), "", links[6]));
        items.add(new ItemModel("Breed", links[7].substring(30).substring(0,index2), "", links[7]));
        return items;
    }

    @Override
    public void onBackPressed(){
        Boolean flag =false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Leaving so soon? \n Check out your stats by clicking below!")
                .setPositiveButton("Gotcha Boss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(),DetailScreen.class);
                        intent.putExtra("Likes",likes+"");
                        intent.putExtra("Dislikes",dislikes+"");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("I wanna Leave",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       finish();
                    }
                });
        ;
        builder.create();
        builder.show();
    }
    public void apiCall(){
        String url = "https://dog.ceo/api/breeds/image/random";
        System.out.println("in the method");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jo = response.getString("message");
                    System.out.println("This is the message" +jo);
                    links[0] = jo;
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("THIS IS NOT THE MESSAGE");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
    public void apiCall2(){
        String url = "https://dog.ceo/api/breeds/image/random";
        System.out.println("in the method");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jo = response.getString("message");
                    System.out.println("This is the message" +jo);
                    links[1] = jo;
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("THIS IS NOT THE MESSAGE");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
    public void apiCall3(){
        String url = "https://dog.ceo/api/breeds/image/random";
        System.out.println("in the method");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jo = response.getString("message");
                    System.out.println("This is the message" +jo);
                    links[2] = jo;
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("THIS IS NOT THE MESSAGE");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
    public void apiCall4(){
        String url = "https://dog.ceo/api/breeds/image/random";
        System.out.println("in the method");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jo = response.getString("message");
                    System.out.println("This is the message" +jo);
                    links[3] = jo;
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("THIS IS NOT THE MESSAGE");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
    public void apiCall5(){
        String url = "https://dog.ceo/api/breeds/image/random";
        System.out.println("in the method");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jo = response.getString("message");
                    System.out.println("This is the message" +jo);
                    links[5] = jo;
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("THIS IS NOT THE MESSAGE");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
    public static boolean isUserOnline(Context context) {
        try {
            ConnectivityManager nConManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (nConManager != null) {
                NetworkInfo nNetworkinfo = nConManager.getActiveNetworkInfo();

                if (nNetworkinfo != null) {
                    return nNetworkinfo.isConnected();
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}