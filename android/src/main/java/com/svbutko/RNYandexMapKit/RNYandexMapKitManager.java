package com.svbutko.RNYandexMapKit;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObject;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingArrivalPoint;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.RequestPoint;
import com.yandex.mapkit.directions.driving.RequestPointType;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.LinearRing;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchType;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.SuggestItem;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.i18n.I18nManagerFactory;
import com.yandex.runtime.i18n.LocaleUpdateListener;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

class SearchResult {
    public WritableMap map;
    public Point location;

    public SearchResult(WritableMap map, Point location) {
        this.map = map;
        this.location = location;
    }
}

public class RNYandexMapKitManager extends SimpleViewManager<MapView> implements UserLocationObjectListener {
    public static final String REACT_CLASS = "RNYandexMapKit";

    public static final int NAVIGATE_TO_REGION = 1;
    public static final int ZOOM_IN = 2;
    public static final int ZOOM_OUT = 3;
    public static final int NAVIGATE_TO_USER_LOCATION = 4;
    public static final int NAVIGATE_TO_BOUNDING_BOX = 5;
    public static final int FETCH_SUGGESTIONS = 6;
    public static final int STOP_MAPKIT = 7;
    public static final int GET_USER_LOCATION = 8;

    public static final String PROP_BOUNDING_BOX = "boundingBox";
    public static final String PROP_MARKERS = "markers";
    public static final String PROP_INITIAL_REGION = "initialRegion";
    public static final String PROP_POLYGONS = "polygons";
    public static final String PROP_ON_MARKER_PRESS = "onMarkerPress";
    public static final String PROP_ON_POLYGON_PRESS = "onPolygonPress";
    public static final String PROP_ON_MAP_PRESS = "onMapPress";
    public static final String PROP_ON_LOCATION_SEARCH = "onLocationSearch";
    public static final String PROP_ON_DEVICE_LOCATION_SEARCH = "onDeviceLocationSearch";
    public static final String PROP_ON_SUGGESTIONS_FETCH = "onSuggestionsFetch";
    public static final String PROP_SEARCH_LOCATION = "searchLocation";
    public static final String PROP_SEARCH_ROUTE = "searchRoute";
    public static final String PROP_SEARCH_MARKER = "searchMarker";

    private UserLocationLayer userLocationLayer;
    private ThemedReactContext context = null;
    private MapView mapView = null;

    private SearchManager searchManager;
    private Session searchSession;
    private Session userSearchSession;

    private DrivingRouter drivingRouter;

    private boolean shouldSearchLocation = false;

    private List<PolygonMapObject> polygonsList = new ArrayList<PolygonMapObject>();
    private List<PlacemarkMapObject> markersList = new ArrayList<PlacemarkMapObject>();
    private List<PolylineMapObject> polylinesList = new ArrayList<PolylineMapObject>();

    private PlacemarkMapObject userSearchPlacemark;
    private final BoundingBox suggestionBoundingBox = new BoundingBox(
            new Point(-180.0, 41.151416124),
            new Point(180.0, 81.2504)
    ); //Russia

    private final SearchOptions suggestionSearchOptions = new SearchOptions().setSearchTypes(SearchType.GEO.value);

    //TODO: Add icon prop
    private byte[] imageDecodedString = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAB4AAAAqCAYAAACk2+sZAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABh0RVh0U29mdHdhcmUAcGFpbnQubmV0IDQuMS40E0BoxAAAAkhJREFUWEftl79O40AQxv0OkMROZCAHytsc75AKIfEOQEFDAz21BQ0F0h05TgEhCoKERIEQgoIKJCr665b5otkwux4HW7FJcVj6SZv5832eOI7XgTFmKqhBn4uZmZjo9prRzsHS/HHSWbgiHpNO+wFrxJBDDWo1DR81aIHI37C+/SdqvNHa5AG16KH12BNQg2g6q9XWe80wt6EPeqFBa/UEUgEUnoSNnhSZBNZKmTsfUPC7FV3LxjJgTcfcMe1FjXPZUCasPTIfmfYb9UNZWAXsMTS3xrgN1OIK6A6NaRHTL/DSS2bS/9E2g7VV87C5MQRrxLRaDfaKC017t7Ji/r280Am7B2LIaT0ZdAM6g10l4dBfbJvXJFFN7YEcalCraUjgGRw3w1ctKYFg3gO1moYEnviq1aRlsPxz7KT+gVr0aFqST42LTGuPPFN/G6f4/4yn9qsGRabOMy0I6Fn5qCUkZf9zwTM4rde2tKRGWf/V8MRXXeiROOnTienCmLY74Y2XqAz2iqe3EWBjbPLuvILSYY+Prc8XTj2c1jfGFujCKywN1nZ3mcK8yqlH0wLfmF5dZve9holhTX1Db0EBbU0GsnESWMsxBc4HCwqLvCFmwRopU5AKWKihjOvtXFeJGgTUhOt94AnlhnvVaYEatKDxJCr+yso9maZADUogwK+ZqokP1441BWrQB0IkeC8NNLjmU1OgBjUg+KsVPUkjCedymQI1mAWE6b58loaAY7lNgRocBwyO5lp7Sad9C47i5l5RU6AGq8cE77senOxoWv4fAAAAAElFTkSuQmCC", Base64.DEFAULT);
    private Bitmap pinBitmapImage = BitmapFactory.decodeByteArray(imageDecodedString, 0, imageDecodedString.length);
    private ImageProvider pinImage = ImageProvider.fromBitmap(pinBitmapImage);

