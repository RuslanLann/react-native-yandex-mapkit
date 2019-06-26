package com.svbutko.RNYandexMapKit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class RNYandexMapKitManager extends SimpleViewManager<MapView> implements UserLocationObjectListener {
    public static final String REACT_CLASS = "RNYandexMapKit";

    public static final int ANIMATE_TO_REGION = 1;
    public static final int ZOOM_IN = 2;
    public static final int ZOOM_OUT = 3;
    public static final int NAVIGATE_TO_USER_LOCATION = 4;

    public static final String PROP_MARKERS = "markers";
    public static final String PROP_INITIAL_REGION = "initialRegion";
    public static final String PROP_ON_MARKER_PRESS = "onMarkerPress";
    public static final String PROP_ON_MAP_PRESS = "onMapPress";
    public static final String PROP_FOLLOW_USER_LOCATION = "followUserLocation";

    private MapObjectCollection mapObjects;
    private Callback onMarkerPressCallback;
    private Callback onMapPressCallback;
    private UserLocationLayer userLocationLayer;

    private ThemedReactContext context = null;
    private MapView mapView = null;


    //TODO: add icon prop
    private byte[] imageDecodedString = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAB4AAAAqCAYAAACk2+sZAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABh0RVh0U29mdHdhcmUAcGFpbnQubmV0IDQuMS40E0BoxAAAAkhJREFUWEftl79O40AQxv0OkMROZCAHytsc75AKIfEOQEFDAz21BQ0F0h05TgEhCoKERIEQgoIKJCr665b5otkwux4HW7FJcVj6SZv5832eOI7XgTFmKqhBn4uZmZjo9prRzsHS/HHSWbgiHpNO+wFrxJBDDWo1DR81aIHI37C+/SdqvNHa5AG16KH12BNQg2g6q9XWe80wt6EPeqFBa/UEUgEUnoSNnhSZBNZKmTsfUPC7FV3LxjJgTcfcMe1FjXPZUCasPTIfmfYb9UNZWAXsMTS3xrgN1OIK6A6NaRHTL/DSS2bS/9E2g7VV87C5MQRrxLRaDfaKC017t7Ji/r280Am7B2LIaT0ZdAM6g10l4dBfbJvXJFFN7YEcalCraUjgGRw3w1ctKYFg3gO1moYEnviq1aRlsPxz7KT+gVr0aFqST42LTGuPPFN/G6f4/4yn9qsGRabOMy0I6Fn5qCUkZf9zwTM4rde2tKRGWf/V8MRXXeiROOnTienCmLY74Y2XqAz2iqe3EWBjbPLuvILSYY+Prc8XTj2c1jfGFujCKywN1nZ3mcK8yqlH0wLfmF5dZve9holhTX1Db0EBbU0GsnESWMsxBc4HCwqLvCFmwRopU5AKWKihjOvtXFeJGgTUhOt94AnlhnvVaYEatKDxJCr+yso9maZADUogwK+ZqokP1441BWrQB0IkeC8NNLjmU1OgBjUg+KsVPUkjCedymQI1mAWE6b58loaAY7lNgRocBwyO5lp7Sad9C47i5l5RU6AGq8cE77senOxoWv4fAAAAAElFTkSuQmCC", Base64.DEFAULT);
    private Bitmap image = BitmapFactory.decodeByteArray(imageDecodedString, 0, imageDecodedString.length);

    private byte[] locationDecodedString = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAtxJREFUeNrsm79PFEEUx2dRQA3BGOgxyJ0URyKttiSGhoYSSztsoNKa0AAVWBn/CGIhISH8BYAmSgLckfCjRGNDEEhg/U7uEcnJrezuvHlv7/Yln+Zu5828777ZNzu7a8IwNGlIYAWwAMrgnCjTb4W4zlKP36MAAXhHAYd1OKdjgkYUYCYi8FpmGk2A4RjBXzHsQ4Ag4Tz+m9fBrbL1C3gW0/VXMHgbAVKN34MARbCd0P1TsMMpQIvht0GhtmoE6BJqq0aAn0Jt1QiwLtRWjQCVhIGsU9vMC2Dtrac28c3jSnAqxiJoqhGXwtYmwWlE4Kd0jLcT6GsleN16wBta6j6m3/bAEngP9uMKoH0lyD6Fs3ARVGt3mf13gBHwktL9F/gIVmi+X7d7YAi8Bo9oWiyDT+A4a1XAzotxcBRxsdsAn4mNiIvjEfkKslIFHoDFBPf//2ORfKsWoI3SO2RihfpQK8A0Y/BXTGtdB/SD7+AO84X1ApTAlrYyOOEheEN9TLhy5ioDrJA/qHz5MFtOu8GllgwoeQzeUF8lTbfDAwKLuAFNAvQJCPBEkwC9zS6ARAb0aRKgKCBAUYsAD6kk+bZu6ltcAIn0d9a3CwEKggIUcgHyKZALkAsgfTcYCgpg9wMC6QxYFYx/1YWCabfEOsEHw78VVovts1PTnuAL8M1D4LaP585OoONN0VZTfax9whD4CfludZrBTA9GRhkEGOWYwlzPBjcz4pPt6bB9ePHb4e32Jbhvqu8S/5MBGvYDas0O9MChv4Obgte0IXKTVZT6yqQA5WYXYLfZBajkAjAZ50tS7VQK075FFVIJPKu3ENKaAXbAhw78HNYLXvsUcJW6rO8LcwtQVuJDTIBdJT7yKZALIFAGDZWv4xRC27vADiqnJmtl0NDA11K0X4sKPgtTwNpsirZz7KPz9MHEvIm/BTbvZfwevxgZM9UPoS4igrb/2RenX/k6gX8EGABSY47G+1wwtgAAAABJRU5ErkJggg==", Base64.DEFAULT);
    private Bitmap locationImage = BitmapFactory.decodeByteArray(locationDecodedString, 0, locationDecodedString.length);

    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(Map map, Point point) {
            WritableMap writableMap = Arguments.createMap();
            writableMap.putString("latitude", Double.toString(point.getLatitude()));
            writableMap.putString("longitude", Double.toString(point.getLongitude()));

            sendNativeEvent(PROP_ON_MAP_PRESS, writableMap, mapView.getId(), context);
        }

        @Override
        public void onMapLongTap(Map map, Point point) {

        }
    };

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public MapView createViewInstance(ThemedReactContext context) {
        RNYandexMapKitModule nativeModule = context.getNativeModule(RNYandexMapKitModule.class);

        MapKitFactory.setApiKey(nativeModule.getApiKey());
        MapKitFactory.initialize(context);

        this.context = context;

        mapView = new MapView(context);
        mapView.getMap().addInputListener(inputListener);

        mapObjects = mapView.getMap().getMapObjects().addCollection();

        userLocationLayer = mapView.getMap().getUserLocationLayer();
        userLocationLayer.setEnabled(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);

        MapKitFactory.getInstance().onStart();
        mapView.onStart();

        return mapView;
    }

    public void setAnimateToCoordinated(ReadableMap region) {
        double latitude = region.getDouble("latitude");
        double longitude = region.getDouble("longitude");
        double latitudeDelta = region.getDouble("latitudeDelta");
        double longitudeDelta = region.getDouble("longitudeDelta");

        Point point = new Point(latitude, longitude);

        this.animatedToCoordinates(point);
    }

    public void zoomOut() {
        mapView.getMap().move(new CameraPosition(mapView.getMap().getCameraPosition().getTarget(),
                        mapView.getMap().getCameraPosition().getZoom()+1, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1), null);
    }

    public void zoomIn() {
        mapView.getMap().move(new CameraPosition(mapView.getMap().getCameraPosition().getTarget(),
                        mapView.getMap().getCameraPosition().getZoom()-1, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1), null);
    }

    public void navigateToUserLocation() {
        if (userLocationLayer != null) {
            mapView.getMap().move(
                    new CameraPosition(userLocationLayer.cameraPosition().getTarget(), 18.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 1),
                    null);
        }
    }

    public void animatedToCoordinates(Point point) {
        mapView.getMap().move(
                new CameraPosition(point, 18.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);
    }

    @ReactProp(name = PROP_INITIAL_REGION)
    public void setInitialRegion(MapView view, ReadableMap region) {
        double latitude = region.getDouble("latitude");
        double longitude = region.getDouble("longitude");
        double latitudeDelta = region.getDouble("latitudeDelta");
        double longitudeDelta = region.getDouble("longitudeDelta");

        Point point = new Point(latitude, longitude);

        view.getMap().move(
                new CameraPosition(point, 18.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null
        );
    }

    public boolean onMarkerPress(MapObject mapObject, Point point) {
        if (mapObject instanceof PlacemarkMapObject) {
            WritableMap writableMap = Arguments.createMap();
            writableMap.putString("latitude", Double.toString(point.getLatitude()));
            writableMap.putString("longitude", Double.toString(point.getLongitude()));

            sendNativeEvent(PROP_ON_MAP_PRESS, writableMap, mapView.getId(), context);
            return true;
        }
        return false;
    }

    @ReactProp(name = PROP_MARKERS)
    public void setMarkers(MapView view, ReadableArray markers) {
        for (int i = 0; i < markers.size(); i++) {
            ReadableMap marker = markers.getMap(i);
            Float opacity = marker.hasKey("opacity") ? (float)marker.getDouble("opacity") : 1.0f;

            ReadableMap latLng = marker.getMap("coordinate");
            double latitude = latLng.getDouble("latitude");
            double longitude = latLng.getDouble("longitude");

            boolean dragable = marker.hasKey("draggable") && marker.getBoolean("draggable");
            Object userData = marker.hasKey("userData") ? marker.getMap("userData") : new Object();

            Point point = new Point(latitude, longitude);
            PlacemarkMapObject mark = view.getMap().getMapObjects().addPlacemark(point);

            mark.setOpacity(opacity);
            mark.setIcon(ImageProvider.fromBitmap(image));
            mark.setDraggable(dragable);
            mark.setUserData(userData);
            mark.addTapListener(this::onMarkerPress);
        }
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationView.getPin().setIcon(ImageProvider.fromBitmap(locationImage));
        userLocationView.getAccuracyCircle().setFillColor(Color.TRANSPARENT);
    }

    @Override
    public void onObjectRemoved(UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(UserLocationView userLocationView, ObjectEvent objectEvent) {

    }

    @Override
    public void receiveCommand(MapView mapView, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(mapView, commandId, args);
        switch (commandId) {
            case ANIMATE_TO_REGION:
                this.setAnimateToCoordinated(args.getMap(0));
                return;
            case ZOOM_IN:
                this.zoomIn();
                return;
            case ZOOM_OUT:
                this.zoomOut();
                return;
            case NAVIGATE_TO_USER_LOCATION:
                this.navigateToUserLocation();
                return;
            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandId,
                        getClass().getSimpleName()));
        }
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> map = this.CreateMap(
                "animateToRegion", ANIMATE_TO_REGION,
                "zoomIn", ZOOM_IN,
                "zoomOut", ZOOM_OUT,
                "navigateToUserLocation", NAVIGATE_TO_USER_LOCATION
        );

        return map;
    }

    public static <K, V> Map<K, V> CreateMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);

        return map;
    }

    @Nullable
    @Override
    public java.util.Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(PROP_ON_MAP_PRESS, MapBuilder.of("registrationName", PROP_ON_MAP_PRESS))
                .build();
    }

    private void sendNativeEvent(final String eventName, final WritableMap event, final int id, final ReactContext context) {
        context.getJSModule(RCTEventEmitter.class).receiveEvent(id, eventName, event);
    }
}
