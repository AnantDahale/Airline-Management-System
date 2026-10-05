import { useEffect, useState } from "react";
import api from "../../api/axiosInstance";

const empty = { code: "", name: "", city: "", country: "" };

function ManageAirports() {
  const [airports, setAirports] = useState([]);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = () => api.get("/airports").then((res) => setAirports(res.data));
  useEffect(() => { load(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); setSuccess("");
    try {
      if (editId) {
        await api.put(`/airports/${editId}`, form);
        setSuccess("Airport updated successfully");
      } else {
        await api.post("/airports", form);
        setSuccess("Airport created successfully");
      }
      setForm(empty); setEditId(null); load();
    } catch (err) {
      setError(err.response?.data?.message || "Operation failed");
    }
  };

  const handleEdit = (a) => { setForm({ code: a.code, name: a.name, city: a.city, country: a.country }); setEditId(a.id); };
  const handleDelete = async (id) => { await api.delete(`/airports/${id}`); load(); };

  return (
    <div className="page-container">
      <h2 className="page-title">Manage Airports</h2>

      <div className="card" style={{ marginBottom: 24 }}>
        <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>{editId ? "Edit Airport" : "Add Airport"}</h3>
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}
        <form onSubmit={handleSubmit}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
            <div className="form-group">
              <label className="form-label">Code</label>
              <input className="form-input" name="code" value={form.code} onChange={handleChange} placeholder="DEL" required />
            </div>
            <div className="form-group">
              <label className="form-label">Name</label>
              <input className="form-input" name="name" value={form.name} onChange={handleChange} placeholder="Airport name" required />
            </div>
            <div className="form-group">
              <label className="form-label">City</label>
              <input className="form-input" name="city" value={form.city} onChange={handleChange} placeholder="Delhi" />
            </div>
            <div className="form-group">
              <label className="form-label">Country</label>
              <input className="form-input" name="country" value={form.country} onChange={handleChange} placeholder="India" />
            </div>
          </div>
          <div style={{ display: "flex", gap: 10, marginTop: 8 }}>
            <button className="btn btn-primary" type="submit">{editId ? "Update" : "Create"} Airport</button>
            {editId && <button className="btn btn-outline" type="button" onClick={() => { setForm(empty); setEditId(null); }}>Cancel</button>}
          </div>
        </form>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 16, color: "var(--primary)" }}>All Airports</h3>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ borderBottom: "2px solid var(--border)" }}>
              {["Code", "Name", "City", "Country", "Actions"].map((h) => (
                <th key={h} style={{ padding: "10px 12px", textAlign: "left", color: "var(--text-muted)", fontWeight: 600 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {airports.map((a) => (
              <tr key={a.id} style={{ borderBottom: "1px solid var(--border)" }}>
                <td style={{ padding: "10px 12px", fontWeight: 700 }}>{a.code}</td>
                <td style={{ padding: "10px 12px" }}>{a.name}</td>
                <td style={{ padding: "10px 12px" }}>{a.city}</td>
                <td style={{ padding: "10px 12px" }}>{a.country}</td>
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

export default ManageAirports;