#import "RNYandexMapKitView.h"
#import "NSStringCategory.h"
#import "WSPoint.h"

@implementation RNYandexMapKitView

static NSString* iconImage = @"iVBORw0KGgoAAAANSUhEUgAAAB4AAAAqCAYAAACk2+sZAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABh0RVh0U29mdHdhcmUAcGFpbnQubmV0IDQuMS40E0BoxAAAAkhJREFUWEftl79O40AQxv0OkMROZCAHytsc75AKIfEOQEFDAz21BQ0F0h05TgEhCoKERIEQgoIKJCr665b5otkwux4HW7FJcVj6SZv5832eOI7XgTFmKqhBn4uZmZjo9prRzsHS/HHSWbgiHpNO+wFrxJBDDWo1DR81aIHI37C+/SdqvNHa5AG16KH12BNQg2g6q9XWe80wt6EPeqFBa/UEUgEUnoSNnhSZBNZKmTsfUPC7FV3LxjJgTcfcMe1FjXPZUCasPTIfmfYb9UNZWAXsMTS3xrgN1OIK6A6NaRHTL/DSS2bS/9E2g7VV87C5MQRrxLRaDfaKC017t7Ji/r280Am7B2LIaT0ZdAM6g10l4dBfbJvXJFFN7YEcalCraUjgGRw3w1ctKYFg3gO1moYEnviq1aRlsPxz7KT+gVr0aFqST42LTGuPPFN/G6f4/4yn9qsGRabOMy0I6Fn5qCUkZf9zwTM4rde2tKRGWf/V8MRXXeiROOnTienCmLY74Y2XqAz2iqe3EWBjbPLuvILSYY+Prc8XTj2c1jfGFujCKywN1nZ3mcK8yqlH0wLfmF5dZve9holhTX1Db0EBbU0GsnESWMsxBc4HCwqLvCFmwRopU5AKWKihjOvtXFeJGgTUhOt94AnlhnvVaYEatKDxJCr+yso9maZADUogwK+ZqokP1441BWrQB0IkeC8NNLjmU1OgBjUg+KsVPUkjCedymQI1mAWE6b58loaAY7lNgRocBwyO5lp7Sad9C47i5l5RU6AGq8cE77senOxoWv4fAAAAAElFTkSuQmCC";
static NSString* locationImage = @"iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAAAXNSR0IArs4c6QAABVVJREFUaAXdWl1sFFUU/u7uoibSGkNiMAahUCEpSPEFo760D/JoMFh8Mr7W2JhgNUVeIFDRQkrjD1oTXxb1hQYTDCZGIt0iWFCkLEJDFQr+i4bYdtvQn90dz7kzk52Z3fndu23teZm5c+895/vmnLl/ZwRUSverDyGfb4SGekBbQ9eVdK0GRJVuRsvQ/RgEhuk6RNc0YrFeNO//SRUMUbai7pfrkRPPEfCt0LRlkfQJ8SsRPIy49hGaD6Qj6TA6RSf0/raNyKGD9DSUA6BE3xTiaMMLXd+WqPN9FJ7QwW219Db3kjeafLWX00CIHvL6DrzYdTWMmuCEenclMJh5E8i/RN/GojBGIrcVmAFib6Ouajsad2WD6AlGKPnaEoxP9ZBXGoMoVd5GiF4svrMJz79xy0+3P6F329ZCTH+mj1h+6ipYzyOjdsdTaOm47GUl5lWJ7tYnIWb655wMg+QpgLEwJg9x95D0DJPRjDnEQ8tsVgmRgbboMTdPlfYQfzMyzOYZGX5x/IIZG2MsIcWEeDSTAwDP8vNUOPwYI2N1SDEhHprnajRzgPMsMkY5jdhb2b8hOWlicNbmGTuW8CU5T6HOOvk6PMQrgFmaNMPDL+4hsRJmixQ8pK/NzlrqZv32gbvvweaadThz82d8/89vwe3H8ai59it8VPpCM7gSRS2ZxJZV69G0qh5P3F8DIQRa+o6EI6Rjb2RIuod4C5DVLijC6KumFAmzk6ZpWJbcjd8nRs1Hwa4JsYG3HrqH9P1MsI4RW3mRsKo8/ef18GRYgeQAgxBvziogQUlYTfdci7q/kxxeEeBtczb7o1VpOfdRSJj2IoebqSCRWJ2QZwDmg4jXckhYTUYON1MJnWckaN6hA43wYpLYWrsBjy9dIUen8FrsPaKHm6GHuNCgQKczAaUSJEzTHG5Hrl00ixGv2hr2kO8i9Omah9H6SIMyT5RCW3a4sVLiQksfPjfzlrVLlmLjfQ8qCSs3S2WHm1SsVRMh8xDQzRTQfu44aj9+HQd/OIXJLJ1bKBY14cagRJVjceqO9JfxEbSc/BQrDu3BvvMnkJmedG8cskZJuBk2OeToeDa43Lw9jrb+Y1hOxHae/QK3JieCd3ZpqSbcWLmW4ZAbc7Hj+fjfqdvYfe5LLE/uQeupo/gj7NrL0K4u3FihGIvpB+ee2D0rJ7LTOJDuw8pD7WhO9WB41PfozKZPZbgxF/bQkM1CxMJUPocPLvdj9Sd7saP/88Ba1IUbmxRD7KGoq8GSoHM0Qe4bOBHIU2rDjfkgHZP5mZLQoj9kUh3nv/JVoDTc2BrlmmIy2STzM772QzVIXvnOd6BQGm7MgRJnxjxEySbFwt9U50DKVavycOOEGYlOiDNnFRAeJNzmqW/+uhFtZ+qG0+CgE9LTgCm3tlGf85D+Vvpkye6Hryo9wkiZqUwj5MgmpwErIO9c/LpomaQ83CzYC4Q4pynTgGpZjdCa771Lp21KlYYbY7bkYwuEpEnKaerHqzYA5Ra6LvTZVunKwk1iJcwWsROSCVrKaSoWXtB+OHhGalUbboTVkVS2E2KTnKDlnKZi2T/Qi5lcDsrCjTEyVofEHWUgmcrj2YZjmMk+Q3X3FtVHfDBK35JGfY8OX8KVkb8jajG6cb518V2bsLm9aO9SOKx3mlhQKUkmx9nmuNhC4RdqA+h8L0rLjIUxeWTCi78hK4LmzuMyQSt/NrJWzMG9TOtTspgxeYh7yFk7/Y9+vPD2kEmK/+Coq94EEeusxDxlmim68jzDNtl2gL9IuH8wD1ktLZifl6yk+H7B/F7mJLZgfgB0EuPyPPhF8z8Rhj4Ww1Y2ZAAAAABJRU5ErkJggg==";
static NSString* selectedPinImage = @"iVBORw0KGgoAAAANSUhEUgAAAB4AAAAqCAYAAACk2+sZAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABh0RVh0U29mdHdhcmUAcGFpbnQubmV0IDQuMS40E0BoxAAAAuVJREFUWEfFlz9oE2EYxl8QRBFE3BTURVx06uBYJzfBTVxcVbrdl7RFp6CbOuYSIy1FEaxZFGwHi3YsSNFBRMW1FlFoBSuY1sj5vHdfkrt+T/5cc0c/+CWX533f57lr7q45CYJgV6BiN2SmdEjKhVGpepfF9wpSMSbcVg01NtMNKsaRR8UDCBmTslnA+5aUvYAS1sKeMZ1hXnGoqEi9vkd8cw18o0G90BmdhQfzVrhYu3kEe/+GmqYCHvCiGY5QGx/B3n7lRjtAveDp5CQ++N5JNK5Tg2FQT3gnstobj0sHUfxIB7NAvZHhBvtmmg5kCTISwVKePI0T4R9tjnHi4e3gztvXwfu11WBjazNEt1XTGptJ4HtNzeoE+95z2hhjYulF0Gg20c6X1sbRw2YT+OYZ2kVkyjsc7Qlpstx9txi5D7C0l3m00SzNxO3uCm2wXJyfspaDL51hXh2QiZcnvBjxef2HtRt8ffr5nXp1QGavO9TI7D1rlX7pLPOMQCY2VtxCxNXFp9Ym/dJZ5mlZ0SPeJIWQG0tz1ib90lnmGYHMXrfI3I5YM3F6f6FFkNt3rJl4eUmLllzOas3EYZdo0ZLLdayZcr9wnhZjZHrnUjRT6qW9OMvWaEOMzO7VmqWZmNHvucqbkgz930mpmEqYGb5UvbO0KQ+Q1Q6Ojtq8oo1Zgox2XntDf5Sz5kwpjDrBUTh+kNOBLDALiazEhwfmFJoa7tDQNNQ7kRX/EAq+uUUGhwOeTo4jzJT24fL6QA12gnrB08nZLoRitXgGe/mHGqVBPeBFM5ioYE/xhEjM0gAP5q1QsQX2eJYaDgJmmWcLKraQurcfJsuOaX+WdZZ5tqBiHJmeOIprcJWYc/TpEDPMKw4Vt2MfXX/RoDjaQx5JGVRkSMU7B+PuZ7rW0MNmGVTshlTNBfzZ/7rB0FBjM92gYi+kXLyEy2QjFvpbNdbbCyr2Q2qTx6VSuB7im2Ospx9UzJ9A/gNsGGyJMIipoAAAAABJRU5ErkJggg==";
static NSString* userLocationImage = @"iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABh0RVh0U29mdHdhcmUAcGFpbnQubmV0IDQuMS40E0BoxAAABAxJREFUaEPdWltPE0EU3gffffAvaLzHxAf/gX/F90KM4Y2ECGLwjSJeeNBAYkh4MGqiUG4BGwSC3CIUbIEil5oGERSjL+N8p1Niu9PuzpntLvFLvrTN7s75zs7MmXNm6gSKztvnnI76W068Pu7E6xJOe13GaY/l5efvAul7hq7hHtyLZ04UOuuuSXFtUmRWChYs4lm0gbYiw8PYDSlm2CXOnsPUdmiIx87KN9qrERIsYQO2aobhxlOFoRX7oxVQC8IWbMJ2oHjWcEa+sSGt0TAI29AQCNrvXJZvKq01FCZJg9Rihc76m/LtHGgN+OTpxw3i+ou2Y+K37j5fhBZoYoF6hucMRN+fHhQvU7Ni/+iH+Bfffh6KvuUZ0Tw1wHOONJn2FM0Z3jC71HNPTG6vK/nVMba5Ki50t2jbqUpo8z2nKJrxAsAV6Uwqv6vk+sPH3SzTKanRV/RDmNQ14MGLUtT0zoaSaYYR2VO84Se1VgUtmrx15sHMkJLHAwKGrt2qpHWq2uLLzAAwZNJ7OSWNh7ncJjdI9Cr1ZSjkZvqHPNg02a9k2YHVS6A297NINBGCgwDbIWgvAdJ2/Y2+2BK9Q6K09GBGtiJPhEMlEc+mOJO8OxXxHALhAwElsO4GAzZNvlOS7GDlEEjlPOp63UUDImxjxbcB0iCrxBWEL3LsxbUXDdk6nVDSeLDuHRC+yLGX0F405PnuZkphOHidXrTvHRC+qK0m/Q2GRD43sZVRMv1hNLtCL0PXnjmlL/JL3n2BTzj1dP69yB1+V5L12D7YEx2zYwE6Q8yjh7ABqLtoxas9raJrPinerM6L0bVlMb6eEiNrS+LVyhw5jLpJ95wdpS9BOITxj0ltS/t5RA7xhxwEIDFdyu+ogWSHha9b1J6FYzTkWEEBRhMby0pKsOhfX2I6haDACNswNriRUuZrA7wsY6cobBsurDDStzqrzNYWqIJ1GiqSFlbD1AeTNyxgC8yolyj1MUhO0XjyS1qZCwfPFye0WrQ8PmvyWT40fnirzISHg19H/hbf4/IB8FngYUGMAo/mxrV6SlhS4PkowTHccof7ykS4yO7nveeS6/TPY5MkqJ0dLlBE6nQplm2SAB7bWF0LSdV0NHgih7tOF7HiEWaVjcahzCfVdDRAkqvTRZorospW8OKOXYlti4nsZ5cm761goELEG5BVZZTAWZNLV0lkq4QKxymIMuXpfph0RTnfxymAxYFXKDQ68CrC4kiypmQdSRYRwKFxoIQW9qFxEdRTJ2D4kQZuz5SD5pQ7UIRG2DaeM16g6CfDJPPIkkVaZ6RN39GMA1p8eUeXRoQNz0UzSBRyv//g72XlQNpOQ9HijAnPog1XCRA1UAKjrsdmBe0m0RZZiH/RdJy/WRcqH3hhVqIAAAAASUVORK5CYII=";
static NSString* disabledImage = @"iVBORw0KGgoAAAANSUhEUgAAAB4AAAAqCAYAAACk2+sZAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAAMQSURBVFhHzZc9iBNBGIZnN7928ap4CgYbPW0UBO9AECyOXKkIaivcIWhtZWktCIqiIMgVXnFgZ0IKG4WcIJhGoxYSQc9Ul3TmP77v5tuQy01mN7ld9IHjZr7M9z0zO9ndjNXv99W/wJc4n88dxb/ldrt9stVqHUfOHP4OMt2yVN2yrJ14PP41Fot9xrhCNrvyw0k0YBRT2O12b/V6vVUUTUnYCCZXt237WSQSeWSagFZMIeI3Op3ObQjnJDwVmMBONBp9iKvxXDeBPWJKIXyCpKyE9gVq5VHr5rh8l5hS7OEm9uushAIBNT+g5pVRuS3/HSkuz4ugpYQ1WZsOCQ3EDOBLdB/7ecGJhgBr0+HK3RUv41t4WdqhIY5ltq1c7jUv8UvMaIkBE6lUSqXTaTU/f5iXz4lh/9T29i9VrVZVvV53YibgKsJ1neJV9J8OwnoSiaRaWDgB6SGJ6KlWf6ty+YtqNhsSmciajRmcko4WSpeWFj2lhGM4ljkm6LRxN12VvhauNJk8ID1vOJY5Jui04/FYWvp7GOyp90rHYQ5zJ0Hn8D7WkclkpDU9XrlG8SyrdfHKNYrD5P8U876cFa9co7hSqUhrerxyjWI+Amu1mvT8wxyvx6eNZ+03aWsplUqq0fgjPW84ljkm6LTx62BT+lr43C0Wt3ztN8dwrNezmk5fLwmXIN5OwprzWkTyKxQ6I8FQgesjXJds/g5C47HEQ4cuOt1vdQEz+STt0BBHgW1HLKt+wHaY0OH+0hy9jwt4Qb+VduBIbWe1ZCjmTPBbaF26gcPa7mrJ+JOrgHPShrQDQ2oOV0u0Rxhclg3McFFC+wK1tlDr2uhqycRDGxJKSPB1QpwEatRR4/S4lIxfagfZ7zvSnRnW0EmJVizsa791+zqK9lK78JLPcmSddDQdxSgmlOOJ4/voirF7jqQ6PMVE5DkUXJCQFowpY8yKl5T4EhORv0HhYxLaBT77js8u+pES32Ii8ncQHJGQA2I/ETvvV0qmEhPKm83m3VarfY59HEfeJxKJe9NIydTiYFDqL8DWsbxO0qz4AAAAAElFTkSuQmCC";
static NSString* courierImage = @"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";

