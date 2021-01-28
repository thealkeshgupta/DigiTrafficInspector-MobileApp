package com.example.digitrafficinspectorv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ComplaintHistoryPage2 extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView firestore_list;
    private FirestoreRecyclerAdapter adapter;
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db;
    String collectionName ="";
    TextView textHeading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_history_page2);

        db = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firestore_list = findViewById(R.id.firestore_list);
        textHeading = findViewById(R.id.textHeading);
        Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
        textHeading.setTypeface(face);

        collectionName = getIntent().getStringExtra("collectionName");

        Query query = firebaseFirestore.collection(collectionName).whereEqualTo("UID",currentuser).orderBy("DATE_TIME_STAMP", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HistoryModel2> options = new FirestoreRecyclerOptions.Builder<HistoryModel2>()
                .setQuery(query, HistoryModel2.class)
                .build();
      adapter = new FirestoreRecyclerAdapter<HistoryModel2, HistoryViewHolder>(options) {
         @NonNull
         @Override
         public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
             return new HistoryViewHolder(view);
         }

         @Override
         protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull HistoryModel2 model) {
             holder.vehicleNumber.setText("Vehicle No. : "+model.getVEHICLE_NUMBER());
             holder.dtstamp.setText("Case ID: "+model.getDATE_TIME_STAMP());
             holder.score.setText(model.getSCORE());

         }
     };
           firestore_list.setHasFixedSize(true);
           firestore_list.setLayoutManager(new LinearLayoutManager(this));
           firestore_list.setAdapter(adapter);
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView vehicleNumber;
        private TextView dtstamp;
        private TextView score;
        private TextView scoreLabel;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            vehicleNumber = itemView.findViewById(R.id.vehicleNumber);
            dtstamp = itemView.findViewById(R.id.dtstamp);
            score = itemView.findViewById(R.id.score);
            scoreLabel = itemView.findViewById(R.id.scoreLabel);
            Typeface face= Typeface.createFromAsset(getAssets(), "font/arkhipfont.ttf");
            vehicleNumber.setTypeface(face);
            dtstamp.setTypeface(face);
            score.setTypeface(face);
            scoreLabel.setTypeface(face);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        Toast.makeText(ComplaintHistoryPage2.this, "Your History", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

