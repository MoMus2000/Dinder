package com.example.imagesasync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailScreen extends AppCompatActivity {
    private AnyChartView anyChartView;
    String[] Features = {"Likes","Dislikes"};
    private FirebaseFirestore mAuth;
    int[] vals = {100,200};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);
        mAuth = FirebaseFirestore.getInstance();
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(1)
                .playstoreUrl("https://play.google.com/store/apps/details?id=com.example.imagesasync")
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Map<String, String> data = new HashMap<>();
                        data.put(System.currentTimeMillis()+"",feedback);
                        mAuth.collection("NegativeReviews").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(),"Thankyou for your kind feedback",Toast.LENGTH_LONG);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG);
                            }
                        });
                    }
                }).build();
        ratingDialog.show();
        Bundle intent = getIntent().getExtras();
        String like = intent.getString("Likes");
        String dislike = intent.getString("Dislikes");
        int likes = Integer.parseInt(like);
        int dislikes = Integer.parseInt(dislike);
        System.out.println("likes : "+likes);
        System.out.println("dislikes : "+dislikes);
        vals[0] = likes;
        vals[1] = dislikes;
        anyChartView = findViewById(R.id.chart);
        setUpChart();
    }

    public void setUpChart(){
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for(int i=0;i<vals.length;i++){
            dataEntries.add(new ValueDataEntry(Features[i],vals[i]));
        }
        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }

}