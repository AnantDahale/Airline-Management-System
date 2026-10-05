import { useEffect, useState } from "react";
import api from "../../api/axiosInstance";

const empty = { flightNumber: "", routeId: "", aircraftId: "", departureTime: "", arrivalTime: "", basePrice: "" };

function ManageFlights() {
  const [flights, setFlights] = useState([]);
  const [routes, setRoutes] = useState([]);
  const [aircraft, setAircraft] = useState([]);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = () => api.get("/flights").then((res) => setFlights(res.data));

  useEffect(() => {
    load();
    api.get("/routes").then((res) => setRoutes(res.data));
    api.get("/aircraft").then((res) => setAircraft(res.data));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); setSuccess("");
    const payload = {
      flightNumber: form.flightNumber,
      routeId: Number(form.routeId),
      aircraftId: Number(form.aircraftId),
      departureTime: form.departureTime,
      arrivalTime: form.arrivalTime,
      basePrice: Number(form.basePrice),
    };
    try {
      if (editId) { await api.put(`/flights/${editId}`, payload); setSuccess("Flight updated"); }
      else { await api.post("/flights", payload); setSuccess("Flight created"); }
      setForm(empty); setEditId(null); load();
    } catch (err) {
      const details = err.response?.data?.details;
      setError(details ? details.join(", ") : err.response?.data?.message || "Failed");
    }
  };

  const handleEdit = (f) => {
    setForm({
      flightNumber: f.flightNumber,
      routeId: f.route.id,
      aircraftId: f.aircraft.id,
      departureTime: f.departureTime,
      arrivalTime: f.arrivalTime,
      basePrice: f.basePrice,
    });
    setEditId(f.id);
  };

  const handleDelete = async (id) => { await api.delete(`/flights/${id}`); load(); };

  const updateStatus = async (id, status) => {
    await api.patch(`/flights/${id}/status?status=${status}`);
    load();
  };

  return (
    <div className="page-container-wide">
      <h2 className="page-title">Manage Flights</h2>

      <div className="card" style={{ marginBottom: 24 }}>
        <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>{editId ? "Edit" : "Add"} Flight</h3>
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}
        <form onSubmit={handleSubmit}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 16 }}>
            <div className="form-group">
              <label className="form-label">Flight Number</label>
              <input className="form-input" name="flightNumber" value={form.flightNumber} onChange={handleChange} placeholder="AI101" required />
            </div>
            <div className="form-group">
              <label className="form-label">Route</label>
              <select className="form-input" name="routeId" value={form.routeId} onChange={handleChange} required>
                <option value="">Select route</option>
                {routes.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.sourceAirport.code} → {r.destinationAirport.code}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Aircraft</label>
              <select className="form-input" name="aircraftId" value={form.aircraftId} onChange={handleChange} required>
                <option value="">Select aircraft</option>
                {aircraft.map((a) => (
                  <option key={a.id} value={a.id}>{a.model} ({a.totalSeats} seats)</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Departure Time</label>
              <input className="form-input" name="departureTime" type="datetime-local" value={form.departureTime} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label className="form-label">Arrival Time</label>
              <input className="form-input" name="arrivalTime" type="datetime-local" value={form.arrivalTime} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label className="form-label">Base Price (₹)</label>
              <input className="form-input" name="basePrice" type="number" value={form.basePrice} onChange={handleChange} placeholder="5000" required />
            </div>
          </div>
          <div style={{ display: "flex", gap: 10, marginTop: 8 }}>
            <button className="btn btn-primary" type="submit">{editId ? "Update" : "Create"} Flight</button>
            {editId && <button className="btn btn-outline" type="button" onClick={() => { setForm(empty); setEditId(null); }}>Cancel</button>}
          </div>
        </form>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>All Flights</h3>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ borderBottom: "2px solid var(--border)" }}>
              {["Flight", "Route", "Departure", "Arrival", "Price", "Status", "Actions"].map((h) => (
                <th key={h} style={{ padding: "10px 12px", textAlign: "left", color: "var(--text-muted)", fontWeight: 600 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {flights.map((f) => (
              <tr key={f.id} style={{ borderBottom: "1px solid var(--border)" }}>
                <td style={{ padding: "10px 12px", fontWeight: 700 }}>{f.flightNumber}</td>
                <td style={{ padding: "10px 12px" }}>{f.route.sourceAirport.code} → {f.route.destinationAirport.code}</td>
                <td style={{ padding: "10px 12px" }}>{new Date(f.departureTime).toLocaleString()}</td>
                <td style={{ padding: "10px 12px" }}>{new Date(f.arrivalTime).toLocaleString()}</td>
                <td style={{ padding: "10px 12px" }}>₹{f.basePrice}</td>
                <td style={{ padding: "10px 12px" }}>
                  <select
                    value={f.status}
                    onChange={(e) => updateStatus(f.id, e.target.value)}
                    style={{ padding: "4px 8px", borderRadius: 6, border: "1px solid var(--border)", fontSize: 13 }}
                  >
                    {["SCHEDULED", "DELAYED", "BOARDING", "DEPARTED", "LANDED", "CANCELLED"].map((s) => (
                      <option key={s} value={s}>{s}</option>
                    ))}
                  </select>
                </td>
                <td style={{ padding: "10px 12px" }}>
                  <button className="btn btn-outline btn-sm" style={{ marginRight: 8 }} onClick={() => handleEdit(f)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(f.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ManageFlights;