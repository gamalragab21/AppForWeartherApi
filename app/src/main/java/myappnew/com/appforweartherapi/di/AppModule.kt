package myappnew.com.appforweartherapi.di

import android.content.Context
import android.content.SharedPreferences
import androidx.databinding.library.BuildConfig
import androidx.navigation.Navigator
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import myappnew.com.appforweartherapi.data.remotel.ApiService
import myappnew.com.appforweartherapi.helper.Constants
import myappnew.com.conserve.data.LocationDataBase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Singleton
    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context

    @Singleton
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
    @Singleton
    @Provides
    @Named("Hourly")
    fun provideSimpleDateFormatForHourily(): SimpleDateFormat = SimpleDateFormat("h a", Locale.getDefault())

    @Singleton
    @Provides
    @Named("Daily")
    fun provideSimpleDateFormatForDaily():SimpleDateFormat = SimpleDateFormat("E", Locale.getDefault())


    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL

    @Provides
    @Named("appId")
    fun provideAppId() = Constants.APP_ID

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }else{
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient , BASE_URL:String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)


    @Singleton
    @Provides
    fun provideSharedPrefernces(context : Context) : SharedPreferences = context.getSharedPreferences("SharedPreferencesAPP",Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideEditor(sharedPref : SharedPreferences) : SharedPreferences.Editor =  sharedPref.edit()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): LocationDataBase {
        return Room.databaseBuilder(
            appContext,
            LocationDataBase::class.java,
            "note_DB"
        ).build()
    }

}