+ (NSString*) iconImage { return iconImage; }
+ (NSString*) locationImage { return locationImage; }
+ (NSString*) selectedPinImage { return selectedPinImage; }
+ (NSString*) userLocationImage { return userLocationImage; }
+ (NSString*) disabledImage { return disabledImage; }
+ (NSString*) courierImage { return courierImage; }

- (instancetype) init
{
    self = [super init];

    _mapPolygons = [[NSMutableArray alloc]init];
    _mapMarkers = [[NSMutableArray alloc]init];
    _mapPolylines = [[NSMutableArray alloc]init];

    _map = [[YMKMapView alloc] initWithFrame:self.bounds];
    _map.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    [_map.mapWindow.map addInputListenerWithInputListener: self];
    [_map.mapWindow.map addCameraListenerWithCameraListener: self];

    [self addSubview:_map];

    _searchManager = [YMKSearch.sharedInstance createSearchManagerWithSearchManagerType:YMKSearchSearchManagerTypeCombined];

    self.locationManager = [CLLocationManager new];
    self.locationManager.delegate = self;

    YMKUserLocationLayer* userLocationLayer = _map.mapWindow.map.userLocationLayer;
    [userLocationLayer setEnabled: true];
    [userLocationLayer setHeadingEnabled: true];
    [userLocationLayer setObjectListenerWithObjectListener: self];

    _userLocationIcon = [locationImage decodeBase64ToImage];
    _zoom = 0;

    return self;
}


