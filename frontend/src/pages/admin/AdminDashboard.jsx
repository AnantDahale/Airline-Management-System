import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../api/axiosInstance";

function StatCard({ label, value, color }) {
  return (
    <div className="card" style={{ textAlign: "center", borderTop: `4px solid ${color}` }}>
      <div style={{ fontSize: 32, fontWeight: 800, color }}>{value}</div>
      <div style={{ color: "var(--text-muted)", fontSize: 14, marginTop: 4 }}>{label}</div>
    </div>
  );
}

function AdminDashboard() {
  const [analytics, setAnalytics] = useState(null);

  useEffect(() => {
    api.get("/admin/analytics").then((res) => setAnalytics(res.data));
  }, []);

  if (!analytics) return <div className="page-container"><p>Loading...</p></div>;

  return (
    <div className="page-container-wide">
      <h2 className="page-title">Admin Dashboard</h2>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(160px, 1fr))", gap: 16, marginBottom: 32 }}>
        <StatCard label="Total Bookings" value={analytics.totalBookings} color="var(--primary)" />
        <StatCard label="Confirmed" value={analytics.confirmedBookings} color="var(--success)" />
        <StatCard label="Cancelled" value={analytics.cancelledBookings} color="var(--danger)" />
        <StatCard label="Total Flights" value={analytics.totalFlights} color="var(--accent)" />
        <StatCard label="Total Users" value={analytics.totalUsers} color="#8b5cf6" />
        <StatCard label="Revenue (₹)" value={analytics.totalRevenue?.toLocaleString()} color="#0891b2" />
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 16 }}>
        {[
          { label: "Manage Airports", to: "/admin/airports", icon: "🏢" },
          { label: "Manage Aircraft", to: "/admin/aircraft", icon: "✈️" },
          { label: "Manage Routes", to: "/admin/routes", icon: "🗺️" },
          { label: "Manage Flights", to: "/admin/flights", icon: "📅" },
        ].map((item) => (
          <Link to={item.to} key={item.to} style={{ textDecoration: "none" }}>
            <div className="card" style={{ textAlign: "center", cursor: "pointer", transition: "transform 0.2s" }}
              onMouseEnter={(e) => e.currentTarget.style.transform = "translateY(-3px)"}
              onMouseLeave={(e) => e.currentTarget.style.transform = "translateY(0)"}>
              <div style={{ fontSize: 36, marginBottom: 8 }}>{item.icon}</div>
              <div style={{ fontWeight: 600, color: "var(--primary)" }}>{item.label}</div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}

export default AdminDashboard;