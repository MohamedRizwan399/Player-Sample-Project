# Player Sample Project

An Android application to demonstrate the robust features such as Google Authentication, Exoplayer, Api handling using retrofit, AdManager and more using modern \n
Android development techniques such as MVVM and MVP architecture.

## Features

- **User authentication with Google Sign-In**: Users can sign in using their Google accounts for secure access.
- **Dynamic Links**: Handle deep linking with Firebase Dynamic Links.
- **API integration with Retrofit**: Seamless data fetching and API handling using Retrofit.
- **Architecture**:
    - MVVM (Model-View-ViewModel) architecture for clean separation of concerns.
    - MVP (Model-View-Presenter) for modular and testable code structure.
- **User Interface**:
    - **ViewPager2 with Slider** Sliding UI components implemented using ViewPager2 with autoSwiping between content.
    - **BottomSheet** for displaying additional content or actions.
    - **Alert Dialogs** for prompting user actions.
    - **RecyclerView** for displaying lists of data with both vertical and horizontal scrolls.
- **Media Playback**:
    - Integrated **ExoPlayer** for seamless video playback with HLS.
    - **Playback controls** Full media control with playback actions (play, pause, seek, etc.).
    - **Telephony Receiver** Observe incoming calls during playback and handle player state accordingly.
- **Ad Integration**:
    - **AdManager** for displaying banner ads and interstitial ads.
    - **FullScreen Ads** for monetization.
    - **Sample Video Ads** using ad providers to display sample video ad.
- **Swipe-to-Refresh**:
  - **SwipeRefreshLayout** integrated into the home screen to allow users to refresh and reload dynamic content.
- **Subscription Management**: Handle in-app purchases and subscriptions(**Currently not activated due to playConsole deactivation**).
- **Session Management**:
    - Store and manage user Api data using **SharedPreferences**.
    - Check for sign-in status to bypass login if already authenticated.
- **Navigation**:
    - BottomNavigation tab handles user navigation between different fragments.
    - Back stack management for a smooth user experience.
- **Network Monitoring**:
  - **BroadcastReceiver** to observe network changes and update user interface.


## Installation

You need to setup the Android Sdk for proper gradle sync to launch the application.

## Prerequisites
- **Android Studio**: Jellyfish or higher recommended.
- **Gradle**: Version 8.1.0 or higher.
- **Google AdManager**: Configuration AdManager for ads integration.

### Steps
**Step 1:** Clone the repo
```bash
 - https://github.com/MohamedRizwan399/Player-Sample-Project.git
```

**Step 2:** Sync gradle and build the project

**Step 3:** Run the project on an emulator or physical device

