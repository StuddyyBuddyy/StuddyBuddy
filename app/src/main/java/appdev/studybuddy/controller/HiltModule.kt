package appdev.studybuddy.controller

import android.content.Context
import appdev.studybuddy.controller.SensorRepository
import appdev.studybuddy.persistency.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Singleton
    @Provides
    fun provideSensorRepository(@ApplicationContext context: Context): SensorRepository {
        return SensorRepository(context)
    }

}
