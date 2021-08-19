# BookOnAShelf
<!-- ABOUT THE PROJECT -->
## About The Project 
Book On A Shelf allows users to scan ISBN barcodes inorder to save the books details in their account (aka their shelf). Their account is created using their google email via google authentication. After storing books on their "shelf", they can then  delete them if they want. Users are also able to find friends who also have the app and persue through their shelf of books. Details (title, publsher, publishing date, description, pagecount, thumbnail, preview link) of all books are from google books database. At the moment the user cannot buy books from google books store but the potential for this creativity is there for the future. 


<!-- LIST OF DEPENDENCIES -->
## List of dependencies:
* implementation 'androidx.appcompat:appcompat:1.2.0'
* implementation 'com.google.android.material:material:1.2.1'
* implementation 'androidx.constraintlayout:constraintlayout:2.0.1'
* implementation 'com.android.volley:volley:1.1.1'

* implementation 'com.squareup.picasso:picasso:2.71828'

* implementation platform('com.google.firebase:firebase-bom:28.3.0')
* implementation 'com.google.firebase:firebase-analytics'
* implementation 'com.google.firebase:firebase-database'

* implementation 'com.google.firebase:firebase-auth'
* implementation 'com.google.android.gms:play-services-auth:19.2.0'
* implementation 'androidx.annotation:annotation:1.2.0'
* implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.3.1'
* implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1'

* testImplementation 'junit:junit:4.+'
* androidTestImplementation 'androidx.test.ext:junit:1.1.2'
* androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
* implementation 'com.google.mlkit:barcode-scanning:16.2.0'
* implementation 'com.google.android.gms:play-services-mlkit-barcode-scanning:16.2.0'
* def camerax_version = "1.0.0"

## CameraX core library using camera2 implementation
* implementation "androidx.camera:camera-camera2:$camerax_version"

## CameraX Lifecycle Library
* implementation "androidx.camera:camera-lifecycle:$camerax_version"

## CameraX View class
* implementation "androidx.camera:camera-view:1.0.0-alpha24"
* implementation 'com.dynamsoft:dynamsoftbarcodereader:8.4.1@aar'
* implementation 'com.dynamsoft:dynamsoftcameraenhancer:1.0.1@aar'
* implementation(name: 'DynamsoftBarcodeReaderAndroid', ext: 'aar')
* implementation(name: 'DynamsoftCameraEnhancerAndroid', ext: 'aar')

<!-- INSTRUCTIONS FOR SETTING UP THE APP -->
## Instructions for setting up the app: 

* The user will download this app via the Google Play Store
* Next they will have to either have a gmail account or create a gmail acount in order to log in. 
* The app will be able to recognize that account from there on out
* If the user would like to create another shelf, at this moment, the user will need to use another account to create a different shelf. 
