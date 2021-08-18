package automatic.learning.bookscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    //properties
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    private DatabaseReference mDatabase;
    private ArrayList<Book> myList = new ArrayList<Book>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth inst = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(inst.getCurrentUser().getEmail().replace(".",""));
        myList.clear();
        Button userButton = findViewById(R.id.userButton);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplication(), UserView.class);
                startActivity(myIntent);
            }
        });
        MyBookAdapter adapter = new MyBookAdapter(myList, MainActivity.this);
        // below line is use to add linear layout
        // manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.idRVBooks);


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myList.clear();

                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    Log.d("number", String.valueOf(Double.parseDouble(datas.child("page").getValue().toString())));

                    Book myBook = new Book(Float.parseFloat(datas.child("barcode_id").getValue().toString()),
                            Float.parseFloat(datas.child("isbn_13").getValue().toString()),
                            datas.child("isbn_10").getValue().toString(),
                            datas.child("title").getValue().toString(),
                            datas.child("author").getValue().toString(),
                            datas.child("publisher").getValue().toString(),
                            datas.child("publishing_date").getValue().toString(),
                            Double.parseDouble(datas.child("page").getValue().toString()),
                            datas.child("thumbnail").getValue().toString(),
                            datas.child("description").getValue().toString(),
                            datas.child("buyLink").getValue().toString());
                    myList.add(myBook);
                    Log.d("debug", datas.child("title").getValue().toString());

                }
                // sort the array by title
                Collections.sort(myList);
               adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("debug", "loadPost:onCancelled", databaseError.toException());
            }
        };

        reference.addValueEventListener(postListener);






        // in below line we are setting layout manager and
        // adapter to our recycler view.
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);

        Button enableCamera = findViewById(R.id.enableCamera);
        Context ctx=this;
        enableCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCameraPermission()) {
                    Intent intent = new Intent(ctx, CameraActivity.class);
                    startActivity(intent);
                } else {
                    requestPermission();
                    Toast.makeText(ctx, "Please grant camera permission" , Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    //methods
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }




}