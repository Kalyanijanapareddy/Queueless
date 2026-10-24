import { useState } from "react";
import "./App.css";
import Dashboard from "./Dashboard";

const API = "http://localhost:8080/api/users";

function App() {
  const [page, setPage] = useState("login");
  const [message, setMessage] = useState("");
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage("");
    setLoading(true);

    const form = new FormData(e.currentTarget);

    const data =
      page === "register"
        ? {
            fullName: form.get("fullName"),
            email: form.get("email"),
            phone: form.get("phone"),
            password: form.get("password"),
          }
        : {
            email: form.get("email"),
            password: form.get("password"),
          };

    try {
      const response = await fetch(
        `${API}/${page === "register" ? "register" : "login"}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(data),
        }
      );

      const result = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          result.detail ||
            result.message ||
            result.error ||
            "Request failed. Please check your details."
        );
      }

      if (page === "register") {
        setMessage("Registration successful! You can now log in.");
        setPage("login");
        setShowPassword(false);
      } else {
        setUser(result);
        setMessage("");
      }
    } catch (error) {
      setMessage(error.message || "Cannot connect to the server.");
    } finally {
      setLoading(false);
    }
  }

  function handleLogout() {
    setUser(null);
    setMessage("");
    setPage("login");
    setShowPassword(false);
  }

  // Keep the dashboard outside the login/register card.
  if (user) {
    return (
      <div className="app">
        <Dashboard user={user} onLogout={handleLogout} />
      </div>
    );
  }

  return (
    <div className="app">
      <div className="card">
        <h1>Queueless</h1>
        <p className="subtitle">Smart Queue Management System</p>

        <h2>{page === "login" ? "Login" : "Create Account"}</h2>

        <form onSubmit={handleSubmit}>
          {page === "register" && (
            <>
              <input
                name="fullName"
                type="text"
                placeholder="Full name"
                autoComplete="name"
                required
              />

              <input
                name="phone"
                type="tel"
                placeholder="Phone number"
                autoComplete="tel"
                required
              />
            </>
          )}

          <input
            name="email"
            type="email"
            placeholder="Email address"
            autoComplete="email"
            required
          />

          <div className="password-field">
            <input
              name="password"
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              autoComplete={
                page === "login" ? "current-password" : "new-password"
              }
              minLength={page === "register" ? 8 : undefined}
              required
            />
            <button
              type="button"
              className="password-toggle"
              onClick={() => setShowPassword((current) => !current)}
              aria-label={showPassword ? "Hide password" : "Show password"}
              aria-pressed={showPassword}
            >
              {showPassword ? "Hide password" : "Show password"}
            </button>
          </div>

          <button type="submit" disabled={loading}>
            {loading
              ? "Please wait..."
              : page === "login"
                ? "Login"
                : "Register"}
          </button>
        </form>

        {message && (
          <p className="message" role="status">
            {message}
          </p>
        )}

        <p className="switch">
          {page === "login"
            ? "Don't have an account? "
            : "Already registered? "}

          <button
            type="button"
            className="link"
            onClick={() => {
              setPage(page === "login" ? "register" : "login");
              setMessage("");
              setShowPassword(false);
            }}
          >
            {page === "login" ? "Register" : "Login"}
          </button>
        </p>
      </div>
    </div>
  );
}

export default App;


