
import { useEffect, useState, useCallback } from "react";

const API = "http://localhost:8080/api";

const categoryDetails = {
  Medical: {
    icon: "✚",
    description: "Hospitals, clinics and labs",
    service: "Doctor visits",
  },
  Banking: {
    icon: "♜",
    description: "Branch counters and loans",
    service: "Cash and accounts",
  },
  Government: {
    icon: "▤",
    description: "Offices and certificates",
    service: "Documents and IDs",
  },
  Education: {
    icon: "⚑",
    description: "Schools and colleges",
    service: "Admissions and fees",
  },
  Others: {
    icon: "▦",
    description: "Any other service",
    service: "More services",
  },
};

const OPERATOR_ROLES = ["STAFF", "PROVIDER", "ADMIN"];

function getStoredToken() {
  // Supports common storage keys used by login implementations.
  return (
    localStorage.getItem("token") ||
    localStorage.getItem("jwtToken") ||
    sessionStorage.getItem("token") ||
    sessionStorage.getItem("jwtToken") ||
    ""
  );
}

function authHeaders(includeJson = false) {
  const headers = {};

  if (includeJson) {
    headers["Content-Type"] = "application/json";
  }

  const token = getStoredToken();

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

async function readResponse(response) {
  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      data.message ||
        data.error ||
        `Request failed (${response.status})`
    );
  }

  return data;
}

function getStatusColor(status) {
  switch (status) {
    case "WAITING":
      return "#b45309";
    case "SERVING":
      return "#15803d";
    case "COMPLETED":
      return "#475569";
    case "CANCELLED":
      return "#b91c1c";
    default:
      return "#334155";
  }
}

function getStatusMessage(status) {
  switch (status) {
    case "WAITING":
      return "Your token is in the queue. Please wait for your turn.";
    case "SERVING":
      return "It is your turn! Please proceed to the service counter.";
    case "COMPLETED":
      return "Your service has been completed.";
    case "CANCELLED":
      return "This queue entry has been cancelled.";
    default:
      return "Check your queue status for updates.";
  }
}

