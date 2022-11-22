package edu.puj.popinapp.dependencies.modules;



import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.puj.popinapp.utils.AlertsHelper;


@Module
public class AlertsModule {
    @Singleton
    @Provides
    public AlertsHelper provideAlertHelper() {
        return new AlertsHelper();
    }
}