- (void) willMoveToSuperview:(nullable UIView *)newSuperview {
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    if (status == kCLAuthorizationStatusAuthorizedAlways || status == kCLAuthorizationStatusAuthorizedWhenInUse) {
        YMKUserLocationLayer* userLocationLayer = _map.mapWindow.map.userLocationLayer;
        [userLocationLayer setEnabled: true];
        [userLocationLayer setHeadingEnabled: true];
        [userLocationLayer setObjectListenerWithObjectListener: self];
    }
}


- (void) onMapTapWithMap:(YMKMap *)map point:(nonnull YMKPoint *)point
{
    [[UIApplication sharedApplication] sendAction:@selector(resignFirstResponder) to:nil from:nil forEvent:nil];

    if (_searchLocation) {
        YMKSearchOptions* options = [YMKSearchOptions new];
        options.searchTypes = YMKSearchTypeGeo;

        _searchSession = [_searchManager submitWithPoint:point
                                                    zoom:@(_zoom)
                                           searchOptions:options
                                         responseHandler:^(YMKSearchResponse *response, NSError *error) {
                                             NSArray* searchResultList = [[response collection] children];

                                             if ([searchResultList count] > 0) {
                                                 YMKGeoObject* geoObject = [searchResultList[0] obj];
                                                 YMKPoint* resultLocation = [[geoObject geometry][0] point];

                                                 if (resultLocation != nil) {
                                                     YMKMapObjectCollection* mapObjects = self.map.mapWindow.map.mapObjects;
                                                     if (self.userSearchPlacemark != nil) {
                                                         @try {
                                                             [mapObjects removeWithMapObject: self.userSearchPlacemark];
                                                         } @catch (NSException *exception) {
                                                             //TODO: Solve the error
                                                         }
                                                     }

                                                     self.userSearchPlacemark = [mapObjects addPlacemarkWithPoint:resultLocation image:[userLocationImage decodeBase64ToImage]];

                                                     NSString* descriptionLocation = [geoObject descriptionText];
                                                     NSString* location = [geoObject name];

                                                     NSDictionary* addressDict = @{
                                                                                   @"latitude" : [NSString stringWithFormat:@"%f", point.latitude],
                                                                                   @"longitude" : [NSString stringWithFormat:@"%f", point.longitude],
                                                                                   @"location": location,
                                                                                   @"descriptionLocation": descriptionLocation == nil ? @"" : descriptionLocation,
                                                                                   };

                                                     NSLog(@"onLocationSearch with output info: %@", addressDict);

                                                     self.onLocationSearch(addressDict);
                                                 }
                                             }
                                         }];

    } else {
        NSDictionary* addressDict = @{
                                      @"latitude" : [NSString stringWithFormat:@"%f", point.latitude],
                                      @"longitude" : [NSString stringWithFormat:@"%f", point.longitude]
                                      };

        NSLog(@"onMapPress with output info: %@", addressDict);
        self.onMapPress(addressDict);
    }
}

