# MatchMate

MatchMate is a small Android app written in Kotlin that fetches random user profiles and displays them as “match cards.” Each card shows a person’s name, age, city, education, religion, and a match score. You can swipe down to refresh, accept or decline profiles, and everything you do is saved locally so the app still works even if you lose internet connection. Dependency injection is handled with Hilt to keep the code organized and modular.

---

## Project Setup

1. **Requirements**
   - Android Studio (2020.3.1 or newer)
   - Kotlin plugin (v1.9.0)
   - Java 11 or newer (required by Gradle 8)
   - An Android device or emulator (minimum API 21)

2. **Clone or Download**
   ```bash
   git clone https://github.com/yourusername/MatchMate.git
   cd MatchMate
   ```
   Alternatively, unzip the provided `MatchMate_Hilt_Project.zip`.

3. **Open in Android Studio**
   - Choose **“Open an existing project”** and navigate to the `MatchMate` folder.
   - Let Gradle sync (it will fetch Hilt, Retrofit, Room, Glide, and other dependencies).

4. **Run the App**
   - Connect an Android device or start an emulator.
   - Click **Run ▶** in Android Studio.
   - When the app launches, swipe down to load profiles from the network.

---

## Libraries & Justifications

- **Retrofit 2.9.0 + Gson**
  Used for HTTP requests to `randomuser.me` and parsing JSON into Kotlin data classes. Retrofit makes network calls simple and readable; Gson handles the JSON-to-object conversion.

- **OkHttp Logging Interceptor 5.0.0-alpha.2**
  Logs all HTTP requests and responses. This is very helpful during development, as you can inspect exactly what’s going over the network.

- **Room 2.5.2**
  Serves as the local SQLite database. Room provides compile-time verification of SQL queries, supports Coroutines and Flow, and makes it easy to persist and observe data for offline use.

- **Kotlin Coroutines 1.7.1 & Flow**
  Simplify asynchronous programming. Network calls and database operations run off the main thread, and Flow streams Room data changes into the UI automatically.

- **Glide 4.15.1**
  Efficiently loads and caches images from URLs. If images need to be hidden for any reason, Glide calls can easily be replaced by showing a placeholder with initials.

- **Hilt 2.44**
  Handles dependency injection across the app. By annotating Application, ViewModel, and Modules, Hilt provides singletons (like MatchRepository and MatchDatabase) without manual boilerplate.

- **AndroidX Lifecycle (ViewModel & LiveData 2.6.1)**
  Ensures UI-related data is lifecycle-aware and survives configuration changes. LiveData keeps the activity/fragment in sync with underlying data.

- **Material Components & ConstraintLayout**
  Provide a modern, responsive UI that follows Google’s Material Design guidelines.

---

## Architecture Overview

MatchMate follows the **MVVM** (Model–View–ViewModel) pattern:

1. **Model**
   - **Network Layer**
     - `RandomUserService` (Retrofit interface) fetches JSON from `https://randomuser.me/api/?results=10`.
     - DTOs (`RandomUserDto`, `RandomUserResponse`) match the JSON structure for parsing.
   - **Room Entities** (`UserProfile`) store data in a local SQLite database.
   - **DAO** (`UserDao`) provides methods to insert, update, and query profiles.

2. **Repository** (`MatchRepository`)
   - Fetches data from the network (using Retrofit).
   - Maps API results to `UserProfile` and inserts them into Room.
   - Simulates a 30% chance of network failure for testing error handling.
   - Provides a Flow of stored profiles and functions to update a profile’s accept/decline status.

3. **ViewModel** (`MatchViewModel`)
   - Holds a reference to `MatchRepository` (injected by Hilt).
   - Exposes:
     - `allProfiles: LiveData<List<UserProfile>>` — streams data from Room.
     - `refreshState: LiveData<RefreshResult>` — loading, success, or error states when fetching.
   - Provides methods to refresh data and to mark a profile as accepted or declined.

4. **View Layer**
   - **`MainActivity`** (annotated with `@AndroidEntryPoint`) sets up the RecyclerView, observes LiveData, and handles swipe-to-refresh.
   - **`MatchAdapter`** populates match cards in the RecyclerView, loads images with Glide (or initials if images are hidden), and handles button clicks for accept/decline.

---

## Why We Added Extra Fields (Education & Religion)

The assignment asked for at least two extra fields beyond what the Random User API provides. We chose:

- **Education** (e.g., High School, Bachelor’s, Master’s, PhD)
- **Religion** (e.g., Hindu, Muslim, Christian, Sikh, Other)

In many real-world matchmaking apps, users filter and sort potential matches based on their education level and religious background. Since `randomuser.me` doesn’t supply those fields, the app picks random values from predefined lists for each profile to demonstrate how such fields could be integrated.

---

## How the Match Score Is Calculated

We want a number between 0 and 100 that indicates how well someone matches “you.” For this demo, assume you are:

- **Age**: 30
- **City**: Mumbai

1. **Age Proximity**
   ```kotlin
   ageDifference = abs(30 - candidateAge)
   ageScore = max(0, 100 - (ageDifference * 2))
   ```
   - If someone is also 30, `ageScore` is 100.
   - For each year difference, subtract 2 points.
   - If the difference is 50 years or more, `ageScore` is floored at 0.

2. **City Match**
   ```kotlin
   cityBonus = if (candidateCity.equals("Mumbai", true)) 20 else 0
   ```

3. **Final Score**
   ```kotlin
   matchScore = min(100, ageScore + cityBonus)
   ```
   - Ensures the maximum possible score is 100.

---

## Offline & Error Handling Strategy

1. **Room for Caching**
   - When the app successfully fetches profiles, they’re saved in Room.
   - If you lose internet, the RecyclerView still shows the last saved profiles.

2. **Simulated 30% Failure Rate**
   - Before each network call, we randomly decide to “fail” 30% of the time.
   - This triggers an error state so you can see how the app behaves when the network is unreliable.

3. **User Feedback**
   - While fetching, a spinner (SwipeRefreshLayout) is visible.
   - On success, the spinner hides, and the new profiles appear.
   - On error, the spinner hides and a red error message is shown at the top (e.g., “Simulated network failure (30%)”).

4. **Pull-to-Refresh**
   - If a fetch fails or you’re offline, you can swipe down again to retry.

---

## Reflection & Hypothetical “No-Images” Constraint

- **Reflection**:
  Using MVVM with Hilt made the code easy to follow—data logic lives in the repository, UI logic is in the Activity/Adapter, and ViewModel bridges the two. Room + Flow ensures seamless offline behavior, and Retrofit + Coroutines keep network calls straightforward.

- **No-Images Constraint**:
  If a rule came in that we can’t show profile pictures, we replaced the ImageView in each card with a TextView that displays the person’s initials (e.g., “JD” for “John Doe”) on a circular background. This way, no actual images are used, but there’s still a simple visual identifier.

---
