package com.enalymounioz.albedo.firebase

import android.util.Log
import com.enalymounioz.albedo.activities.SignInActivity
import com.enalymounioz.albedo.activities.SignUpActivity
import com.enalymounioz.albedo.models.User
import com.enalymounioz.albedo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error")
            }
    }


    fun signInUser(activity: SignInActivity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null)
                    activity.signInSuccess(loggedInUser)
            }.addOnFailureListener { e ->
                Log.e("SignInUser", "Error")
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID =""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}