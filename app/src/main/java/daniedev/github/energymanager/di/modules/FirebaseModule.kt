package daniedev.github.energymanager.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import daniedev.github.energymanager.utils.firebase.NODE_USERS
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = Firebase.database

    @Singleton
    @Provides
    fun provideUsersDatabaseReference(firebaseDatabase: FirebaseDatabase): DatabaseReference =
        firebaseDatabase.reference.child(
            NODE_USERS
        )
}