    private byte[] locationDecodedString = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAAAXNSR0IArs4c6QAABVVJREFUaAXdWl1sFFUU/u7uoibSGkNiMAahUCEpSPEFo760D/JoMFh8Mr7W2JhgNUVeIFDRQkrjD1oTXxb1hQYTDCZGIt0iWFCkLEJDFQr+i4bYdtvQn90dz7kzk52Z3fndu23teZm5c+895/vmnLl/ZwRUSverDyGfb4SGekBbQ9eVdK0GRJVuRsvQ/RgEhuk6RNc0YrFeNO//SRUMUbai7pfrkRPPEfCt0LRlkfQJ8SsRPIy49hGaD6Qj6TA6RSf0/raNyKGD9DSUA6BE3xTiaMMLXd+WqPN9FJ7QwW219Db3kjeafLWX00CIHvL6DrzYdTWMmuCEenclMJh5E8i/RN/GojBGIrcVmAFib6Ouajsad2WD6AlGKPnaEoxP9ZBXGoMoVd5GiF4svrMJz79xy0+3P6F329ZCTH+mj1h+6ipYzyOjdsdTaOm47GUl5lWJ7tYnIWb655wMg+QpgLEwJg9x95D0DJPRjDnEQ8tsVgmRgbboMTdPlfYQfzMyzOYZGX5x/IIZG2MsIcWEeDSTAwDP8vNUOPwYI2N1SDEhHprnajRzgPMsMkY5jdhb2b8hOWlicNbmGTuW8CU5T6HOOvk6PMQrgFmaNMPDL+4hsRJmixQ8pK/NzlrqZv32gbvvweaadThz82d8/89vwe3H8ai59it8VPpCM7gSRS2ZxJZV69G0qh5P3F8DIQRa+o6EI6Rjb2RIuod4C5DVLijC6KumFAmzk6ZpWJbcjd8nRs1Hwa4JsYG3HrqH9P1MsI4RW3mRsKo8/ef18GRYgeQAgxBvziogQUlYTfdci7q/kxxeEeBtczb7o1VpOfdRSJj2IoebqSCRWJ2QZwDmg4jXckhYTUYON1MJnWckaN6hA43wYpLYWrsBjy9dIUen8FrsPaKHm6GHuNCgQKczAaUSJEzTHG5Hrl00ixGv2hr2kO8i9Omah9H6SIMyT5RCW3a4sVLiQksfPjfzlrVLlmLjfQ8qCSs3S2WHm1SsVRMh8xDQzRTQfu44aj9+HQd/OIXJLJ1bKBY14cagRJVjceqO9JfxEbSc/BQrDu3BvvMnkJmedG8cskZJuBk2OeToeDa43Lw9jrb+Y1hOxHae/QK3JieCd3ZpqSbcWLmW4ZAbc7Hj+fjfqdvYfe5LLE/uQeupo/gj7NrL0K4u3FihGIvpB+ee2D0rJ7LTOJDuw8pD7WhO9WB41PfozKZPZbgxF/bQkM1CxMJUPocPLvdj9Sd7saP/88Ba1IUbmxRD7KGoq8GSoHM0Qe4bOBHIU2rDjfkgHZP5mZLQoj9kUh3nv/JVoDTc2BrlmmIy2STzM772QzVIXvnOd6BQGm7MgRJnxjxEySbFwt9U50DKVavycOOEGYlOiDNnFRAeJNzmqW/+uhFtZ+qG0+CgE9LTgCm3tlGf85D+Vvpkye6Hryo9wkiZqUwj5MgmpwErIO9c/LpomaQ83CzYC4Q4pynTgGpZjdCa771Lp21KlYYbY7bkYwuEpEnKaerHqzYA5Ra6LvTZVunKwk1iJcwWsROSCVrKaSoWXtB+OHhGalUbboTVkVS2E2KTnKDlnKZi2T/Qi5lcDsrCjTEyVofEHWUgmcrj2YZjmMk+Q3X3FtVHfDBK35JGfY8OX8KVkb8jajG6cb518V2bsLm9aO9SOKx3mlhQKUkmx9nmuNhC4RdqA+h8L0rLjIUxeWTCi78hK4LmzuMyQSt/NrJWzMG9TOtTspgxeYh7yFk7/Y9+vPD2kEmK/+Coq94EEeusxDxlmim68jzDNtl2gL9IuH8wD1ktLZifl6yk+H7B/F7mJLZgfgB0EuPyPPhF8z8Rhj4Ww1Y2ZAAAAABJRU5ErkJggg==", Base64.DEFAULT);
    private Bitmap locationBitmapImage = BitmapFactory.decodeByteArray(locationDecodedString, 0, locationDecodedString.length);
    private ImageProvider locationImage = ImageProvider.fromBitmap(locationBitmapImage);

