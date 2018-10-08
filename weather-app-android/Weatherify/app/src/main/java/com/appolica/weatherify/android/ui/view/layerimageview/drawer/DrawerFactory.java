package com.appolica.weatherify.android.ui.view.layerimageview.drawer;

import com.appolica.weatherify.android.ui.view.layerimageview.drawer.complex.PathBmpDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.BmpLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.PathLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.complex.PathBmpLayer;

public class DrawerFactory {

    public static BmpDrawer createFor(BmpLayer layer) {
        return new BmpDrawer(layer);
    }

    public static PathDrawer createFor(PathLayer layer) {
        return new PathDrawer(layer);
    }

    public static PathBmpDrawer createFor(PathBmpLayer layer) {
        return new PathBmpDrawer(layer);
    }
}
