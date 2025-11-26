# Google OAuth2 + JWT Authentication Flow

This project implements JWT-based authentication with optional Google OAuth2 login.
---
<img width="1172" height="648" alt="Screenshot (497)" src="https://github.com/user-attachments/assets/6cfcd467-7f44-4d51-ba38-29c31b9289eb" />

<img width="1129" height="628" alt="Screenshot (498)" src="https://github.com/user-attachments/assets/d676def6-77c0-4190-bf1d-d15a2f720b09" />


---

## JWT Flow (Existing)

1. User signs up via `/auth/signup` with username/password.
2. User logs in via `/auth/login`.
3. Backend validates credentials and generates:
    - **Access Token**
    - **Refresh Token**
4. Tokens are returned to the client.
5. Client uses **Access Token** in `Authorization: Bearer <token>` header to access protected endpoints.
6. Use `/auth/refresh` to generate a new access token using the refresh token.

---

## Google OAuth2 Flow

### Endpoints

- **Start Login:** `/oauth2/authorization/google`
- **Callback:** `/login/oauth2/code/google` (handled automatically by Spring)

### Flow Steps

1. Browser or Postman makes a GET request to:
   `http://localhost:8083/oauth2/authorization/google`
---
OR
---
   <img width="662" height="167" alt="Screenshot (495)" src="https://github.com/user-attachments/assets/4e1074f6-209b-4914-8935-089ce4f153e5" />

2. Spring Security redirects the user to the **Google login page**.
3. User logs into Google.
4. Google authenticates the user and redirects back to your backend:

    `http://localhost:8083/login/oauth2/code/google?code=
<TEMP_CODE>`
5. 
5. Spring exchanges the temporary code for user info.
6. Backend checks if the user exists in DB:
- If not, creates a new user with role `USER`.
7. Backend generates **JWT Access Token** and **Refresh Token**.
8. Tokens are returned in JSON response:
```json
{
  "message": "Google login successful",
  "accessToken": "<JWT_ACCESS_TOKEN>",
  "refreshToken": "<JWT_REFRESH_TOKEN>"
}
