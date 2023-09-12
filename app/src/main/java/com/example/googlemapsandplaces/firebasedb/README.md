To use the methods from the `FirebaseDatabase` class in an Android activity or fragment, you need to create an instance of the `FirebaseDatabase` class and call its methods. Here's how you can do it:

1. **Create an Instance of `FirebaseDatabase`**:

   In your activity or fragment, create an instance of the `FirebaseDatabase` class. For example:

   ```java
   FirebaseHelper firebaseHelper = new FirebaseHelper();
   ```

2. **Call Methods**:

   You can now call the methods of the `FirebaseHelper` instance you created to interact with Firebase Realtime Database.

    - **Create Parking**:

      To create a new parking item, you can call the `createParking` method:

      ```java
      Parking newParking = new Parking(/* initialize with parking details */);
      firebaseHelper.createParking(newParking);
      ```

    - **Update Parking**:

      To update an existing parking item, you need the key of the parking item you want to update. Then, call the `updateParking` method:

      ```java
      String parkingKeyToUpdate = /* key of the parking item you want to update */;
      Parking updatedParking = new Parking(/* updated parking details */);
      firebaseHelper.updateParking(parkingKeyToUpdate, updatedParking);
      ```

    - **Delete Parking**:

      To delete a parking item, you need the key of the parking item you want to delete. Then, call the `deleteParking` method:

      ```java
      String parkingKeyToDelete = /* key of the parking item you want to delete */;
      firebaseHelper.deleteParking(parkingKeyToDelete);
      ```

    - **Get Parking by Key**:

      To get a single parking item by its key, call the `getParking` method with the key:

      ```java
      String parkingKeyToRetrieve = /* key of the parking item you want to retrieve */;
      firebaseHelper.getParking(parkingKeyToRetrieve, new FirebaseHelper.OnParkingDataReceivedListener() {
          @Override
          public void onDataReceived(Parking parking) {
              // Handle the retrieved parking data
          }
 
          @Override
          public void onError(String errorMessage) {
              // Handle errors, if any
          }
      });
      ```

    - **Get All Parkings**:

      To retrieve all parking items, call the `getAllParkings` method:

      ```java
      firebaseHelper.getAllParkings(new FirebaseHelper.OnParkingsDataReceivedListener() {
          @Override
          public void onDataReceived(List<Parking> parkings) {
              // Handle the list of parking items
          }
 
          @Override
          public void onError(String errorMessage) {
              // Handle errors, if any
          }
      });
      ```

    - **Search and Sort Parking**:

      To search and sort parking items, use the `searchAndSortParking` method:

      ```java
      String searchQuery = /* the search query */;
      FirebaseHelper.LocationSortOption sortOption = FirebaseHelper.LocationSortOption.DISTANCE; // or TIME
      firebaseHelper.searchAndSortParking(searchQuery, new FirebaseHelper.OnParkingsDataReceivedListener() {
          @Override
          public void onDataReceived(List<Parking> parkings) {
              // Handle the sorted search results
          }
 
          @Override
          public void onError(String errorMessage) {
              // Handle errors, if any
          }
      }, sortOption);
      ```

By following these steps, you can integrate the `FirebaseDatabase` class into your Android activity or fragment and use its methods to interact with Firebase Realtime Database.



ooglemapsandplaces      E  FATAL EXCEPTION: main
Process: com.example.googlemapsandplaces, PID: 3159
com.google.firebase.database.DatabaseException: Class com.example.googlemapsandplaces.Parking does not define a no-argument constructor. If you are using ProGuard, make sure these constructors are not stripped.
at com.google.firebase.database.core.utilities.encoding.CustomClassMapper$BeanMapper.deserialize(CustomClassMapper.java:570)
at com.google.firebase.database.core.utilities.encoding.CustomClassMapper$BeanMapper.deserialize(CustomClassMapper.java:563)
at com.google.firebase.database.core.utilities.encoding.CustomClassMapper.convertBean(CustomClassMapper.java:433)
at com.google.firebase.database.core.utilities.encoding.CustomClassMapper.deserializeToClass(CustomClassMapper.java:232)
at com.google.firebase.database.core.utilities.encoding.CustomClassMapper.convertToCustomClass(CustomClassMapper.java:80)
at com.google.firebase.database.DataSnapshot.getValue(DataSnapshot.java:202)
at com.example.googlemapsandplaces.firebasedb.FirebaseHelper$2.onChildAdded(FirebaseHelper.java:72)
at com.google.firebase.database.core.ChildEventRegistration.fireEvent(ChildEventRegistration.java:79)
at com.google.firebase.database.core.view.DataEvent.fire(DataEvent.java:63)
at com.google.firebase.database.core.view.EventRaiser$1.run(EventRaiser.java:55)
at android.os.Handler.handleCallback(Handler.java:942)
at android.os.Handler.dispatchMessage(Handler.java:99)
at android.os.Looper.loopOnce(Looper.java:226)
at android.os.Looper.loop(Looper.java:313)
at android.app.ActivityThread.main(ActivityThread.java:8757)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:571)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1067)
2023-09-03 03:10:17.344  1411-3163  Debug                   system_server                        D  low && ship && 3rdparty app crash, do not dump
2023-09-03 03:10:17.344  1411-3324  DropBoxManagerService   system_server                  