# Core Logic Documentation

This document outlines the core functionalities and business logic that power the Umoney app. It covers transaction flow, budget allocation, error handling, categorization, and other essential logic that drives the app’s user experience and performance.

---

## 1. Transaction Flow

### 1.1 First-Time User Setup
When a user starts the app for the first time, they will be guided through a **setup wizard**. The wizard collects essential information such as:
- Name
- Date of Birth (DOB), with automatic age calculation
- Employment type (salaried, business, self-employed, etc.)
- Approximate monthly income and expenses

This information is saved for future reference and can be modified later through the **Settings** page. The app also supports international users, allowing them to choose their preferred **currency type**, with a default set based on their locale.

### 1.2 Navigation and Ledger System
The app uses a **bottom navigation menu** with five tabs:
- **Dashboard**
- **Needs**
- **Wants**
- **Savings**
- **Income**

Each tab (except for the dashboard) has the following structure:
- A **transaction summary card** showing "In" (Income), "Out" (Expenses), and "Hold" (Balance).
- A **RecyclerView** displaying the list of transactions.
- A **"+" or "-" button** for adding or removing funds.
  - **Income Tab**: "+" button to add income entries.
  - **Needs, Wants, and Savings Tabs**: "-" button to deduct funds.

### 1.3 Transaction Details for "-" (Expense)
When users click the "-" button, they can input the following details for a transaction:
1. **Amount**: The expense value.
2. **Date and Time**: Defaults to the current timestamp but can be changed.
3. **Given To**: A list of common recipients, such as contacts, UPI IDs, bank accounts, or merchants like Amazon or Flipkart, is maintained. Suggestions are made based on frequently used recipients.
4. **Category**: A list of predefined categories is available, specific to each tab (e.g., food, rent, groceries for Needs). Users can also create custom categories.
5. **Attachment (optional)**: Users can attach receipts, images, or other relevant files for reference.

### 1.4 Transaction Logic
- **Income Tab**: Funds are added via the "+" button. Once income is received, it can be distributed into the Needs, Wants, and Savings tabs for spending.
- **Needs, Wants, and Savings Tabs**: Funds are deducted via the "-" button for specific expenses (e.g., rent, groceries). The app ensures expenses are taken from the correct ledger.

---

## 2. Budget Allocation (20-30-50 Rule)

Upon initial setup, the app explains the **20-30-50 Rule**, which allocates:
- 50% of income to **Needs**
- 30% to **Wants**
- 20% to **Savings**

Users can modify these percentages during setup or later via the **Settings** page. The app ensures that the sum of the three categories is always **100%**.

---

## 3. Error Handling

The app uses **robust validation** to ensure data integrity. Common examples include:
- **Budget Allocation**: The total percentage for Needs, Wants, and Savings must equal 100%. If the user enters values that do not sum to 100%, an error will prompt them to adjust the inputs.
- **Transaction Entry**: Negative entries are not allowed in income, and incorrect formats or missing fields are validated with prompts.
- **General Data Handling**: Any invalid data or errors in the flow are caught and handled gracefully with appropriate error messages and suggestions for correction.

---

## 4. Categorization of Expenses

Each tab has predefined **categories** to ensure clarity:
- **Needs**: Food, Groceries, Rent, Personal Care, etc.
- **Wants**: Entertainment, Dining Out, etc.
- **Savings**: Emergency Fund, Investments, etc.

Some categories, like **Food**, may appear in multiple tabs, allowing users flexibility. Users can also create **custom categories** to suit their needs.

---

## 5. Manual Entry vs. Automated Entry

### 5.1 Manual Entry
Manual entries follow the same parameters outlined above and are saved directly into the corresponding tab (Needs, Wants, Savings, or Income).

### 5.2 Automated Entry
Automated transactions (via SMS, email, or future API integrations) are initially categorized as **uncategorized** and appear at the bottom of the **Dashboard**. Users must manually assign these entries to the appropriate tab (Needs, Wants, Savings, or Income) before they are finalized.

---

## 6. Real-Time Updates

The app implements **real-time synchronization** of transaction data with the database (Firestore). Data refresh occurs automatically when:
- The user manually enters new transactions.
- Updates happen through automated SMS/email (future development).

---

## 7. Data Synchronization

Best practices are used to ensure **data consistency** and minimize **API calls to Firestore**. Synchronization happens automatically to keep local and remote data in sync, and offline access will be supported with a **caching mechanism**.

---

## 8. Security and User Data Protection

### 8.1 User Data Protection
Each user's financial and personal information is stored in a **secure directory** that only they can access. Users cannot view or interact with each other's data. All sensitive data (e.g., bank details) is **encrypted**.

### 8.2 Authentication and Access Control
The app uses **Google Authentication** to ensure that only the user can access their account. Sessions are protected, and re-authentication is required after session expiration.

---

## 9. Future Development

### 9.1 Investment and Goal Tracking
The manual entry system is a priority, but future updates will include tracking for investments and financial goals. **High-debt loans** will take priority in the tracking system, ensuring users focus on paying off debt before pursuing other goals.

### 9.2 AI and Machine Learning
Future versions will integrate **AI and machine learning** for better predictions of spending patterns and investment suggestions.

---

## 10. Performance Optimizations

Optimizations are applied to minimize **Firestore API calls** and improve app responsiveness. For example, transaction data is retrieved and cached locally where possible, and API calls are batched to reduce overhead.

---

This documentation will continue to evolve as more features are developed and feedback is received from users. 

