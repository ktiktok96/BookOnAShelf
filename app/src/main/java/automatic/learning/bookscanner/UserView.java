package automatic.learning.bookscanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserView extends AppCompatActivity {
    private ArrayList<String> keys = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);
        list = (ListView) findViewById(R.id.myList);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //myList.clear();
                Log.d("user", dataSnapshot.toString());
                keys.clear();
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    Log.d("number", datas.getKey());
                    keys.add(datas.getKey());

                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("debug", "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addValueEventListener(postListener);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listitem, R.id.textview, keys);

        // Here, you set the data in your ListView
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String selected = ((TextView) view.findViewById(R.id.textview)).getText().toString();
                Intent myIntent = new Intent(getApplicationContext(),userBooks.class);
                myIntent.putExtra("user", selected);
                startActivity(myIntent);

            }
        });

    }
}