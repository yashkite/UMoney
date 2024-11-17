# Architecture

This document provides an overview of the architecture and core components used in the project. It’s designed to help contributors understand the project's structure, data flow, and key architectural decisions.

## 1. Overall Design Pattern

The project follows the *MVVM (Model-View-ViewModel)* pattern. This design pattern is chosen to separate UI and business logic, making it easier to maintain and test the code. 

- *Current Status*: We are not committed to specific frameworks or libraries yet. As development progresses, we may integrate libraries to streamline MVVM implementation, such as using LiveData, ViewModel, or other Android Jetpack components.

## 2. Modules or Layers

Our app is structured into several logical layers, as follows:

- *UI Layer (View)*: Contains Activities, Fragments, and other UI components. This layer is responsible for rendering data and handling user interactions.
  
- *ViewModel Layer*: The ViewModel holds UI-related data that survives configuration changes. It interacts with the Repository to retrieve and process data for the View layer.
  
- *Data Layer*: Currently, Firestore is used as the primary database to manage user data. The Data layer will evolve to include data caching and offline support as the app develops.

- *Future Enhancements*: We may add separate modules as the app grows, such as modules for specific features or different data sources (e.g., SMS, email, API).

### 2.1 Package Structure

The project follows this package structure:

- **data/**: Contains all data-related components
  - `local/`: Local storage components (Room DB, SharedPreferences)
  - `remote/`: Network and Firebase interactions
  - `model/`: Data models/entities
  - `repository/`: Single source of truth for data operations

- **di/**: Dependency injection modules

- **ui/**: User interface components organized by features
  - Each feature (auth, dashboard, needs, etc.) contains:
    - Activities/Fragments
    - ViewModels
    - Adapters for RecyclerViews

- **utils/**: Helper classes and utility functions

app/src/main/java/com/elececo/umoney/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── entity/
│   │   └── preferences/
│   ├── remote/
│   │   ├── api/
│   │   └── firebase/
│   ├── model/
│   │   ├── Transaction.java
│   │   ├── User.java
│   │   └── Category.java
│   └── repository/
│       ├── TransactionRepository.java
│       └── UserRepository.java
├── di/
│   └── AppModule.java
├── ui/
│   ├── auth/
│   │   ├── GoogleLoginActivity.java
│   │   └── viewmodel/
│   │       └── AuthViewModel.java
│   ├── dashboard/
│   │   ├── DashboardActivity.java
│   │   ├── adapter/
│   │   └── viewmodel/
│   │       └── DashboardViewModel.java
│   ├── needs/
│   │   ├── NeedsFragment.java
│   │   ├── adapter/
│   │   └── viewmodel/
│   │       └── NeedsViewModel.java
│   ├── wants/
│   │   ├── WantsFragment.java
│   │   ├── adapter/
│   │   └── viewmodel/
│   │       └── WantsViewModel.java
│   └── savings/
│       ├── SavingsFragment.java
│       ├── adapter/
│       └── viewmodel/
│           └── SavingsViewModel.java
└── utils/
    ├── Constants.java
    ├── DateUtils.java
    └── CurrencyUtils.java

## 3. Data Flow

Data in this app can enter through two primary means:

- *Manual Entry*: Users can manually input data, which will be prioritized in the initial development phases.
  
- *Automated Detection*: Later on, we will add automation to capture data from external sources such as SMS, email, and APIs.

For now, the ViewModel and Repository components will manage the flow of data between Firestore and the UI layer.

## 4. Navigation

The app will use a combination of *Bottom Navigation* and *Drawer Navigation*:

- *Bottom Navigation: This menu includes tabs for *Dashboard, Needs, Wants, Savings, and Income.
  - Each tab (except Dashboard) will have a uniform data entry screen, allowing users to input related transactions.
  - The Income tab will auto-distribute the entered amount based on preset percentages: 50% for Needs, 30% for Wants, and 20% for Savings. Users can modify these percentages in the *Settings*, accessible via the left-side drawer.

- *Drawer Navigation*: The left-side drawer provides access to settings and other configuration options.

Each screen will have a summary card showing transaction data for that category.

## 5. Error Handling and Logging

To prevent app crashes and improve debugging:

- *Error Checks*: Code should include checks to validate data types and expected formats. For example, when a function expects an integer, code should verify that it’s receiving an integer, not a string or double. 

This approach will help identify issues early and make the app more robust.

## 6. Threading and Concurrency

*Status*: No specific threading or concurrency strategy has been decided yet.

Asynchronous tasks will likely be handled using *coroutines* or *RxJava* to ensure smooth performance. We’ll determine the best approach based on the final project requirements.

## 7. Caching and Data Persistence

The app supports *offline functionality* so that users can still access data without an internet connection. 

- *Syncing with Firestore*: Once the user regains internet access, data changes will sync with Firestore.
  
- *User Profile*: User-specific data, such as profile details and income preferences, are stored in local storage (e.g., SharedPreferences) and will sync with Firestore as needed.

- *Auto Refresh*: Data will refresh automatically if there are updates in Firestore or the user manually enters new data.

## 8. Testing

*Planned Testing Approach*:
- Testing will begin after the manual data entry system is fully implemented.
- We aim to incorporate both manual and automated tests to validate data entry and synchronization with Firestore.
  
Currently, we have not yet selected specific testing tools or frameworks. This section will be updated as we define our testing strategy.

## 9. Security Considerations

*Data Security*: User data security is a priority, especially since the app will be used by multiple individuals.

- *User Authentication*: Google Sign-In is required to access the app. Users cannot proceed without logging in.
  
- *Data Privacy*: Each user’s data is isolated to prevent unauthorized access. Only developers may access user data for testing and development purposes, adhering to data privacy standards.

As the app evolves, additional security measures will be implemented to safeguard user data.