    private byte[] selectedPinDecodedString = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAB4AAAAqCAYAAACk2+sZAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABh0RVh0U29mdHdhcmUAcGFpbnQubmV0IDQuMS40E0BoxAAAAuVJREFUWEfFlz9oE2EYxl8QRBFE3BTURVx06uBYJzfBTVxcVbrdl7RFp6CbOuYSIy1FEaxZFGwHi3YsSNFBRMW1FlFoBSuY1sj5vHdfkrt+T/5cc0c/+CWX533f57lr7q45CYJgV6BiN2SmdEjKhVGpepfF9wpSMSbcVg01NtMNKsaRR8UDCBmTslnA+5aUvYAS1sKeMZ1hXnGoqEi9vkd8cw18o0G90BmdhQfzVrhYu3kEe/+GmqYCHvCiGY5QGx/B3n7lRjtAveDp5CQ++N5JNK5Tg2FQT3gnstobj0sHUfxIB7NAvZHhBvtmmg5kCTISwVKePI0T4R9tjnHi4e3gztvXwfu11WBjazNEt1XTGptJ4HtNzeoE+95z2hhjYulF0Gg20c6X1sbRw2YT+OYZ2kVkyjsc7Qlpstx9txi5D7C0l3m00SzNxO3uCm2wXJyfspaDL51hXh2QiZcnvBjxef2HtRt8ffr5nXp1QGavO9TI7D1rlX7pLPOMQCY2VtxCxNXFp9Ym/dJZ5mlZ0SPeJIWQG0tz1ib90lnmGYHMXrfI3I5YM3F6f6FFkNt3rJl4eUmLllzOas3EYZdo0ZLLdayZcr9wnhZjZHrnUjRT6qW9OMvWaEOMzO7VmqWZmNHvucqbkgz930mpmEqYGb5UvbO0KQ+Q1Q6Ojtq8oo1Zgox2XntDf5Sz5kwpjDrBUTh+kNOBLDALiazEhwfmFJoa7tDQNNQ7kRX/EAq+uUUGhwOeTo4jzJT24fL6QA12gnrB08nZLoRitXgGe/mHGqVBPeBFM5ioYE/xhEjM0gAP5q1QsQX2eJYaDgJmmWcLKraQurcfJsuOaX+WdZZ5tqBiHJmeOIprcJWYc/TpEDPMKw4Vt2MfXX/RoDjaQx5JGVRkSMU7B+PuZ7rW0MNmGVTshlTNBfzZ/7rB0FBjM92gYi+kXLyEy2QjFvpbNdbbCyr2Q2qTx6VSuB7im2Ospx9UzJ9A/gNsGGyJMIipoAAAAABJRU5ErkJggg==", Base64.DEFAULT);
    private Bitmap selectedPinBitmapImage = BitmapFactory.decodeByteArray(selectedPinDecodedString, 0, selectedPinDecodedString.length);
    private ImageProvider selectedPinImage = ImageProvider.fromBitmap(selectedPinBitmapImage);

    private byte[] userLocationDecodedString = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABh0RVh0U29mdHdhcmUAcGFpbnQubmV0IDQuMS40E0BoxAAABAxJREFUaEPdWltPE0EU3gffffAvaLzHxAf/gX/F90KM4Y2ECGLwjSJeeNBAYkh4MGqiUG4BGwSC3CIUbIEil5oGERSjL+N8p1Niu9PuzpntLvFLvrTN7s75zs7MmXNm6gSKztvnnI76W068Pu7E6xJOe13GaY/l5efvAul7hq7hHtyLZ04UOuuuSXFtUmRWChYs4lm0gbYiw8PYDSlm2CXOnsPUdmiIx87KN9qrERIsYQO2aobhxlOFoRX7oxVQC8IWbMJ2oHjWcEa+sSGt0TAI29AQCNrvXJZvKq01FCZJg9Rihc76m/LtHGgN+OTpxw3i+ou2Y+K37j5fhBZoYoF6hucMRN+fHhQvU7Ni/+iH+Bfffh6KvuUZ0Tw1wHOONJn2FM0Z3jC71HNPTG6vK/nVMba5Ki50t2jbqUpo8z2nKJrxAsAV6Uwqv6vk+sPH3SzTKanRV/RDmNQ14MGLUtT0zoaSaYYR2VO84Se1VgUtmrx15sHMkJLHAwKGrt2qpHWq2uLLzAAwZNJ7OSWNh7ncJjdI9Cr1ZSjkZvqHPNg02a9k2YHVS6A297NINBGCgwDbIWgvAdJ2/Y2+2BK9Q6K09GBGtiJPhEMlEc+mOJO8OxXxHALhAwElsO4GAzZNvlOS7GDlEEjlPOp63UUDImxjxbcB0iCrxBWEL3LsxbUXDdk6nVDSeLDuHRC+yLGX0F405PnuZkphOHidXrTvHRC+qK0m/Q2GRD43sZVRMv1hNLtCL0PXnjmlL/JL3n2BTzj1dP69yB1+V5L12D7YEx2zYwE6Q8yjh7ABqLtoxas9raJrPinerM6L0bVlMb6eEiNrS+LVyhw5jLpJ95wdpS9BOITxj0ltS/t5RA7xhxwEIDFdyu+ogWSHha9b1J6FYzTkWEEBRhMby0pKsOhfX2I6haDACNswNriRUuZrA7wsY6cobBsurDDStzqrzNYWqIJ1GiqSFlbD1AeTNyxgC8yolyj1MUhO0XjyS1qZCwfPFye0WrQ8PmvyWT40fnirzISHg19H/hbf4/IB8FngYUGMAo/mxrV6SlhS4PkowTHccof7ykS4yO7nveeS6/TPY5MkqJ0dLlBE6nQplm2SAB7bWF0LSdV0NHgih7tOF7HiEWaVjcahzCfVdDRAkqvTRZorospW8OKOXYlti4nsZ5cm761goELEG5BVZZTAWZNLV0lkq4QKxymIMuXpfph0RTnfxymAxYFXKDQ68CrC4kiypmQdSRYRwKFxoIQW9qFxEdRTJ2D4kQZuz5SD5pQ7UIRG2DaeM16g6CfDJPPIkkVaZ6RN39GMA1p8eUeXRoQNz0UzSBRyv//g72XlQNpOQ9HijAnPog1XCRA1UAKjrsdmBe0m0RZZiH/RdJy/WRcqH3hhVqIAAAAASUVORK5CYII=", Base64.DEFAULT);
    private Bitmap userLocationBitmapImage = BitmapFactory.decodeByteArray(userLocationDecodedString, 0, userLocationDecodedString.length);
    private ImageProvider userLocationImage = ImageProvider.fromBitmap(userLocationBitmapImage);

