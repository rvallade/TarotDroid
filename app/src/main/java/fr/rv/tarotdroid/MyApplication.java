package fr.rv.tarotdroid;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
                mailTo = "tarotdroidgame@gmail.com", 
                mode = ReportingInteractionMode.TOAST, 
                resToastText = R.string.crash_toast_text,
                customReportContent = { ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT })
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
