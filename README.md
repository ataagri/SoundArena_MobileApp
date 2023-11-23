Sound Arena Mobile App

Overview

Sound Arena is a mobile application that offers a streamlined music listening experience. This app integrates with a web platform, enabling users to access their music and preferences from both mobile and web interfaces.

Key Features

User Registration: Sign up to start listening, with input validation for email and password requirements.
User Login: Secure login functionality with email and password verification.
Interactive UI: Modern and user-friendly interface designed for an engaging music experience.
Technical Details

Kotlin Files (.kt)
UserService: Interface for user registration and login using Retrofit.
RetrofitClient: Configuration of Retrofit client for network requests.
User, SignupResponse, LoginResponse: Data classes for managing user information and responses.
RegisterAct: Activity handling user registration with input validation.
LoginAct: Activity for user login.
mainpage: Main activity for the app after successful login or registration.
XML Files (.xml)
loginact.xml: Layout for the login screen, featuring email and password input fields, login button, and navigation to sign up.
registeract.xml: Layout for the registration screen, including fields for email, password, password confirmation, and navigation to log in.