    private byte[] disabledLocationDecodedString = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAB4AAAAqCAYAAACk2+sZAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAAMQSURBVFhHzZc9iBNBGIZnN7928ap4CgYbPW0UBO9AECyOXKkIaivcIWhtZWktCIqiIMgVXnFgZ0IKG4WcIJhGoxYSQc9Ul3TmP77v5tuQy01mN7ld9IHjZr7M9z0zO9ndjNXv99W/wJc4n88dxb/ldrt9stVqHUfOHP4OMt2yVN2yrJ14PP41Fot9xrhCNrvyw0k0YBRT2O12b/V6vVUUTUnYCCZXt237WSQSeWSagFZMIeI3Op3ObQjnJDwVmMBONBp9iKvxXDeBPWJKIXyCpKyE9gVq5VHr5rh8l5hS7OEm9uushAIBNT+g5pVRuS3/HSkuz4ugpYQ1WZsOCQ3EDOBLdB/7ecGJhgBr0+HK3RUv41t4WdqhIY5ltq1c7jUv8UvMaIkBE6lUSqXTaTU/f5iXz4lh/9T29i9VrVZVvV53YibgKsJ1neJV9J8OwnoSiaRaWDgB6SGJ6KlWf6ty+YtqNhsSmciajRmcko4WSpeWFj2lhGM4ljkm6LRxN12VvhauNJk8ID1vOJY5Jui04/FYWvp7GOyp90rHYQ5zJ0Hn8D7WkclkpDU9XrlG8SyrdfHKNYrD5P8U876cFa9co7hSqUhrerxyjWI+Amu1mvT8wxyvx6eNZ+03aWsplUqq0fgjPW84ljkm6LTx62BT+lr43C0Wt3ztN8dwrNezmk5fLwmXIN5OwprzWkTyKxQ6I8FQgesjXJds/g5C47HEQ4cuOt1vdQEz+STt0BBHgW1HLKt+wHaY0OH+0hy9jwt4Qb+VduBIbWe1ZCjmTPBbaF26gcPa7mrJ+JOrgHPShrQDQ2oOV0u0Rxhclg3McFFC+wK1tlDr2uhqycRDGxJKSPB1QpwEatRR4/S4lIxfagfZ7zvSnRnW0EmJVizsa791+zqK9lK78JLPcmSddDQdxSgmlOOJ4/voirF7jqQ6PMVE5DkUXJCQFowpY8yKl5T4EhORv0HhYxLaBT77js8u+pES32Ii8ncQHJGQA2I/ETvvV0qmEhPKm83m3VarfY59HEfeJxKJe9NIydTiYFDqL8DWsbxO0qz4AAAAAElFTkSuQmCC", Base64.DEFAULT);
    private Bitmap disabledLocationBitmapImage = BitmapFactory.decodeByteArray(disabledLocationDecodedString, 0, disabledLocationDecodedString.length);
    private ImageProvider disabledLocationImage = ImageProvider.fromBitmap(disabledLocationBitmapImage);
    
    private byte[] courierIcon = Base64.decode("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='110' height='111' viewBox='0 0 110 111' fill='none'%3E%3Cpath d='M103.404 61.7569C89.2342 34.5565 62.6051 16.0834 32.1563 12.3366C27.4952 11.7914 27.8364 14.0179 27.3005 18.505C26.6693 23.0796 27.012 25.661 31.4831 26.3281C31.5946 26.3555 31.7072 26.3744 31.8194 26.3906C56.5864 29.5148 78.2984 44.5768 89.8989 66.681C89.9501 66.7783 90.0095 66.8741 90.071 66.9666C92.3132 71.0214 94.6007 70.7479 98.4903 68.6448C102.808 66.4398 105.464 65.4901 103.404 61.7569Z' fill='%2370544F'/%3E%3Cpath d='M29.0675 44.9612C28.0618 44.719 27.0517 44.7007 26.0503 44.827L23.5844 60.8544C25.1317 61.5606 26.8658 61.8432 28.5929 61.5309C33.1882 60.6997 36.2506 56.2853 35.4196 51.69C34.8224 48.3888 32.3305 45.7465 29.0675 44.9612Z' fill='%2370544F'/%3E%3Cpath d='M52.036 85.0349C50.0997 82.4293 46.916 81.1673 43.7231 81.7447C39.1278 82.5757 36.0657 86.9901 36.8967 91.5854C37.3117 93.8805 38.6125 95.8528 40.5569 97.1399C40.702 97.2359 40.8624 97.2934 41.0118 97.38L53.6528 90.301C53.6794 88.3747 53.171 86.5633 52.036 85.0349Z' fill='%2370544F'/%3E%3Cpath d='M62.9333 61.0577C61.3997 61.335 60.3791 62.8062 60.6564 64.3398C60.9338 65.8733 62.4048 66.8939 63.9385 66.6166C65.4721 66.3393 66.4927 64.868 66.2154 63.3345C65.938 61.801 64.4668 60.7804 62.9333 61.0577Z' fill='%2370544F'/%3E%3Cpath d='M85.1307 69.6985C73.1588 46.9343 54.6242 36.347 30.1333 31.8118C29.4686 31.7176 28.7984 31.5268 28.1323 31.3003L26.9282 39.1258C28.0808 39.1108 29.2391 39.1955 30.3925 39.4718C35.8265 40.7834 39.9812 45.1841 40.9761 50.6857C42.361 58.3439 37.2567 65.7018 29.5985 67.0867C27.2548 67.5105 24.9098 67.2219 22.7166 66.4999L16.1972 106.446C16.0314 107.512 16.4904 108.578 17.3776 109.193C18.2789 109.819 19.4461 109.856 20.3646 109.337L35.6477 100.383C33.4184 98.3232 31.8938 95.643 31.3417 92.5896C29.9568 84.9314 35.0611 77.5735 42.7193 76.1886C48.039 75.2266 53.3463 77.326 56.5689 81.6683C57.8325 83.3697 58.5193 85.324 58.9182 87.3528L86.604 71.8495C86.053 71.1846 85.556 70.4683 85.1307 69.6985ZM49.0874 68.4591C48.1987 69.7402 46.4401 70.0583 45.1587 69.1695C43.8777 68.2809 43.5597 66.5221 44.4483 65.2411C45.3371 63.9602 47.0957 63.6421 48.377 64.5307C49.658 65.4193 49.9761 67.1783 49.0874 68.4591ZM46.696 45.942C45.4148 45.053 45.0969 43.2952 45.9857 42.014C46.8745 40.7328 48.6325 40.4149 49.9137 41.3037L56.474 45.8547C57.7552 46.7434 58.0731 48.5015 57.1843 49.7827C56.2954 51.0641 54.5377 51.3819 53.2563 50.493L46.696 45.942ZM64.9421 72.1699C60.3468 73.0009 55.9324 69.9386 55.1014 65.3433C54.2704 60.748 57.3327 56.3336 61.928 55.5026C66.5233 54.6716 70.9377 57.7339 71.7687 62.3292C72.5997 66.9246 69.5374 71.3389 64.9421 72.1699Z' fill='%2370544F'/%3E%3C/svg%3E", Base64.DEFAULT);
    private Bitmap courierIconBitmapImage = BitmapFactory.decodeByteArray(disabledLocationDecodedString, 0, disabledLocationDecodedString.length);
    private ImageProvider courierImage = ImageProvider.fromBitmap(courierIconBitmapImage);

