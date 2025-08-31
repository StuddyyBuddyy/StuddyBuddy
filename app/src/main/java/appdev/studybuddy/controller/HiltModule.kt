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

/**
 * Hilt Module to inject Context into
 *
 * @constructor Create empty App module
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Injects context to UserPreferences
     * @param context
     * @return instance of UserPreferences()
     */
    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    /**
     * Injects context to SensorRepository
     * @param context
     * @return instance of SensorRepository()
     */
    @Singleton
    @Provides
    fun provideSensorRepository(@ApplicationContext context: Context): SensorRepository {
        return SensorRepository(context)
    }

}
