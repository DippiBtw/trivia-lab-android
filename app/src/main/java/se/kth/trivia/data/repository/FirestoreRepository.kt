package se.kth.trivia.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Player(val name: String, val rank: Int, val points: Int)

class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchTopUsers(): List<Pair<String, Int>> {
        val topUsers = mutableListOf<Pair<String, Int>>()
        val querySnapshot = db.collection("rankings")
            .orderBy("highscore", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()

        for (document in querySnapshot.documents) {
            val username = document.getString("username") ?: ""
            val highscore = document.getLong("highscore")?.toInt() ?: 0
            topUsers.add(Pair(username, highscore))
        }

        return topUsers
    }

    suspend fun fetchHighscore(): Pair<String, Int>? {
        val user = auth.currentUser

        if (user != null) {
            try {
                val userDoc = db.collection("users")
                    .document(user.uid)
                    .get().await()

                val username = userDoc.getString("username") ?: "Unknown"
                val highscore = userDoc.getLong("highscore")?.toInt() ?: 0

                return Pair(username, highscore)
            } catch (e: Exception) {
                Log.d("FirestoreRepository", "Error fetching highscore or ranking", e)
            }
        }

        return null
    }

    suspend fun saveHighscore(highscore: Int) {
        val user = auth.currentUser

        if (user != null) {
            val userRef = db.collection("users").document(user.uid)

            try {
                val userDoc = userRef.get().await()
                val currentHighscore = userDoc.getLong("highscore")?.toInt() ?: 0

                if (highscore > currentHighscore) {
                    // Step 1: Update the user's highscore in their document
                    userRef.update("highscore", highscore)
                        .addOnSuccessListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                updateRanking(user.uid, highscore)
                            }
                            Log.d("FirestoreRepository", "Updated highscore")
                        }
                        .addOnFailureListener { e ->
                            Log.d("FirestoreRepository", "Error updating highscore", e)
                        }
                }

            } catch (e: Exception) {
                Log.d("FirestoreRepository", "Error updating highscore", e)
            }
        }
    }

    suspend fun updateRanking(userId: String, highscore: Int) {
        // Fetch the top 20 rankings from Firestore
        val querySnapshot = db.collection("rankings")
            .orderBy("highscore", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .get().await()

        var isUserInTop20 = false
        var lowestRankUserDoc: DocumentSnapshot? = null
        var userDocInTop20: DocumentSnapshot? = null

        val userDoc = db.collection("users").document(userId).get().await()
        val username = userDoc.getString("username") ?: "Unknown"

        for (document in querySnapshot.documents) {
            val currentHighscore = document.getLong("highscore")?.toInt() ?: 0
            val docUserId = document.getString("userId") ?: ""
            // Check if this user is already in the top 20 or if they should be added
            if (docUserId == userId) {
                isUserInTop20 = true
                userDocInTop20 = document
            }
            // Track the document with the lowest score (last ranked)
            if (lowestRankUserDoc == null || currentHighscore <= (lowestRankUserDoc?.getLong("highscore")
                    ?.toInt() ?: 0)
            ) {
                lowestRankUserDoc = document
            }
        }

        if (isUserInTop20) {
            // If the user is in the top 20 and beat their old highscore, update their ranking
            val oldHighscore = userDocInTop20?.getLong("highscore")?.toInt() ?: 0
            if (highscore > oldHighscore) {
                userDocInTop20?.reference?.update("highscore", highscore)
                    ?.addOnSuccessListener {
                        Log.d("FirestoreRepository", "User's highscore updated in rankings")
                    }
                    ?.addOnFailureListener { e ->
                        Log.d("FirestoreRepository", "Error updating highscore in rankings", e)
                    }
            }
            Log.d("FirestoreRepository", "User is already in the top 20.")
            return
        }

        // Add user to the rankings if their highscore is in the top 20
        if (querySnapshot.size() < 20 || highscore > (lowestRankUserDoc?.getLong("highscore")
                ?.toInt() ?: 0)
        ) {
            // Step 3: Add the user to the rankings collection
            db.collection("rankings")
                .add(mapOf("userId" to userId, "highscore" to highscore, "username" to username))
                .addOnSuccessListener {
                    Log.d("FirestoreRepository", "User added to top 20 rankings.")
                }
                .addOnFailureListener { e ->
                    Log.d("FirestoreRepository", "Error adding user to rankings", e)
                }

            // Step 4: If the rankings exceed 20, remove the user with the lowest score
            if (querySnapshot.size() >= 20) {
                lowestRankUserDoc?.reference?.delete()
                    ?.addOnSuccessListener {
                        Log.d("FirestoreRepository", "Removed lowest rank user from rankings.")
                    }
                    ?.addOnFailureListener { e ->
                        Log.d("FirestoreRepository", "Error removing lowest rank user", e)
                    }
            }
        }
    }


}