import { useEffect, useState } from "react";
import api from "../../api/axiosInstance";

const empty = { model: "", economySeats: "", businessSeats: "" };

function ManageAircraft() {
  const [aircraft, setAircraft] = useState([]);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = () => api.get("/aircraft").then((res) => setAircraft(res.data));
  useEffect(() => { load(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); setSuccess("");
    const payload = { model: form.model, economySeats: Number(form.economySeats), businessSeats: Number(form.businessSeats) };
    try {
      if (editId) { await api.put(`/aircraft/${editId}`, payload); setSuccess("Aircraft updated"); }
      else { await api.post("/aircraft", payload); setSuccess("Aircraft created"); }
      setForm(empty); setEditId(null); load();
    } catch (err) { setError(err.response?.data?.message || "Failed"); }
  };

  const handleEdit = (a) => { setForm({ model: a.model, economySeats: a.economySeats, businessSeats: a.businessSeats }); setEditId(a.id); };
  const handleDelete = async (id) => { await api.delete(`/aircraft/${id}`); load(); };

  return (
    <div className="page-container">
      <h2 className="page-title">Manage Aircraft</h2>

      <div className="card" style={{ marginBottom: 24 }}>
        <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>{editId ? "Edit" : "Add"} Aircraft</h3>
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}
        <form onSubmit={handleSubmit}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 16 }}>
            <div className="form-group">
              <label className="form-label">Model</label>
              <input className="form-input" name="model" value={form.model} onChange={handleChange} placeholder="Boeing 737" required />
            </div>
            <div className="form-group">
              <label className="form-label">Economy Seats</label>
              <input className="form-input" name="economySeats" type="number" value={form.economySeats} onChange={handleChange} placeholder="150" required />
            </div>
            <div className="form-group">
              <label className="form-label">Business Seats</label>
              <input className="form-input" name="businessSeats" type="number" value={form.businessSeats} onChange={handleChange} placeholder="20" required />
            </div>
          </div>
          <div style={{ display: "flex", gap: 10, marginTop: 8 }}>
            <button className="btn btn-primary" type="submit">{editId ? "Update" : "Create"} Aircraft</button>
            {editId && <button className="btn btn-outline" type="button" onClick={() => { setForm(empty); setEditId(null); }}>Cancel</button>}
          </div>
        </form>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>All Aircraft</h3>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ borderBottom: "2px solid var(--border)" }}>
              {["Model", "Economy", "Business", "Total", "Actions"].map((h) => (
                <th key={h} style={{ padding: "10px 12px", textAlign: "left", color: "var(--text-muted)", fontWeight: 600 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {aircraft.map((a) => (
              <tr key={a.id} style={{ borderBottom: "1px solid var(--border)" }}>
                <td style={{ padding: "10px 12px", fontWeight: 600 }}>{a.model}</td>
                <td style={{ padding: "10px 12px" }}>{a.economySeats}</td>
                <td style={{ padding: "10px 12px" }}>{a.businessSeats}</td>
                <td style={{ padding: "10px 12px" }}>{a.totalSeats}</td>
                <td style={{ padding: "10px 12px" }}>
                  <button className="btn btn-outline btn-sm" style={{ marginRight: 8 }} onClick={() => handleEdit(a)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(a.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ManageAircraft;