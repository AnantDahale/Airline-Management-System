import { useEffect, useState } from "react";
import api from "../api/axiosInstance";

function badgeClass(status) {
  if (status === "CONFIRMED") return "badge badge-confirmed";
  if (status === "PENDING") return "badge badge-pending";
  return "badge badge-cancelled";
}

function MyBookings() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadBookings = () => {
    api.get("/bookings/my").then((res) => {
      setBookings(res.data);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadBookings();
  }, []);

  const cancelBooking = async (id) => {
    await api.post(`/bookings/${id}/cancel`);
    loadBookings();
  };

  const downloadTicket = async (id, pnr) => {
    const res = await api.get(`/bookings/${id}/ticket`, { responseType: "blob" });
    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `ticket_${pnr}.pdf`);
    document.body.appendChild(link);
    link.click();
  };

  if (loading) {
    return <div className="page-container"><p>Loading...</p></div>;
  }

  return (
    <div className="page-container">
      <h2 className="page-title">My Bookings</h2>

      {bookings.length === 0 && (
        <div className="empty-state">
          <h3>No bookings yet</h3>
          <p>Search for a flight to get started.</p>
        </div>
      )}

      {bookings.map((b) => (
        <div className="card" key={b.id}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 10 }}>
            <span className={badgeClass(b.status)}>{b.status}</span>
            <span style={{ fontWeight: 700, color: "var(--primary)" }}>PNR: {b.pnr}</span>
          </div>

          <div className="flight-route" style={{ fontSize: 18 }}>
            {b.flight.route.sourceAirport.code}
            <span className="arrow">✈</span>
            {b.flight.route.destinationAirport.code}
          </div>
          <div className="flight-meta">
            {b.flight.flightNumber} · {new Date(b.flight.departureTime).toLocaleString()}
          </div>
          <div style={{ marginTop: 10, fontWeight: 600 }}>Total: ₹{b.totalAmount}</div>

          <div style={{ display: "flex", gap: 10, marginTop: 14 }}>
            {b.status === "CONFIRMED" && (
              <>
                <button className="btn btn-outline btn-sm" onClick={() => downloadTicket(b.id, b.pnr)}>
                  Download Ticket
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => cancelBooking(b.id)}>
                  Cancel Booking
                </button>
              </>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

export default MyBookings;