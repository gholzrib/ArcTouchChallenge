ArcTouchChallenge

Developed by Gunther Ribak

BUILD INSTRUCTIONS

1 - Install the release APK 
	- Go to the folder "app", located at ArcTouchChallenge\app 
	- Copy the file "app-release.apk" to your phone.
	- Find the file on your phone and select it.
	- Choose the option "Install" (you are required to allow APKs outside the Play Store to be installed on your device)
	- Enjoy

2 - Run the app with Android Studio
	- Open the ArcTouchChallenge project on Android Studio
	- Plug your phone to the computer (the USB Debugging Mode must be enable)
	- Select the option "Run" on Android Studio (play icon located on the toolbar)
	- Enjoy
	
THIRD-PARTY LIBRARIES

- OkHttp (http://square.github.io/okhttp/)

	HTTP client implementation with multiple benefits and simple application. Was used to simplify the requests needed to acquire the data.

- Picasso (http://square.github.io/picasso/)

	This library provides a great number of tools to handle images (like the Callback used in this project to remove the ProgressBar once the image is downloaded), while cache the required images efficiently. 

- Gson (https://github.com/google/gson)

	This library was used to simplify the creation of custom objects from JSON files, reducing time and number of lines required to develop the app.
	