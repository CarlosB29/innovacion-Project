package edu.puj.popinapp;

import android.app.Application;

import edu.puj.popinapp.dependencies.components.ApplicationComponent;
import edu.puj.popinapp.dependencies.components.DaggerApplicationComponent;
import lombok.Getter;

@Getter
public class App extends Application {
    ApplicationComponent appComponent = DaggerApplicationComponent.builder().build();
}
