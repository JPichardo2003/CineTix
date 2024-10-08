package com.ucne.cinetix.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.ucne.cinetix.data.local.database.CineTixDb
import com.ucne.cinetix.data.remote.CineTixApi
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.util.Constants.BASE_URL
import com.ucne.cinetix.util.Constants.OWN_API_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun providesMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Singleton
    @Provides
    fun providesTheMovieDbApi(moshi: Moshi): TheMovieDbApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TheMovieDbApi::class.java)
    }

    @Singleton
    @Provides
    fun providesCineTixApi(moshi: Moshi): CineTixApi {
        return Retrofit.Builder()
            .baseUrl(OWN_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CineTixApi::class.java)
    }

    //Room local
    @Provides
    @Singleton
    fun provideCineTixDb(@ApplicationContext appContext: Context): CineTixDb {
        return Room.databaseBuilder(
            appContext,
            CineTixDb::class.java,
            "CineTix.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFireBaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideWatchListDao(database: CineTixDb) = database.watchListDao()

    @Provides
    @Singleton
    fun provideUsuarioDao(database: CineTixDb) = database.usuarioDao()

    @Provides
    @Singleton
    fun provideCineTixDao(database: CineTixDb) = database.cineTixDao()
}