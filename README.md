# urbanpark

In your project's top-level build.gradle file, ensure that Google's Maven repository is included:


`allprojects {
    repositories {
        google()
        // If you're using a version of Gradle lower than 4.1, you must instead use:
        // maven {
        //     url 'https://maven.google.com'
        // }
    }
}`


<br>Then, in your app-level build.gradle file, declare Google Play services as a dependency:


`apply plugin: 'com.android.application'
    ...
    dependencies {
        implementation 'com.google.android.gms:play-services-auth:20.6.0'
    }`

API KEY GAuth = AIzaSyBOuc12c-L41Ddssvbqt_E2SjASk9co1GU (Pretty sure not needed)
FIrebase DB Link = urbanpark-cba35.eur3.firebasedatabase.app
