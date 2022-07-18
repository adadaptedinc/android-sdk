# AdAdapted Android SDK

The Android SDK integrates AdAdapted services with partner Android apps.

Development is done using Android Studio. Updating the public facing API should be done with care since those changes will require more than a drop-in update from partners.

Documentation for integrating the SDK with an App can be found at [https://docs.adadapted.com/#/docs/android-getting-started](https://docs.adadapted.com/#/docs/android-getting-started)

A valid API key is required to be able to run the SDK which can be dropped into the testing application in TestApplication.java.

### Prerequisites

* Android Studio with recent Android SDK versions installed

### Installing

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the **end** of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency (based on latest release version)

	dependencies {
	        implementation 'com.gitlab.adadapted:android_sdk:2.3.7'
	}

## Running the tests

Unit tests can be run within the IDE and will report coverage on the gradle logs / merge request details. There is also a test app that can run basic implementation and verification of features.

## Deployment

To create a new release, it must be named as only the version number (i.e. 1.0.0). Once the new release is published, it will be available through the Jitpack repository shortly afterward.

## Built With

* [Google Play Services](https://developers.google.com/android/guides/overview) - Specifically the Ad library for access to device Advertiser information
* [Volley](https://github.com/google/volley) - HTTP library
* [LeakCanary](https://square.github.io/leakcanary/) - Memory leak detection

## Versioning

SDK version is maintained in the BuildConfig and Build.Gradle. Each new rounds of updates should increment the appropriate values based on the significance of the update

The value is updated from right to left based on this loose criteria:
* Bug fixes and minor tweaks
* Small feature additions or refactor
* Major feature additions or refactor


## Acknowledgments

* [README Template](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)
