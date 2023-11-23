
# Sound Arena Mobile App

Sound Arena is a mobile application that offers a music reccomendtation. This app integrates with a web platform, enabling users to access their music and preferences from both mobile and web interfaces.

## Features

- User Registration: Sign up to start listening, with input validation for email and password requirements.
- User Login: Secure login functionality with email and password verification.
- Interactive UI: Modern and user-friendly interface designed for an engaging music experience.

## Technical Details

- **UserService:** Interface for user registration and login using Retrofit.
- **RetrofitClient:** Configuration of Retrofit client for network requests.
- **User, SignupResponse, LoginResponse:** Data classes for managing user information and responses.
- **RegisterAct:** Activity handling user registration with input validation.
- **LoginAct:** Activity for user login.
- **mainpage:** Main activity for the app after successful login or registration. (Will be updated)