    private Session.SearchListener searchListener = new Session.SearchListener() {
        @Override
        public void onSearchResponse(@NonNull Response response) {
            try {
                SearchResult result = onSearchResult(response);
                if (result != null) {
                    resetUserMarker(result.location);
                    sendNativeEvent(PROP_ON_LOCATION_SEARCH, result.map, mapView.getId(), context);
                }
            } catch (Exception e) {
                //TODO: Solve the error
            }
        }

        @Override
        public void onSearchError(@NonNull Error error) {
            onSearchErrorCustom(error);
        }
    };

    private Session.SearchListener userSearchListener = new Session.SearchListener() {
        @Override
        public void onSearchResponse(@NonNull Response response) {
            try {
                SearchResult result = onSearchResult(response);
                if (result != null) {
                    sendNativeEvent(PROP_ON_DEVICE_LOCATION_SEARCH, result.map, mapView.getId(), context);
                }
            } catch (Exception e) {
                //TODO: Solve the error
            }
        }

        @Override
        public void onSearchError(@NonNull Error error) {
            onSearchErrorCustom(error);
        }
    };

    private @Nullable SearchResult onSearchResult(@NonNull Response response) {
        List<GeoObjectCollection.Item> searchResultList = response.getCollection().getChildren();

        if(searchResultList.size() > 0) {
            GeoObject geoObject = searchResultList.get(0).getObj();
            Point resultLocation = geoObject.getGeometry().get(0).getPoint();

            if (resultLocation != null) {
                WritableMap writableMap = Arguments.createMap();

                String descriptionLocation = geoObject.getDescriptionText();
                String location = geoObject.getName();

                writableMap.putString("location", location);
                writableMap.putString("descriptionLocation", descriptionLocation);
                writableMap.putDouble("latitude", resultLocation.getLatitude());
                writableMap.putDouble("longitude", resultLocation.getLongitude());
                return new SearchResult(writableMap, resultLocation);
            }
        }

        return null;
    }

    private void resetUserMarker(@Nullable Point location) {
        if (location != null) {
            MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
            if (userSearchPlacemark != null) {
                try {
                    mapObjects.remove(userSearchPlacemark);
                } catch (Exception e) {
                    //TODO: Solve the error
                }
            }
            userSearchPlacemark = mapObjects.addPlacemark(location, userLocationImage);
        }
    }

    private void onSearchErrorCustom(Error error) {
        String errorMessage = "Unknown search error";
        if (error instanceof RemoteError) {
            errorMessage = "Remote server error";
        } else if (error instanceof NetworkError) {
            errorMessage = "Network error";
        }

        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("error", errorMessage);

        sendNativeEvent(PROP_ON_LOCATION_SEARCH, writableMap, mapView.getId(), context);
    }

    private LocaleUpdateListener localeUpdateListener = new LocaleUpdateListener() {
        @Override
        public void onLocaleUpdated() {

        }

        @Override
        public void onLocaleUpdateError(@NonNull Error error) {

        }
    };

