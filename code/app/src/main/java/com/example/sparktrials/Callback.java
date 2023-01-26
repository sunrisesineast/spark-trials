package com.example.sparktrials;

import com.google.firebase.firestore.DocumentSnapshot;

public interface Callback {
    void onCallback(DocumentSnapshot document);
}
