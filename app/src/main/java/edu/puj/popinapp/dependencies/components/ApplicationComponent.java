package edu.puj.popinapp.dependencies.components;



import javax.inject.Singleton;

import dagger.Component;
import edu.puj.popinapp.activities.BasicActivity;
import edu.puj.popinapp.dependencies.modules.AlertsModule;


@Singleton
@Component(modules = {AlertsModule.class})
public interface ApplicationComponent {
    void inject(BasicActivity activity);
}
