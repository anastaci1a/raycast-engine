package game;


// dep

import ana.fx.app.AppX;
import ana.fx.app.launcher.LauncherX;
import ana.fx.window.WindowConfig;


// main

public class Launcher extends LauncherX<MainApp> {
    @Override
    public WindowConfig initWindow() {
        return Config.WINDOW_CONFIG;
    }

    @Override
    public MainApp initApp(AppX.Init init) {
        return new MainApp(init);
    }
}
