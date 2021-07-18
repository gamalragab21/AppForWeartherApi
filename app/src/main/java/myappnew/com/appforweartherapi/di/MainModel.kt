package myappnew.com.appforweartherapi.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import myappnew.com.appforweartherapi.data.local.LocationDao
import myappnew.com.conserve.data.LocationDataBase
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Named


@Module
@InstallIn(ViewModelComponent::class)
object MainModel {
//



    @ViewModelScoped
    @Provides
    fun provideSimpleDateFormat(): SimpleDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy h:mm a", Locale.getDefault())



    @ViewModelScoped
    @Provides
    fun provideDate(): Date = Date()


    @Provides
    @ViewModelScoped
    fun provideNoteDao(appDatabase: LocationDataBase): LocationDao {
        return appDatabase.noteDao()
    }

}