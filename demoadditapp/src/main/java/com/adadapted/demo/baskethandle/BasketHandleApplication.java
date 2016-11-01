package com.adadapted.demo.baskethandle;

import android.app.Application;

import com.adadapted.demo.baskethandle.plugins.AdditPlugin;
import com.adadapted.demo.baskethandle.plugins.FlurryPlugin;
import com.adadapted.demo.baskethandle.plugins.NewRelicPlugin;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class BasketHandleApplication extends Application {
    private static final String LOGTAG = BasketHandleApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        new NewRelicPlugin().initialize(this);
        new FlurryPlugin().initialize(this);
        new AdditPlugin().initialize(this);
    }
}