- (void) setSearchMarker:(NSDictionary *)searchMarker {
    YMKMapObjectCollection* mapObjects = _map.mapWindow.map.mapObjects;

    if (_userSearchPlacemark != nil) {
        @try {
            [mapObjects removeWithMapObject: self.userSearchPlacemark];
        } @catch (NSException *exception) {
            //TODO: Solve the error
        }
    }

    NSDictionary *coordinates = [searchMarker objectForKey:@"coordinate"];

    double latitude = [[coordinates valueForKey:@"latitude"] doubleValue];
    double longitude = [[coordinates valueForKey:@"longitude"] doubleValue];

    YMKPoint* point = [YMKPoint pointWithLatitude:latitude longitude:longitude];

    _userSearchPlacemark = [mapObjects addPlacemarkWithPoint:point image:[userLocationImage decodeBase64ToImage]];
}

- (void) onMapLongTapWithMap:(YMKMap *)map point:(nonnull YMKPoint *)point
{
    [self onMapTapWithMap:map point:point];
}

- (void) setSearchRoute:(NSArray *)searchRoute {
    NSMutableArray* points = [[NSMutableArray alloc]init];

    for (NSDictionary *marker in searchRoute) {
        NSDictionary *coordinates = [marker objectForKey:@"coordinate"];

        double latitude = [[coordinates valueForKey:@"latitude"] doubleValue];
        double longitude = [[coordinates valueForKey:@"longitude"] doubleValue];

        YMKPoint* point = [YMKPoint pointWithLatitude:latitude longitude:longitude];
        [points addObject:point];
    }

    [self submitRouteRequest:points];
}

