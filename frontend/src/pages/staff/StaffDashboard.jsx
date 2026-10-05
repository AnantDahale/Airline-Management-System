import { useEffect, useState } from "react";
import api from "../../api/axiosInstance";

function StaffDashboard() {
  const [flights, setFlights] = useState([]);
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [manifest, setManifest] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api.get("/flights").then((res) => setFlights(res.data));
  }, []);

  const viewManifest = async (flight) => {
    setSelectedFlight(flight);
    setLoading(true);
    const res = await api.get(`/staff/flights/${flight.id}/manifest`);
    setManifest(res.data);
    setLoading(false);
  };

  const updateStatus = async (flightId, status) => {
    await api.patch(`/flights/${flightId}/status?status=${status}`);
    const res = await api.get("/flights");
    setFlights(res.data);
    if (selectedFlight?.id === flightId) {
      setSelectedFlight((prev) => ({ ...prev, status }));
    }
  };

  return (
    <div className="page-container-wide">
      <h2 className="page-title">Staff Dashboard</h2>

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 24 }}>
        <div>
          <div className="card">
            <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>Flights</h3>
            {flights.map((f) => (
              <div
                key={f.id}
                style={{
                  padding: "12px 16px",
                  borderRadius: 8,
                  border: selectedFlight?.id === f.id ? "2px solid var(--primary)" : "1.5px solid var(--border)",
                  marginBottom: 10,
                  cursor: "pointer",
                  background: selectedFlight?.id === f.id ? "#eff6ff" : "white",
                }}
                onClick={() => viewManifest(f)}
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div>
                    <span style={{ fontWeight: 700, color: "var(--primary)" }}>{f.flightNumber}</span>
                    <span style={{ margin: "0 8px", color: "var(--text-muted)" }}>
                      {f.route.sourceAirport.code} → {f.route.destinationAirport.code}
                    </span>
                  </div>
                  <span className={`badge ${f.status === "SCHEDULED" ? "badge-confirmed" : f.status === "CANCELLED" ? "badge-cancelled" : "badge-pending"}`}>
                    {f.status}
                  </span>
                </div>
                <div style={{ fontSize: 12, color: "var(--text-muted)", marginTop: 4 }}>
                  {new Date(f.departureTime).toLocaleString()}
                </div>
                <div style={{ marginTop: 10 }}>
                  <label style={{ fontSize: 12, color: "var(--text-muted)", marginRight: 8 }}>Update Status:</label>
                  <select
                    value={f.status}
                    onChange={(e) => { e.stopPropagation(); updateStatus(f.id, e.target.value); }}
                    onClick={(e) => e.stopPropagation()}
                    style={{ padding: "4px 8px", borderRadius: 6, border: "1px solid var(--border)", fontSize: 13 }}
                  >
                    {["SCHEDULED", "DELAYED", "BOARDING", "DEPARTED", "LANDED", "CANCELLED"].map((s) => (
                      <option key={s} value={s}>{s}</option>
                    ))}
                  </select>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div>
          <div className="card">
            <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>
              {selectedFlight ? `Manifest — ${selectedFlight.flightNumber}` : "Select a flight to view manifest"}
            </h3>

            {loading && <p>Loading manifest...</p>}

            {!loading && selectedFlight && manifest.length === 0 && (
              <div className="empty-state" style={{ padding: 20 }}>
                <p>No confirmed passengers yet</p>
              </div>
            )}

            {!loading && manifest.length > 0 && (
              <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 13 }}>
                <thead>
                  <tr style={{ borderBottom: "2px solid var(--border)" }}>
                    {["#", "Passenger", "Seat", "Class", "PNR"].map((h) => (
                      <th key={h} style={{ padding: "8px 10px", textAlign: "left", color: "var(--text-muted)", fontWeight: 600 }}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {manifest.map((m, i) => (
                    <tr key={i} style={{ borderBottom: "1px solid var(--border)" }}>
                      <td style={{ padding: "8px 10px", color: "var(--text-muted)" }}>{i + 1}</td>
                      <td style={{ padding: "8px 10px", fontWeight: 600 }}>
                        {m.passengerName}
                        {m.passengerAge && <span style={{ color: "var(--text-muted)", fontWeight: 400 }}> ({m.passengerAge}y)</span>}
                      </td>
                      <td style={{ padding: "8px 10px" }}>{m.seatNumber}</td>
                      <td style={{ padding: "8px 10px" }}>
                        <span className={`badge ${m.seatClass === "BUSINESS" ? "badge-pending" : "badge-confirmed"}`}>
                          {m.seatClass}
                        </span>
                      </td>
                      <td style={{ padding: "8px 10px", fontFamily: "monospace", fontWeight: 700 }}>{m.pnr}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default StaffDashboard;