    private MapObjectTapListener mapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            if (mapObject instanceof PlacemarkMapObject || mapObject instanceof PolygonMapObject) {
                WritableMap writableMap = Arguments.createMap();
                Object userData = mapObject.getUserData();

                if (userData != null) {
                    try {
                        MarkerUserData markerUserData = (MarkerUserData)userData;
                        writableMap.putMap("data", markerUserData.getData());
                    }
                    catch (Exception e) {

                    }
                }

                writableMap.putDouble("latitude", point.getLatitude());
                writableMap.putDouble("longitude", point.getLongitude());

                String eventName = mapObject instanceof PlacemarkMapObject ? PROP_ON_MARKER_PRESS : PROP_ON_POLYGON_PRESS;

                sendNativeEvent(eventName, writableMap, mapView.getId(), context);
                return true;
            }
            return false;
        }
    };

    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull com.yandex.mapkit.map.Map map, @NonNull Point point) {
            WritableMap writableMap = Arguments.createMap();
            writableMap.putString("latitude", Double.toString(point.getLatitude()));
            writableMap.putString("longitude", Double.toString(point.getLongitude()));

            sendNativeEvent(PROP_ON_MAP_PRESS, writableMap, mapView.getId(), context);
            if(shouldSearchLocation) {
                if (searchSession != null) {
                    searchSession.cancel();
                }

                int zoom = (int)mapView.getMap().getCameraPosition().getZoom();
                searchSession = searchManager.submit(point, zoom, new SearchOptions(), searchListener);
            }
        }

        @Override
        public void onMapLongTap(@NonNull com.yandex.mapkit.map.Map map, @NonNull Point point) {

        }
    };

    private DrivingSession.DrivingRouteListener drivingRouteListener = new DrivingSession.DrivingRouteListener() {
        @Override
        public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
            MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
            clearPolylines(mapView);

            if(!routes.isEmpty()) {
                for (int i = 0; i < routes.size(); i++) {
                    Polyline polylineGeometry = routes.get(i).getGeometry();
                    if (polylineGeometry.getPoints().size() > 0) {
                        PolylineMapObject polyline = mapObjects.addPolyline(polylineGeometry);
                        polyline.setStrokeColor(Color.argb(153,194, 19, 19));
                        polyline.setOutlineWidth(0);
                        polylinesList.add(polyline);
                        break;
                    }
                }
            }
        }

        @Override
        public void onDrivingRoutesError(@NonNull Error error) {
        }
    };

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public MapView createViewInstance(ThemedReactContext context) {
        RNYandexMapKitModule nativeModule = context.getNativeModule(RNYandexMapKitModule.class);

        String locale = nativeModule.getLocale();

        MapKitFactory.setApiKey(nativeModule.getApiKey());
        MapKitFactory.initialize(context);
        SearchFactory.initialize(context);
        DirectionsFactory.initialize(context);
        if (locale != null) {
            I18nManagerFactory.setLocale(locale, localeUpdateListener);
        }
        this.context = context;
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = new MapView(context);
        mapView.getMap().addInputListener(inputListener);
        mapView.getMap().getMapObjects().addCollection();

        if (ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.context.getCurrentActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            this.addUserLocationLayer();
        }

        MapKitFactory.getInstance().onStart();
        mapView.onStart();

        return mapView;
    }


    private void stopMapKit() {
        try {
            mapView.onStop();
            MapKitFactory.getInstance().onStop();
        } catch (Exception e) {
            //TODO: Solve the error
        }
    }

    private void addUserLocationLayer() {
        userLocationLayer = mapView.getMap().getUserLocationLayer();
        userLocationLayer.setEnabled(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
    }

    @ReactProp(name = PROP_SEARCH_MARKER)
    public void setSearchMarker(MapView view, ReadableMap marker) {
        if (marker != null) {
            MapObjectCollection mapObjects = view.getMap().getMapObjects();

            if (userSearchPlacemark != null) {
                try {
                    mapObjects.remove(userSearchPlacemark);
                } catch (Exception e) {
                    //TODO: Solve the error
                }
            }
            ReadableMap latLng = marker.getMap("coordinate");
            double latitude = latLng.getDouble("latitude");
            double longitude = latLng.getDouble("longitude");

            userSearchPlacemark = mapObjects.addPlacemark(new Point(latitude, longitude), userLocationImage);
        }
    }

    @ReactProp(name = PROP_SEARCH_ROUTE)
    public void setSearchRoute(MapView view, ReadableArray markers) {
        if (markers.size() == 2) {
            ArrayList<Point> points = new ArrayList<>();

            for (int i = 0; i < markers.size(); i++) {
                ReadableMap marker = markers.getMap(i);
                ReadableMap latLng = marker.getMap("coordinate");
                double latitude = latLng.getDouble("latitude");
                double longitude = latLng.getDouble("longitude");

                points.add(new Point(latitude, longitude));
            }

            submitRouteRequest(points);
        }
    }

    private void submitRouteRequest(ArrayList<Point> points) {
        try {
            DrivingOptions options = new DrivingOptions();
            List<RequestPoint> requestPoints = new ArrayList<>();

            Point firstPoint = points.get(0);
            Point secondPoint = points.get(1);

            List<Point> arrivingPoint = new ArrayList<>();
            List<DrivingArrivalPoint> drivingArrivalPoint = new ArrayList<>();

            requestPoints.add(new RequestPoint(firstPoint, arrivingPoint, drivingArrivalPoint, RequestPointType.WAYPOINT));
            requestPoints.add(new RequestPoint(secondPoint, arrivingPoint, drivingArrivalPoint, RequestPointType.WAYPOINT));

            drivingRouter.requestRoutes(requestPoints, options, drivingRouteListener);
        } catch (Exception e) {
            //TODO: Solve the error
        }
    }

    public void setNavigateToCoordinates(ReadableMap region, boolean isAnimated) {
        double latitude = region.getDouble("latitude");
        double longitude = region.getDouble("longitude");

        Point point = new Point(latitude, longitude);

        this.navigateToCoordinates(point, isAnimated);
    }

    public void zoomIn() {
        mapView.getMap().move(new CameraPosition(mapView.getMap().getCameraPosition().getTarget(),
                        mapView.getMap().getCameraPosition().getZoom()+2, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1), null);
    }

    public void zoomOut() {
        mapView.getMap().move(new CameraPosition(mapView.getMap().getCameraPosition().getTarget(),
                        mapView.getMap().getCameraPosition().getZoom()-2, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1), null);
    }

    public void navigateToUserLocation() {
        this.navigateToLocationAfterChecks();
    }
    private void navigateToLocationAfterChecks() {
        try {
            if (userLocationLayer == null) {
                this.addUserLocationLayer();
            }

            CameraPosition cameraPosition = userLocationLayer.cameraPosition();
            if (cameraPosition != null) {
                Point point = cameraPosition.getTarget();
                if (point != null) {
                    mapView.getMap().move(
                            new CameraPosition(point, 18.0f, 0.0f, 0.0f),
                            new Animation(Animation.Type.SMOOTH, 1),
                            null);
                }
            }
        } catch (Exception e) {
            //TODO: Solve the error
        }
    }

    public void getUserLocation() {
        try {
            if (userLocationLayer == null) {
                this.addUserLocationLayer();
            }

            CameraPosition cameraPosition = userLocationLayer.cameraPosition();
            if (cameraPosition != null) {

                if (userSearchSession != null) {
                    userSearchSession.cancel();
                }

                Point point = cameraPosition.getTarget();
                userSearchSession = searchManager.submit(point, 20, new SearchOptions(), userSearchListener);
            }
        } catch (Exception e) {
            //TODO: Solve the error
        }
    }

    public void navigateToCoordinates(Point point, Boolean isAnimated) {
        CameraPosition position = new CameraPosition(point, mapView.getMap().getCameraPosition().getZoom(), 0.0f, 0.0f);

        if (isAnimated) {
            mapView.getMap().move(position, new Animation(Animation.Type.SMOOTH, 1), null);
        } else {
            mapView.getMap().move(position);
        }
    }

    @ReactProp(name = PROP_INITIAL_REGION)
    public void setInitialRegion(MapView view, ReadableMap region) {
        double latitude = region.getDouble("latitude");
        double longitude = region.getDouble("longitude");

        Point point = new Point(latitude, longitude);

        view.getMap().move(new CameraPosition(point, 10.0f, 0.0f, 0.0f));
    }

    @ReactProp(name = PROP_BOUNDING_BOX)
    public void setBoundingBox(MapView view, ReadableMap boundingBox) {
        ReadableMap northEastMap = boundingBox.getMap("northEastPoint");
        ReadableMap southWestMap = boundingBox.getMap("southWestPoint");

        this.navigateToBoundingBox(northEastMap, southWestMap, view);
    }

    @ReactProp(name = PROP_SEARCH_LOCATION, defaultBoolean = false)
    public void setShouldSearchLocation(MapView view, boolean shouldSearch) {
        shouldSearchLocation = shouldSearch;
    }

    @ReactProp(name = PROP_POLYGONS)
    public void setPolygons(MapView view, ReadableArray polygons) {
        this.clearPolygons(view);

        if (polygons != null) {
            for (int i = 0; i < polygons.size(); i++) {
                ReadableMap polygon = polygons.getMap(i);
                ReadableArray points = polygon.getArray("points");

                ArrayList<Point> rectPoints = new ArrayList<>();

                for (int j = 0; j < points.size(); j++) {
                    ReadableMap latLng = points.getMap(j);
                    double latitude = latLng.getDouble("latitude");
                    double longitude = latLng.getDouble("longitude");

                    Point point = new Point(latitude, longitude);
                    rectPoints.add(point);
                }

                PolygonMapObject rect = view.getMap().getMapObjects().addPolygon(new Polygon(new LinearRing(rectPoints), new ArrayList<LinearRing>()));
                polygonsList.add(rect);

                if (polygon.hasKey("backgroundColor") && !polygon.isNull("backgroundColor")) {
                    int backgroundColor = (int)polygon.getDouble("backgroundColor");
                    rect.setFillColor(backgroundColor);
                } else {
                    rect.setFillColor(Color.argb(85, 0, 148, 113));
                }

                if (polygon.hasKey("borderColor") && !polygon.isNull("borderColor")) {
                    int borderColor = (int)polygon.getDouble("borderColor");
                    rect.setStrokeColor(borderColor);
                } else {
                    rect.setStrokeColor(Color.rgb(0, 148, 113));
                }
                rect.setStrokeWidth(1.0f);

                if (polygon.hasKey("userData")) {
                    rect.setUserData(new MarkerUserData(polygon.getMap("userData")));
                    rect.addTapListener(mapObjectTapListener);
                }
            }
        }
    }

    @ReactProp(name = PROP_MARKERS)
    public void setMarkers(MapView view, ReadableArray markers) {
        this.clearMarkers(view);

        if (markers != null) {
            for (int i = 0; i < markers.size(); i++) {
                ReadableMap marker = markers.getMap(i);
                Float opacity = marker.hasKey("opacity") ? (float)marker.getDouble("opacity") : 1.0f;

                ReadableMap latLng = marker.getMap("coordinate");
                double latitude = latLng.getDouble("latitude");
                double longitude = latLng.getDouble("longitude");

                boolean draggable = marker.hasKey("draggable") && marker.getBoolean("draggable");
                ImageProvider icon = marker.hasKey("icon") ? getImageById(marker.getString("icon")) : pinImage;

                Point point = new Point(latitude, longitude);
                PlacemarkMapObject mark = view.getMap().getMapObjects().addPlacemark(point);
                markersList.add(mark);

                mark.setOpacity(opacity);
                mark.setIcon(icon);
                mark.setDraggable(draggable);

                if (marker.hasKey("userData")) {
                    mark.setUserData(new MarkerUserData(marker.getMap("userData")));
                    mark.addTapListener(mapObjectTapListener);
                }
            }
        }
    }

    private ImageProvider getImageById(String id) {
        switch (id) {
            case "pin":
                return pinImage;
            case "selectedPin":
                return selectedPinImage;
            case "user":
                return userLocationImage;
            case "disabled":
                return disabledLocationImage;
            case "courier":
                return courierImage;
            default:
                return pinImage;
        }
    }

    private void clearMarkers(MapView view) {
        MapObjectCollection mapObjects = view.getMap().getMapObjects();

        for (PlacemarkMapObject marker : markersList) {
            try {
                mapObjects.remove(marker);
            } catch (Exception e) {
                //TODO: Solve the error
            }
        }

        markersList.clear();
    }

    private void clearPolygons(MapView view) {
        MapObjectCollection mapObjects = view.getMap().getMapObjects();

        for (PolygonMapObject polygon : polygonsList) {
            try {
                mapObjects.remove(polygon);
            } catch (Exception e) {
                //TODO: Solve the error
            }
        }

        polygonsList.clear();
    }

    private void clearPolylines(MapView view) {
        MapObjectCollection mapObjects = view.getMap().getMapObjects();

        for (PolylineMapObject polyline : polylinesList) {
            try {
                mapObjects.remove(polyline);
            } catch (Exception e) {
                //TODO: Solve the error
            }
        }

        polylinesList.clear();
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationView.getPin().setIcon(locationImage);
        userLocationView.getArrow().setIcon(locationImage);
        userLocationView.getAccuracyCircle().setFillColor(Color.TRANSPARENT);
    }

    @Override
    public void onObjectRemoved(UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(UserLocationView userLocationView, ObjectEvent objectEvent) {

    }

    public void navigateToBoundingBox(ReadableMap northEastRegion, ReadableMap southWestRegions, @Nullable MapView view) {
        double neLatitude = northEastRegion.getDouble("latitude");
        double neLongitude = northEastRegion.getDouble("longitude");

        Point northEastPoint = new Point(neLatitude, neLongitude);

        double swLatitude = southWestRegions.getDouble("latitude");
        double swLongitude = southWestRegions.getDouble("longitude");

        Point southWestPoint = new Point(swLatitude, swLongitude);

        MapView map = view != null ? view : mapView;

        try {
            CameraPosition cameraPosition = map.getMap().cameraPosition(new BoundingBox(southWestPoint, northEastPoint));
            map.getMap().move(cameraPosition);
        } catch (Exception e) {
            //TODO: Solve the error
        }
    }

    private SearchManager.SuggestListener suggestListener = new SearchManager.SuggestListener() {
        @Override
        public void onSuggestResponse(@NonNull List<SuggestItem> suggestItems) {
            WritableMap writableMap = Arguments.createMap();
            WritableArray suggestResult = Arguments.createArray();

            int suggestionsSize = Math.min(5, suggestItems.size());

            for (int i = 0; i < suggestionsSize; i++) {
                WritableMap suggestionObject = Arguments.createMap();
                suggestionObject.putString("value", suggestItems.get(i).getTitle().getText());
                suggestionObject.putString("searchText", suggestItems.get(i).getSearchText());
                suggestResult.pushMap(suggestionObject);
            }

            writableMap.putArray("suggestions", suggestResult);
            sendNativeEvent(PROP_ON_SUGGESTIONS_FETCH, writableMap, mapView.getId(), context);
        }

        @Override
        public void onSuggestError(@NonNull Error error) {
        }
    };

    public void fetchSuggestions(String query, @Nullable ReadableMap searchCoordinates) {
        searchManager.cancelSuggest();
        if (query != null && !query.equals("")) {
            BoundingBox searchBox = suggestionBoundingBox;
            if (searchCoordinates != null) {
                ReadableMap southWest = searchCoordinates.getMap("southWest");
                ReadableMap northEast = searchCoordinates.getMap("northEast");
                searchBox = new BoundingBox(
                    new Point(southWest.getDouble("latitude"), southWest.getDouble("longitude")),
                    new Point(northEast.getDouble("latitude"), northEast.getDouble("longitude"))
                );
            }
            searchManager.suggest(query, searchBox, suggestionSearchOptions, suggestListener);
        }
    }

    @Override
    public void receiveCommand(MapView mapView, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(mapView, commandId, args);
        switch (commandId) {
            case NAVIGATE_TO_REGION:
                this.setNavigateToCoordinates(args.getMap(0), args.getBoolean(1));
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
            case GET_USER_LOCATION:
                this.getUserLocation();
                return;
            case NAVIGATE_TO_BOUNDING_BOX:
                this.navigateToBoundingBox(args.getMap(0), args.getMap(1), null);
                return;
            case FETCH_SUGGESTIONS:
                this.fetchSuggestions(args.getString(0), args.getMap(1));
                return;
            case STOP_MAPKIT:
                this.stopMapKit();
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
        Map map = new HashMap<String, Integer>();
        map.put("navigateToRegion", NAVIGATE_TO_REGION);
        map.put("zoomIn", ZOOM_IN);
        map.put("zoomOut", ZOOM_OUT);
        map.put("navigateToUserLocation", NAVIGATE_TO_USER_LOCATION);
        map.put("navigateToBoundingBox", NAVIGATE_TO_BOUNDING_BOX);
        map.put("fetchSuggestions", FETCH_SUGGESTIONS);
        map.put("stopMapKit", STOP_MAPKIT);
        map.put("getUserLocation", GET_USER_LOCATION);

        return map;
    }

    @Nullable
    @Override
    public java.util.Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(PROP_ON_MAP_PRESS, MapBuilder.of("registrationName", PROP_ON_MAP_PRESS))
                .put(PROP_ON_MARKER_PRESS, MapBuilder.of("registrationName", PROP_ON_MARKER_PRESS))
                .put(PROP_ON_LOCATION_SEARCH, MapBuilder.of("registrationName", PROP_ON_LOCATION_SEARCH))
                .put(PROP_ON_POLYGON_PRESS, MapBuilder.of("registrationName", PROP_ON_POLYGON_PRESS))
                .put(PROP_ON_SUGGESTIONS_FETCH, MapBuilder.of("registrationName", PROP_ON_SUGGESTIONS_FETCH))
                .put(PROP_ON_DEVICE_LOCATION_SEARCH, MapBuilder.of("registrationName", PROP_ON_DEVICE_LOCATION_SEARCH))
                .build();
    }

    private void sendNativeEvent(final String eventName, final WritableMap event, final int id, final ReactContext context) {
        context.getJSModule(RCTEventEmitter.class).receiveEvent(id, eventName, event);
    }
}

class MarkerUserData {
    private ReadableMap data;

    public MarkerUserData(ReadableMap data) {
        this.data = data;
    }

    public WritableMap getData() {
        return this.toMap(this.data);
    }

    private WritableMap toMap(ReadableMap readableMap) {
         Bundle bundle = Arguments.toBundle(readableMap);

         return Arguments.fromBundle(bundle);
    }
}