- (void) submitRouteRequest:(NSMutableArray *)points {
    @try {
        if ([points count] == 2) {
            NSMutableArray* requestPoints = [[NSMutableArray alloc]init];
            YMKDrivingDrivingOptions *options = [[YMKDrivingDrivingOptions alloc]init];

            YMKRequestPoint *firstPoint = [YMKRequestPoint requestPointWithPoint:[points objectAtIndex:0] type:YMKRequestPointTypeWaypoint pointContext:nil];
            YMKRequestPoint *secondPoint = [YMKRequestPoint requestPointWithPoint:[points objectAtIndex:1] type:YMKRequestPointTypeWaypoint pointContext:nil];
            [requestPoints addObject:firstPoint];
            [requestPoints addObject:secondPoint];

            [_drivingRouter requestRoutesWithPoints:requestPoints
                                         drivingOptions:options
                                           routeHandler:^(NSArray<YMKDrivingRoute *> *routes, NSError *error) {
                                               [self clearPolylines];
                                               YMKMapObjectCollection* mapObjects = self.map.mapWindow.map.mapObjects;

                                               if ([routes count] > 0) {
                                                   YMKPolylineMapObject *polyline = [mapObjects addPolylineWithPolyline:[[routes objectAtIndex:0] geometry]];
                                                   [polyline setStrokeColor:[UIColor colorWithRed:194.0f/255.0f
                                                                                           green:19.0f/255.0f
                                                                                            blue:19.0f/255.0f
                                                                                           alpha:1.0f]];
                                                   [self.mapPolylines addObject:polyline];
                                               }
                                           }];
        }
    } @catch (NSException *exception) {
        //TODO: Solve the error
    }
}


