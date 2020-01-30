# AdAdapted Android SDK

The Android SDK integrates AdAdapted services with partner Android apps. Development is done using Android Studio. Documentation for integrating the SDK with and App can be found at [https://dev.adadapted.com/android/index.html](https://dev.adadapted.com/android/index.html)

### Prerequisites

* Android Studio with recent Android SDK versions installed

### Installing

Project should be checked out from version control through Android Studio. Project can also be manually checked out into a location

## Running the tests

There are a minimal set of Unit Tests. Typically testing is done through verification in the companion app which acts a reference integration.

## Deployment

Once the master branch is built the resulting .apk file is uploaded to the documentation website along with change log information and any documentation updates that may be necessary.

## Built With

* [Google Play Services](https://developers.google.com/android/guides/overview) - Specifically the Ad library for access to device Advertiser information
* [Volley](https://github.com/google/volley) - HTTP library
* [LeakCanary](https://square.github.io/leakcanary/) - Memory leak detection

## Versioning

SDK version is maintained in the com.adadapted.android.sdk.config.Config class. Each new rounds of updates should increment the appropriate values based on the significance of the update

Typically the value is updates from right to left based on this loose criteria:
* Bug fixes and minor tweaks
* Small feature additions or refactor
* Major feature additions or refactor


## Acknowledgments

* [README Template](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)
