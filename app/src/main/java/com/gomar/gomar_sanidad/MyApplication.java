package com.gomar.gomar_sanidad;

import android.app.Application;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(
        mailTo = "ivangomezarnedo@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash)
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