- (void) addMarkerWithJSON: (NSMutableDictionary*)dict {
    UIImage* icon = [self getImageByID:[dict valueForKey:@"icon"]];
    double longitude = [[dict objectForKey:@"longitude"] doubleValue];
    double latitude = [[dict objectForKey:@"latitude"] doubleValue];

    YMKMapObjectCollection* mapObjects = _map.mapWindow.map.mapObjects;
    YMKPoint* point = [YMKPoint pointWithLatitude:latitude longitude:longitude];
    YMKPlacemarkMapObject* placemark = [mapObjects addPlacemarkWithPoint:point];

    [_mapMarkers addObject:placemark];

    [placemark setIconWithImage: icon];
    [placemark setOpacity: 1];
    [placemark setDraggable: false];

    NSDictionary* userData = dict[@"userData"];
    if (userData != nil) {
        [placemark setUserData: userData];
        [placemark addTapListenerWithTapListener: self];
    }
}

- (UIImage*) getImageByID:(NSString *)imageId {
    if ([imageId isEqualToString:@"pin"]) {
        return [iconImage decodeBase64ToImage];
    } else if ([imageId isEqualToString:@"selectedPin"]) {
        return [selectedPinImage decodeBase64ToImage];
    } else if ([imageId isEqualToString:@"user"]) {
        return [userLocationImage decodeBase64ToImage];
    } else if ([imageId isEqualToString:@"disabled"]) {
        return [disabledImage decodeBase64ToImage];
    } else if ([imageId isEqualToString:@"courier"]) {
        return [courierImage decodeBase64ToImage];
    } else {
        return [iconImage decodeBase64ToImage];
    }
}

- (void) addPolygon: (NSMutableArray*)rectPoints backgroundColor: (UIColor*)backgroundColor borderColor: (UIColor*)borderColor dict: (NSDictionary*)dict {
    YMKPolygon *jsPolygon = [YMKPolygon polygonWithOuterRing:[YMKLinearRing linearRingWithPoints:rectPoints] innerRings:[[NSMutableArray alloc]init]];

    YMKMapObjectCollection* mapObjects = _map.mapWindow.map.mapObjects;
    YMKPolygonMapObject* polygon = [mapObjects addPolygonWithPolygon:jsPolygon];

    [_mapPolygons addObject:polygon];

    [polygon setStrokeColor:borderColor];
    [polygon setStrokeWidth:1.0f];
    [polygon setFillColor:backgroundColor];

    NSDictionary* userData = dict[@"userData"];
    if (userData != nil) {
        [polygon setUserData: userData];
        [polygon addTapListenerWithTapListener: self];
    }
}

- (void) clearMarkers {
    YMKMapObjectCollection* mapObjects = _map.mapWindow.map.mapObjects;

    for (YMKPlacemarkMapObject* marker in _mapMarkers) {
        @try {
            [mapObjects removeWithMapObject:marker];
        } @catch (NSException *exception) {
            //TODO: Solve the error
        }
    }

    [_mapMarkers removeAllObjects];
}

- (void) clearPolygons {
    YMKMapObjectCollection* mapObjects = _map.mapWindow.map.mapObjects;

    for (YMKPlacemarkMapObject* polygon in _mapPolygons) {
        @try {
            [mapObjects removeWithMapObject:polygon];
        } @catch (NSException *exception) {
            //TODO: Solve the error
        }
    }

    [_mapPolygons removeAllObjects];
}

- (void) clearPolylines {
    YMKMapObjectCollection* mapObjects = _map.mapWindow.map.mapObjects;

    for (YMKPlacemarkMapObject* polyline in _mapPolylines) {
        @try {
            [mapObjects removeWithMapObject:polyline];
        } @catch (NSException *exception) {
            //TODO: Solve the error
        }
    }

    [_mapPolylines removeAllObjects];
}

- (void) setSearchLocation: (BOOL)json {
    _searchLocation = json;
}

- (void) navigateToUserLocation {
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        YMKPoint* point = [self getDeviceLocation];
        YMKCameraPosition* cameraPos = [YMKCameraPosition cameraPositionWithTarget:point zoom:14 azimuth:0 tilt:0];
        YMKAnimation* animation = [YMKAnimation animationWithType:YMKAnimationTypeSmooth duration:1];

        [self.map.mapWindow.map moveWithCameraPosition:cameraPos animationType:animation cameraCallback:nil];
    }];
}

