package com.yingling.reset.listener;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import com.yingling.reset.helper.BrokenPlugins;
import com.yingling.reset.helper.CustomProperties;
import com.yingling.reset.helper.CustomRepository;
import com.yingling.reset.helper.ReflectionHelper;

import java.lang.reflect.Method;

public class ListenerConnector {
    private static Disposable disposable;

    public static void setup() {
        dispose();

        CustomProperties.fix();
        BrokenPlugins.fix();
        CustomRepository.checkAndAdd(CustomRepository.DEFAULT_HOST);

        Application app = ApplicationManager.getApplication();
        disposable = Disposer.newDisposable();
        Disposer.register(app, disposable);
        MessageBusConnection connection = app.getMessageBus().connect(disposable);
        connection.subscribe(AppLifecycleListener.TOPIC, new AppEventListener());
        connection.subscribe(ApplicationActivationListener.TOPIC, new AppActivationListener());

        callPluginInstallListenerMethod("setup");
    }

    public static void dispose() {
        if (null == disposable || Disposer.isDisposed(disposable)) {
            return;
        }

        callPluginInstallListenerMethod("remove");
        Disposer.dispose(disposable);
        disposable = null;
    }

    private static void callPluginInstallListenerMethod(String methodName) {    // reflection for old versions
        Class<?> klass = ReflectionHelper.getClass("com.intellij.ide.plugins.PluginStateListener");
        if (null == klass) {
            return;
        }

        String className = ListenerConnector.class.getPackage().getName() + ".PluginInstallListener";
        Method method = ReflectionHelper.getMethod(className, methodName);
        if (null == method) {
            return;
        }

        try {
            method.invoke(null);
        } catch (Exception e) {
            // ignored
        }
    }
}
