package com.beyondthecode.shared.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

fun FirebaseFirestore.document2019(): DocumentReference =
    //this is a prefix for Firestore document for 2019
    collection("google_io_events").document("2019")