- (void) getUserLocation {
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        YMKPoint* point = [self getDeviceLocation];
        YMKCameraPosition* cameraPos = [YMKCameraPosition cameraPositionWithTarget:point zoom:14 azimuth:0 tilt:0];

        YMKSearchOptions* options = [YMKSearchOptions new];
        options.searchTypes = YMKSearchTypeGeo;

        _searchSession = [_searchManager submitWithPoint:cameraPos.target
                                                    zoom:[NSNumber numberWithInt: 20]
                                           searchOptions:options
                                         responseHandler:^(YMKSearchResponse *response, NSError *error) {
                                             NSArray* searchResultList = [[response collection] children];

                                             if ([searchResultList count] > 0) {
                                                 YMKGeoObject* geoObject = [searchResultList[0] obj];
                                                 YMKPoint* resultLocation = [[geoObject geometry][0] point];

                                                 if (resultLocation != nil) {
                                                     NSString* descriptionLocation = [geoObject descriptionText];
                                                     NSString* location = [geoObject name];

                                                     NSDictionary* addressDict = @{
                                                                                   @"latitude" : [NSString stringWithFormat:@"%f", point.latitude],
                                                                                   @"longitude" : [NSString stringWithFormat:@"%f", point.longitude],
                                                                                   @"location": location,
                                                                                   @"descriptionLocation": descriptionLocation == nil ? @"" : descriptionLocation,
                                                                                   };

                                                     NSLog(@"onLocationSearch with output info: %@", addressDict);

                                                     self.onDeviceLocationSearch(addressDict);
                                                 }
                                             }
                                         }];
    }];
}

- (void) zoomIn {
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        YMKPoint* point = self.map.mapWindow.map.cameraPosition.target;
        float zoom = self.map.mapWindow.map.cameraPosition.zoom + 2;

        YMKCameraPosition* cameraPos = [YMKCameraPosition cameraPositionWithTarget:point zoom:zoom azimuth:0 tilt:0];
        YMKAnimation* animation = [YMKAnimation animationWithType:YMKAnimationTypeSmooth duration:1];

        [self.map.mapWindow.map moveWithCameraPosition:cameraPos animationType:animation cameraCallback:nil];
    }];
}

- (void) zoomOut {
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        YMKPoint* point = self.map.mapWindow.map.cameraPosition.target;
        float zoom = self.map.mapWindow.map.cameraPosition.zoom - 2;

        YMKCameraPosition* cameraPos = [YMKCameraPosition cameraPositionWithTarget:point zoom:zoom azimuth:0 tilt:0];
        YMKAnimation* animation = [YMKAnimation animationWithType:YMKAnimationTypeSmooth duration:1];

        [self.map.mapWindow.map moveWithCameraPosition:cameraPos animationType:animation cameraCallback:nil];
    }];
}

- (void) fetchSuggestions:(NSString *)query searchCoordinates: (NSDictionary *)searchCoordinates {
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        [_searchManager cancelSuggest];
        if ([query length] != 0) {
            YMKBoundingBox* boundingBox = [YMKBoundingBox boundingBoxWithSouthWest:[YMKPoint pointWithLatitude:-180.0 longitude:41.151416124] northEast:[YMKPoint pointWithLatitude:180.0 longitude:81.2504]];
            if (searchCoordinates != nil) {
                NSDictionary* southWest = [searchCoordinates objectForKey:@"southWest"];
                NSDictionary* northEast = [searchCoordinates objectForKey:@"northEast"];
                boundingBox = [YMKBoundingBox boundingBoxWithSouthWest:
                               [YMKPoint pointWithLatitude: [[southWest objectForKey:@"latitude"] doubleValue]
                                                 longitude: [[southWest objectForKey:@"longitude"] doubleValue]]
                                                             northEast:
                               [YMKPoint pointWithLatitude: [[northEast objectForKey:@"latitude"] doubleValue]
                                                 longitude: [[northEast objectForKey:@"longitude"] doubleValue]]];
            }

            YMKSearchOptions* options = [YMKSearchOptions new];
            options.searchTypes = YMKSearchTypeGeo;

            [_searchManager suggestWithText:query window:boundingBox searchOptions:options responseHandler:^(NSArray<YMKSuggestItem *> *suggestItems, NSError *error) {
                NSMutableArray* suggestResult = [[NSMutableArray alloc]init];
                unsigned long suggestionsSize = MIN(5, [suggestItems count]);

                for (int i = 0; i < suggestionsSize; i++) {
                    [suggestResult addObject:@{
                        @"value": [[[suggestItems objectAtIndex:i] title] text],
                        @"searchText": [[suggestItems objectAtIndex:i] searchText]
                    }];
                }

                NSMutableDictionary* resultObject = [[NSMutableDictionary alloc]init];
                [resultObject setValue:suggestResult forKey:@"suggestions"];

                self.onSuggestionsFetch(resultObject);
            }];
        }
    }];
}

- (void) navigateToRegion:(NSDictionary *)region isAnimated:(BOOL)isAnimated {
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        double latitude = [[region objectForKey:@"latitude"] doubleValue];
        double longitude = [[region objectForKey:@"longitude"] doubleValue];

        YMKPoint* point = [YMKPoint pointWithLatitude:latitude longitude:longitude];

        float zoom = self.map.mapWindow.map.cameraPosition.zoom;
        YMKCameraPosition* cameraPos = [YMKCameraPosition cameraPositionWithTarget:point zoom:zoom azimuth:0 tilt:0];

        if (isAnimated) {
            YMKAnimation* animation = [YMKAnimation animationWithType:YMKAnimationTypeSmooth duration:1];
            [self.map.mapWindow.map moveWithCameraPosition:cameraPos animationType:animation cameraCallback:nil];
        } else {
            [self.map.mapWindow.map moveWithCameraPosition:cameraPos];
        }
    }];
}