function Dashboard({ user, onLogout }) {
  const [categories, setCategories] = useState([]);
  const [services, setServices] = useState([]);
  const [professionals, setProfessionals] = useState([]);
  const [queueEntries, setQueueEntries] = useState([]);

  const [message, setMessage] = useState("Loading categories...");
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [queueMessage, setQueueMessage] = useState("");
  const [queueError, setQueueError] = useState("");
  const [showMyQueue, setShowMyQueue] = useState(false);
  const [loadingQueue, setLoadingQueue] = useState(false);
  const [joining, setJoining] = useState(false);
  const [actionLoading, setActionLoading] = useState(null);

  const roleName = String(
    user?.roleName || user?.role?.roleName || user?.role || ""
  )
    .replace(/^ROLE_/, "")
    .toUpperCase();

  const canOperate = OPERATOR_ROLES.includes(roleName);

  // Load each resource independently so one failure does not
  // prevent the categories from appearing.
  useEffect(() => {
    let cancelled = false;

    async function loadData() {
      try {
        const response = await fetch(`${API}/categories`);
        const data = await readResponse(response);

        if (!cancelled) {
          setCategories(Array.isArray(data) ? data : []);
          setMessage(data.length ? "" : "No categories available.");
        }
      } catch (error) {
        if (!cancelled) {
          setMessage(error.message || "Unable to load categories.");
        }
      }

      try {
        const response = await fetch(`${API}/services`, {
          headers: authHeaders(),
        });
        const data = await readResponse(response);

        if (!cancelled) {
          setServices(Array.isArray(data) ? data : []);
        }
      } catch (error) {
        console.error("Services request failed:", error);
      }

      try {
        const response = await fetch(`${API}/professionals`, {
          headers: authHeaders(),
        });
        const data = await readResponse(response);

        if (!cancelled) {
          setProfessionals(Array.isArray(data) ? data : []);
        }
      } catch (error) {
        console.error("Professionals request failed:", error);
      }
    }

    loadData();

    return () => {
      cancelled = true;
    };
  }, []);

  const loadMyQueue = useCallback(async () => {
    if (!user?.id) {
      setQueueError("User ID is missing. Please log in again.");
      return;
    }

    setLoadingQueue(true);
    setQueueError("");

    try {
      const response = await fetch(
        `${API}/queue/user/${user.id}`,
        { headers: authHeaders() }
      );

      const data = await readResponse(response);

      setQueueEntries(Array.isArray(data) ? data : []);
      setShowMyQueue(true);
    } catch (error) {
      setQueueError(
        error.message || "Unable to load your queue entries."
      );
    } finally {
      setLoadingQueue(false);
    }
  }, [user?.id]);

  useEffect(() => {
    if (!user?.id) return;

    loadMyQueue();

    const intervalId = setInterval(loadMyQueue, 10000);

    return () => clearInterval(intervalId);
  }, [user?.id, loadMyQueue]);

  async function handleJoinQueue(service) {
    if (!user?.id) {
      setQueueMessage("Please log in again before joining a queue.");
      return;
    }

    setJoining(true);
    setQueueMessage("");
    setQueueError("");

    try {
      const response = await fetch(`${API}/queue`, {
        method: "POST",
        headers: authHeaders(true),
        body: JSON.stringify({
          userId: user.id,
          serviceId: service.id,
        }),
      });

      const result = await readResponse(response);

      setQueueMessage(
        `Successfully joined the queue! Token number: ${
          result.tokenNumber ?? result.token ?? "Created"
        }. Status: ${result.status || "WAITING"}.`
      );

      await loadMyQueue();
    } catch (error) {
      setQueueMessage(error.message || "Unable to join the queue.");
    } finally {
      setJoining(false);
    }
  }

  async function handleCancelQueue(entry) {
    if (!window.confirm(`Cancel token #${entry.tokenNumber}?`)) {
      return;
    }

    setActionLoading(entry.id);
    setQueueError("");

    try {
      // This assumes the backend supports DELETE /api/queue/{id}.
      const response = await fetch(`${API}/queue/${entry.id}`, {
        method: "DELETE",
        headers: authHeaders(),
      });

      await readResponse(response);
      setQueueMessage(`Token #${entry.tokenNumber} cancellation requested.`);
      await loadMyQueue();
    } catch (error) {
      setQueueError(
        error.message ||
          "Cancellation failed. Check the backend cancellation endpoint."
      );
    } finally {
      setActionLoading(null);
    }
  }

  async function handleOperatorAction(entry, action) {
    if (!canOperate) {
      setQueueError("You do not have permission to operate the queue.");
      return;
    }

    setActionLoading(entry.id);
    setQueueError("");

    try {
      // These paths must match your backend controller mappings.
      const endpoint =
        action === "NEXT"
          ? `${API}/queue/${entry.id}/call-next`
          : `${API}/queue/${entry.id}/complete`;

      const response = await fetch(endpoint, {
        method: "PATCH",
        headers: authHeaders(),
      });

      await readResponse(response);
      setQueueMessage(
        action === "NEXT"
          ? "Queue advanced successfully."
          : `Token #${entry.tokenNumber} marked as completed.`
      );

      await loadMyQueue();
    } catch (error) {
      setQueueError(
        error.message ||
          "Queue action failed. Check the backend endpoint and permissions."
      );
    } finally {
      setActionLoading(null);
    }
  }

  const selectedCategoryName = categories.find(
    (category) => category.id === selectedCategory
  )?.name;

  const visibleServices =
    selectedCategoryName === "Medical"
      ? services.filter((service) => service.active)
      : [];

  return (
    <div className="dashboard-page">
      <header className="topbar">
        <div className="brand">
          <span className="brand-icon">✓</span>
          <span>Queueless</span>
        </div>

        <div className="profile">
          <span className="avatar">
            {(user?.fullName || "U").charAt(0).toUpperCase()}
          </span>
          <span className="profile-name">{user?.fullName}</span>
          <button className="logout-button" onClick={onLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="dashboard-content">
        <section className="hero">
          <div className="hero-text">
            <span className="eyebrow">YOUR TIME MATTERS</span>
            <h1>Welcome, {user?.fullName}!</h1>
            <p>
              Select a service category to get started. Book your
              spot and skip the line.
            </p>

            <div className="hero-stats">
              <div>
                <strong>{categories.length}</strong>
                <span>Service categories</span>
              </div>
              <div>
                <strong>Live</strong>
                <span>Queue updates</span>
              </div>
              <div>
                <strong>Easy</strong>
                <span>Queue booking</span>
              </div>
            </div>
          </div>

          <div className="hero-art">
            <div className="hero-logo">
              <span className="logo-line long"></span>
              <span className="logo-line medium"></span>
              <span className="logo-line short"></span>
              <span className="logo-check">✓</span>
            </div>
            <strong>Queueless</strong>
            <span>Smart Queue Management</span>
          </div>
        </section>

        <section className="category-section">
          <div className="section-heading">
            <div>
              <h2>Service categories</h2>
              <p>Choose where you need to be seen.</p>
            </div>
          </div>

          {message && (
            <p className="dashboard-message" role="status">
              {message}
            </p>
          )}

          <div className="category-grid">
            {categories.map((category) => {
              const details = categoryDetails[category.name] || {
                icon: "▦",
                description: category.description || "Explore available services",
                service: "View services",
              };

              return (
                <button
                  type="button"
                  className={`category-card ${
                    selectedCategory === category.id ? "selected" : ""
                  }`}
                  key={category.id}
                  onClick={() => {
                    setSelectedCategory(category.id);
                    setQueueMessage("");
                  }}
                >
                  <span className="category-icon">{details.icon}</span>
                  <h3>{category.name}</h3>
                  <p>{category.description || details.description}</p>
                  <span className="category-link">
                    {details.service} →
                  </span>
                </button>
              );
            })}
          </div>

          {selectedCategory !== null && (
            <div className="selection-panel">
              <div>
                <h3>{selectedCategoryName} selected</h3>
                <p>
                  {selectedCategoryName === "Medical"
                    ? "Choose an available medical service below."
                    : "Services for this category are not connected yet."}
                </p>
              </div>

              <button
                type="button"
                className="secondary-button"
                onClick={() => setSelectedCategory(null)}
              >
                Change selection
              </button>
            </div>
          )}

          {selectedCategoryName === "Medical" && (
            <section style={{ marginTop: "24px" }}>
              <h2>Available Medical Services</h2>

              {visibleServices.length === 0 && (
                <p>No active medical services are available.</p>
              )}

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
                  gap: "20px",
                  marginTop: "16px",
                }}
              >
                {visibleServices.map((service) => {
                  const professional = professionals.find(
                    (person) => person.id === service.professionalId
                  );

                  return (
                    <div
                      key={service.id}
                      style={{
                        background: "#ffffff",
                        border: "1px solid #e2e8f0",
                        borderRadius: "14px",
                        padding: "22px",
                        boxShadow: "0 4px 14px rgba(15, 23, 42, 0.06)",
                      }}
                    >
                      <h3 style={{ marginTop: 0 }}>{service.name}</h3>
                      <p>{service.description}</p>

                      <p>
                        <strong>Doctor:</strong>{" "}
                        {service.professionalName ||
                          professional?.fullName ||
                          professional?.name ||
                          "Professional not found"}
                      </p>

                      {(service.specialization ||
                        professional?.specialization) && (
                        <p>
                          <strong>Specialization:</strong>{" "}
                          {service.specialization ||
                            professional?.specialization}
                        </p>
                      )}

                      <p>
                        <strong>Duration:</strong>{" "}
                        {service.estimatedDurationMinutes} minutes
                      </p>

                      <p>
                        <strong>Fee:</strong> ₹
                        {Number(service.baseFee || 0).toFixed(2)}
                      </p>

                      <button
                        type="button"
                        disabled={joining}
                        onClick={() => handleJoinQueue(service)}
                        style={{
                          width: "100%",
                          padding: "12px",
                          border: "none",
                          borderRadius: "8px",
                          background: joining ? "#94a3b8" : "#2563eb",
                          color: "#ffffff",
                          fontWeight: "bold",
                          cursor: joining ? "not-allowed" : "pointer",
                        }}
                      >
                        {joining ? "Please wait..." : "Join Queue"}
                      </button>
                    </div>
                  );
                })}
              </div>

              {queueMessage && (
                <p
                  role="status"
                  style={{
                    marginTop: "18px",
                    padding: "14px",
                    borderRadius: "8px",
                    background: "#eff6ff",
                    color: "#1e40af",
                  }}
                >
                  {queueMessage}
                </p>
              )}
            </section>
          )}
        </section>

        <section
          style={{
            marginTop: "28px",
            padding: "24px",
            background: "#ffffff",
            border: "1px solid #e2e8f0",
            borderRadius: "14px",
          }}
        >
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              gap: "16px",
              flexWrap: "wrap",
            }}
          >
            <div>
              <h2 style={{ margin: "0 0 8px" }}>My Queue</h2>
              <p style={{ margin: 0, color: "#64748b" }}>
                View your tokens and current queue status.
              </p>
            </div>

            <button
              type="button"
              onClick={loadMyQueue}
              disabled={loadingQueue}
              style={{
                padding: "11px 18px",
                border: "none",
                borderRadius: "8px",
                background: "#2563eb",
                color: "#ffffff",
                fontWeight: "bold",
                cursor: loadingQueue ? "not-allowed" : "pointer",
              }}
            >
              {loadingQueue ? "Loading..." : "View / Refresh Queue"}
            </button>
          </div>

          {queueError && (
            <p role="alert" style={{ color: "#b91c1c" }}>
              {queueError}
            </p>
          )}

          {queueMessage && (
            <p role="status" style={{ color: "#166534" }}>
              {queueMessage}
            </p>
          )}

          {showMyQueue && !loadingQueue && queueEntries.length === 0 && (
            <p>You have no queue entries yet.</p>
          )}

          {showMyQueue && queueEntries.length > 0 && (
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(230px, 1fr))",
                gap: "16px",
                marginTop: "22px",
              }}
            >
              {queueEntries.map((entry) => {
                const service = services.find(
                  (item) => item.id === entry.serviceId
                );

                const busy = actionLoading === entry.id;
                const cancellable = ["WAITING"].includes(entry.status);

                return (
                  <div
                    key={entry.id}
                    style={{
                      padding: "18px",
                      border: "1px solid #e2e8f0",
                      borderRadius: "12px",
                      background: "#f8fafc",
                    }}
                  >
                    <p style={{ margin: "0 0 8px", color: "#64748b" }}>
                      Token Number
                    </p>

                    <h2 style={{ margin: "0 0 14px", fontSize: "32px" }}>
                      #{entry.tokenNumber}
                    </h2>

                    <p>
                      <strong>Service:</strong>{" "}
                      {service?.name || `Service ${entry.serviceId}`}
                    </p>

                    <p>
                      <strong>Status:</strong>{" "}
                      <span
                        style={{
                          color: getStatusColor(entry.status),
                          fontWeight: "bold",
                        }}
                      >
                        {entry.status}
                      </span>
                    </p>

                    <p style={{ color: "#64748b", fontSize: "14px" }}>
                      {getStatusMessage(entry.status)}
                    </p>

                    {entry.status === "WAITING" && (
                      <p>
                        <strong>People ahead:</strong>{" "}
                        {entry.peopleAhead ?? 0}
                      </p>
                    )}

                    <p style={{ fontSize: "13px", color: "#64748b" }}>
                      Booked:{" "}
                      {entry.createdAt
                        ? new Date(entry.createdAt).toLocaleString()
                        : "Not available"}
                    </p>

                    {cancellable && (
                      <button
                        type="button"
                        disabled={busy}
                        onClick={() => handleCancelQueue(entry)}
                        style={{
                          marginTop: "8px",
                          width: "100%",
                          padding: "10px",
                          border: "1px solid #dc2626",
                          borderRadius: "8px",
                          background: "#fff",
                          color: "#b91c1c",
                          cursor: busy ? "not-allowed" : "pointer",
                        }}
                      >
                        {busy ? "Please wait..." : "Cancel Queue"}
                      </button>
                    )}

                    {canOperate && (
                      <div
                        style={{
                          marginTop: "14px",
                          paddingTop: "12px",
                          borderTop: "1px solid #e2e8f0",
                        }}
                      >
                        <p style={{ fontSize: "12px", color: "#64748b" }}>
                          OPERATOR ACTIONS · {roleName}
                        </p>

                        {entry.status === "WAITING" && (
                          <button
                            type="button"
                            disabled={busy}
                            onClick={() => handleOperatorAction(entry, "NEXT")}
                            style={{
                              width: "100%",
                              padding: "10px",
                              marginBottom: "8px",
                              border: "none",
                              borderRadius: "8px",
                              background: "#0f766e",
                              color: "#fff",
                              cursor: busy ? "not-allowed" : "pointer",
                            }}
                          >
                            Call Next
                          </button>
                        )}

                        {entry.status === "SERVING" && (
                          <button
                            type="button"
                            disabled={busy}
                            onClick={() =>
                              handleOperatorAction(entry, "COMPLETE")
                            }
                            style={{
                              width: "100%",
                              padding: "10px",
                              border: "none",
                              borderRadius: "8px",
                              background: "#15803d",
                              color: "#fff",
                              cursor: busy ? "not-allowed" : "pointer",
                            }}
                          >
                            Complete Service
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </section>

        <section className="queue-banner">
          <div>
            <h3>Ready to save your time?</h3>
            <p>Choose a category and get ready to join a service queue.</p>
          </div>
          <span className="queue-banner-icon">↗</span>
        </section>

        <footer className="dashboard-footer">
          Queueless · Smart Queue Management System
        </footer>
      </main>
    </div>
  );
}

export default Dashboard;
