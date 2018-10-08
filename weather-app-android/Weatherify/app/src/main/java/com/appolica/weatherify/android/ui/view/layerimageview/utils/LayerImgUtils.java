package com.appolica.weatherify.android.ui.view.layerimageview.utils;

import android.content.Context;

import com.appolica.weatherify.android.ui.view.layerimageview.drawer.DrawerFactory;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.LayerDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.complex.PathBmpDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.complex.PathBmpLayer;

import java.util.ArrayList;
import java.util.List;

public class LayerImgUtils {

    // For binding
    public static List<LayerDrawer> pathBmpDrawersFromArray(Context context, int[] resIds) {
        return pathBmpDrawersFrom(context, resIds);
    }

    public static List<LayerDrawer> pathBmpDrawersFrom(Context context, int... resIds) {
        final List<LayerDrawer> drawers = new ArrayList<>();

        if (resIds == null) {
            return drawers;
        }

        for (int i = 0; i < resIds.length; i++) {
            drawers.add(pathBmpDrawerFrom(context, resIds[i]));
        }

        return drawers;
    }

    public static PathBmpDrawer pathBmpDrawerFrom(Context context, int resId) {
        return DrawerFactory.createFor(pathBmpLayerFrom(context, resId));
    }

    public static PathBmpLayer pathBmpLayerFrom(Context context, int resId) {
        return PathBmpLayer.Builder.instance()
                .shouldDraw(true)
                .bmpLayer()
                .bitmap(context, resId)
                .end()
                .pathLayer()
                .end()
                .build();
    }
}
