package com.example.my.todoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1);

        listView.setAdapter(adapter);

        Firebase.setAndroidContext(this);

        new Firebase("https://todoapp-ab054.firebaseio.com/todoItems")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        adapter.add((String)dataSnapshot.child("text").getValue());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        adapter.remove((String)dataSnapshot.child("text").getValue());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
                });

        final EditText text = (EditText) findViewById(R.id.todoText);
        final Button button = (Button) findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener(){
            public void  onClick(View v){
                new Firebase("https://todoapp-ab054.firebaseio.com/todoItems")
                        .push()
                        .child("text")
                        .setValue(text.getText().toString());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            //@Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                new Firebase("https://todoapp-ab054.firebaseio.com/todoItems")
                        .orderByChild("text")
                        .equalTo((String) listView.getItemAtPosition(position))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot){
                                if (dataSnapshot.hasChildren()){
                                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                    firstChild.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
            }
        });
    }
}
