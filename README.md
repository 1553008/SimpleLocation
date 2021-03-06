# SimpleLocation
SimpleLocation is all about Map and inspired by Google Map, it is created as a purpose for studying Android. Key feature that makes our app prominent is Nearest Street Food Vendor.

## Demo
<div align="center">
  <a href="https://www.youtube.com/watch?v=QPNvd-no4ok&t=7s"><img src="https://preview.ibb.co/mbSacn/image.png" alt="image" border="0"></a>
</div>

## Screenshots

<p display="inline" margin="auto">
   <a href="https://ibb.co/nvaBHn"><img src="https://preview.ibb.co/mcQYV7/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/icMwiS"><img src="https://preview.ibb.co/cMzSq7/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/khjMHn"><img src="https://preview.ibb.co/c6UXOS/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/ghK5cn"><img src="https://preview.ibb.co/bLyLA7/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/gvzriS"><img src="https://preview.ibb.co/ejoTV7/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/eQcYxn"><img src="https://preview.ibb.co/gLN0cn/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/bYByV7"><img src="https://preview.ibb.co/mCtCq7/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/dEQWiS"><img src="https://preview.ibb.co/fn143S/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/gBRMiS"><img src="https://preview.ibb.co/di6MiS/image.png" alt="image" border="0" width="150"></a>
  <a href="https://ibb.co/byPwHn"><img src="https://preview.ibb.co/nQ8dV7/image.png" alt="image" border="0" width="150"></a>
</p>

## Install

* Clone the project
* Build project with Android Studio

**Note:** *If you have any issue while building this project, you can contact us through this email: lhdung1997@gmail.com*

## Development Environment

* Compatible with Android 5.0 and above
* Android code mainly written in Java, some written in Kotlin. In future, we will convert all code into Kotlin because of its advantages
* Cloud Functions are written in NodeJs with version specified by Google Cloud Function
* Android Studio 3.0.1

## Features:
* Login with Facebook.
* Searching places. Places near user's current location will be showed with highest priority.
* Finding directions, users can choose and see information between 2 travel modes: bus and driving. Supports up to 23 waypoints
* Finding nearest food street vendors based on user's current locations and food name. Users can find directions to food shop and see shop's information including ratings, reviews, photos, adresses. Users are enable to leave reviews and ratings.
* Intuitive, Colorful UI.
* Smooth, Fast, Stable UX.

## Applied Techniques

* Handling Rest API calls with Retrofit
* Implementing OnScrollListener for RecyclerView to achieve "just in time" data loading
* K-d tree for spatial index of geographic locations
* Use Queue as an underlying algorithm to query k-nearest-locations
* Model-View-Controller model and Dependency Injection with Butterknife
* Managing database with Firebase, Firestore
* Managing our Rest API with Google Cloud Function

## External Libraries and Frameworks

* [Material Drawer by mikepenz](https://github.com/mikepenz/MaterialDrawer) - UI Library
* [SlidingUp Panel by sothree](https://github.com/umano/AndroidSlidingUpPanel) - UI Library
* [Tagview by Cutta](https://github.com/Cutta/TagView) - UI Library
* [MaterialRatingBar](https://github.com/DreaminginCodeZH/MaterialRatingBar) - UI Library
* [Recyclerview Animators](https://github.com/wasabeef/recyclerview-animators) - UI Library
* [Google Progress Bar](https://github.com/jpardogo/GoogleProgressBar) - UI Library
* [Android Material App Rating](https://github.com/stepstone-tech/android-material-app-rating) - UI Library
* [Picasso](https://github.com/square/picasso) - Loading Images Library
* [ButterKnife](http://jakewharton.github.io/butterknife/) - Dependency Injection Library
* [Retrofit](http://square.github.io/retrofit/) - HTTP Network Communication Library
* [GSON](https://github.com/google/gson) - Convert JSON to Java Object and otherwise.
* [GeoKdbush](https://github.com/mourner/geokdbush) - The fastest spatial index for geographic locations in JavaScript
* Google Firebase
* Google Firestore
* Google Cloud Functions
* Google Map API
* Facebook Login 

* [FuzzyWuzzy](https://github.com/xdrop/fuzzywuzzy) - String Searching Library

## Authors:

* **Dung H.Lam** - *Analyst, Design Architect, Developer*- **Junior at Information Technology, University of Science, HCM**, Vietnam
* **Khanh Q.Nguyen** - *Design Architect, Developer, Quality Control* - **Junior at Information Technology, University of Science, HCM, Vietnam**

## Acknowledgements:

* Thanks **Mr. Ho Tuan Thanh** for guiding us through this project
* Thanks **Foody** for amazing food shop data
* Inspired by **Google Map** application

## Contribution:

Any contribution, pull request is welcome. If you want to use our project for any purpose, please contact our email: *lhdung1997@gmail.com*