- (void) navigateToBoundingBox:(NSDictionary *)northEastRegion southWestRegions:(NSDictionary *)southWestRegions {
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        double neLatitude = [[northEastRegion objectForKey:@"latitude"] doubleValue];
        double neLongitude = [[northEastRegion objectForKey:@"longitude"] doubleValue];

        YMKPoint* northEastPoint = [YMKPoint pointWithLatitude:neLatitude longitude:neLongitude];

        double swLatitude = [[southWestRegions objectForKey:@"latitude"] doubleValue];
        double swLongitude = [[southWestRegions objectForKey:@"longitude"] doubleValue];

        YMKPoint* southWestPoint = [YMKPoint pointWithLatitude:swLatitude longitude:swLongitude];

        YMKBoundingBox* boundingBox = [YMKBoundingBox boundingBoxWithSouthWest:southWestPoint northEast:northEastPoint];
        @try {
            YMKCameraPosition* cameraPosition = [self.map.mapWindow.map cameraPositionWithBoundingBox:boundingBox];
            [self.map.mapWindow.map moveWithCameraPosition:cameraPosition];
        } @catch (NSException *exception) {
            NSLog(@"Navigate To Bounding Box error");
        }
    }];
}

- (YMKPoint*) getDeviceLocation
{
    YMKPoint* lastUserLocation = self.map.mapWindow.map.userLocationLayer.cameraPosition.target;
    YMKPoint* point = [YMKPoint pointWithLatitude:lastUserLocation.latitude longitude:lastUserLocation.longitude];

    return point;
}


- (void)onObjectAddedWithView:(YMKUserLocationView *)view
{
    [view.pin setIconWithImage: _userLocationIcon];
    [view.arrow setIconWithImage: _userLocationIcon];
    view.accuracyCircle.fillColor = UIColor.clearColor;
}


- (BOOL)onMapObjectTapWithMapObject:(YMKMapObject *)mapObject point:(YMKPoint *)point {
    if ([mapObject isKindOfClass:[YMKPlacemarkMapObject class]] || [mapObject isKindOfClass:[YMKPolygonMapObject class]]) {
        NSMutableDictionary* resultObject = [[NSMutableDictionary alloc]init];
        id userData = [mapObject userData];

        if (userData != nil) {
            @try {
                [resultObject setValue:userData forKey:@"data"];
            } @catch (NSException *exception) {
                //TODO: Solve the error
            }
        }

        [resultObject setValue:[NSNumber numberWithDouble:[point latitude]] forKey:@"latitude"];
        [resultObject setValue:[NSNumber numberWithDouble:[point longitude]] forKey:@"longitude"];

        if ([mapObject isKindOfClass:[YMKPlacemarkMapObject class]]) {
            self.onMarkerPress(resultObject);
        } else {
            self.onPolygonPress(resultObject);
        }

        return true;
    }
    return false;
}

- (void)onObjectRemovedWithView:(YMKUserLocationView *)view { }


- (void)onObjectUpdatedWithView:(YMKUserLocationView *)view event:(YMKObjectEvent *)event { }


-(NSDictionary*) cameraPositionToJSON:(YMKCameraPosition*) position {
    NSDictionary* addressDict = @{
        @"azimuth": [NSNumber numberWithFloat:position.azimuth],
        @"tilt": [NSNumber numberWithFloat:position.tilt],
        @"zoom": [NSNumber numberWithFloat:position.zoom],
        @"point": @{
                @"latitude": [NSNumber numberWithDouble:position.target.latitude],
                @"longitude": [NSNumber numberWithDouble:position.target.longitude],
        },
    };
    
    return addressDict;
}

- (void)onCameraPositionChangedWithMap:(nonnull YMKMap *)map
                        cameraPosition:(nonnull YMKCameraPosition *)cameraPosition
                    cameraUpdateSource:(YMKCameraUpdateSource)cameraUpdateSource
                              finished:(BOOL)finished {
    if (self.onCameraPositionChange) {
        if (finished) {
            self.onCameraPositionChange([self cameraPositionToJSON:cameraPosition]);
        }
    }

    _zoom = cameraPosition.zoom;

    [[UIApplication sharedApplication] sendAction:@selector(resignFirstResponder) to:nil from:nil forEvent:nil];
}


- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    NSLog(@"locationManager didFailWithError: %@", error.localizedDescription);

    self.onLocationError(@{@"error": error.localizedDescription});
}

@end
