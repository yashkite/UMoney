# UMoney
![image](https://user-images.githubusercontent.com/32860312/169589143-a3cbb6e5-0134-442b-950c-d0530fa232af.png)

## About The Project
UMoney is a comprehensive money management app designed around the **50-30-20 rule** (Needs-Wants-Savings). It combines the best features of investment tracking and manual transaction management to provide a complete financial management solution.

### Key Features
- **Smart Transaction Categorization**: Automatically categorizes expenses into Needs (50%), Wants (30%), and Savings (20%)
- **Multiple Input Methods**: 
  - Manual transaction entry
  - SMS transaction detection (upcoming)
  - Email integration (upcoming)
- **Real-time Synchronization**: Automatic data sync with Firestore
- **Offline Support**: Access your data without internet connection
- **Secure Authentication**: Google Sign-In integration
- **Customizable Categories**: Create and manage your own transaction categories
- **Multi-currency Support**: International usage with locale-based currency settings

### Built With
- Java (Android Native)
- XML (UI Layouts)
- Firebase (Backend)
  - Firestore (Database)
  - Authentication
- MVVM Architecture

## Getting Started

### Prerequisites
1. Android Studio IDE
2. Git for version control
3. Google Firebase account
4. Physical Android device or emulator for testing

### Installation

1. **Fork & Clone**
   ```bash
   git clone https://github.com/yourusername/UMoney.git
   ```

2. **Firebase Setup**
   - Create a new Firebase project
   - Add your Android app to Firebase
   - Download `google-services.json` and place it in the app directory
   - Enable Google Sign-In in Firebase Console

3. **Android Studio Setup**
   - Open the project in Android Studio
   - Sync project with Gradle files
   - Configure your local.properties file

4. **Run the App**
   - Select your target device (physical or emulator)
   - Click "Run" or press Shift + F10

## Project Structure
The project follows MVVM architecture with clear separation of concerns:

