import { useEffect, useState } from "react";
import { useParams, useNavigate, useSearchParams } from "react-router-dom";
import api from "../api/axiosInstance";

export default function BookingPage() {
  const { flightId } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const seatIdsParam = searchParams.get("seatIds");
  const selectedSeatIds = seatIdsParam ? seatIdsParam.split(",").map(Number) : [];

  const [seats, setSeats] = useState([]);
  const [passengers, setPassengers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [booking, setBooking] = useState(null);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (selectedSeatIds.length === 0) {
      navigate(`/flights/${flightId}/seats`);
      return;
    }
    api.get(`/flights/${flightId}/seats`)
      .then((res) => {
        setSeats(res.data || []);
        setPassengers(selectedSeatIds.map((seatId) => ({ seatId, passengerName: "", passengerAge: "" })));
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="page-container"><p>Loading...</p></div>;
  }

  const updatePassenger = (index, field, value) => {
    const updated = passengers.slice();
    updated[index] = { ...updated[index], [field]: value };
    setPassengers(updated);
  };

  const handleHold = () => {
    setError("");
    setSubmitting(true);
    api.post("/bookings/hold", { flightId: Number(flightId), passengers })
      .then((res) => setBooking(res.data))
      .catch((err) => {
        const details = err?.response?.data?.details;
        const message = err?.response?.data?.message;
        setError(details ? details.join(", ") : message || "Booking failed");
      })
      .finally(() => setSubmitting(false));
  };

  const handleConfirm = () => {
    if (!booking) return;
    setError("");
    setSubmitting(true);
    api.post(`/bookings/${booking.id}/confirm`)
      .then((res) => setBooking(res.data))
      .catch((err) => setError(err?.response?.data?.message || "Confirmation failed"))
      .finally(() => setSubmitting(false));
  };

  const downloadTicket = () => {
    if (!booking) return;
    api.get(`/bookings/${booking.id}/ticket`, { responseType: "blob" }).then((res) => {
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `ticket_${booking.pnr}.pdf`);
      document.body.appendChild(link);
      link.click();
    });
  };

  const totalSeatsLabel = (id) => {
    const seat = seats.find((s) => s.id === id);
    return seat ? seat.seatNumber : "?";
  };

  return (
    <div className="page-container">
      <h2 className="page-title">
        {booking ? "Booking Status" : "Passenger Details"}
      </h2>

      {error && <div className="alert alert-error">{error}</div>}

      {!booking && (
        <>
          {passengers.map((p, i) => (
            <div className="passenger-card" key={p.seatId}>
              <div className="passenger-card-header">
                <strong>Passenger {i + 1}</strong>
                <span className="badge badge-pending">Seat {totalSeatsLabel(p.seatId)}</span>
              </div>
              <div className="input-row">
                <div className="form-group">
                  <label className="form-label">Full Name</label>
                  <input
                    className="form-input"
                    placeholder="As per ID"
                    value={p.passengerName}
                    onChange={(e) => updatePassenger(i, "passengerName", e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Age</label>
                  <input
                    className="form-input"
                    type="number"
                    placeholder="Age"
                    value={p.passengerAge}
                    onChange={(e) => updatePassenger(i, "passengerAge", Number(e.target.value))}
                  />
                </div>
              </div>
            </div>
          ))}

          <button className="btn btn-primary btn-block" onClick={handleHold} disabled={submitting}>
            {submitting ? "Holding seats..." : "Hold Seats →"}
          </button>
        </>
      )}

      {booking && booking.status === "PENDING" && (
        <div className="card">
          <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
            <span className="badge badge-pending">PENDING</span>
            <span style={{ fontSize: 13, color: "var(--text-muted)" }}>
              Expires at {new Date(booking.lockExpiresAt).toLocaleTimeString()}
            </span>
          </div>
          <p style={{ fontSize: 14, color: "var(--text-muted)" }}>PNR</p>
          <p className="pnr-display" style={{ fontSize: 24, marginBottom: 16 }}>{booking.pnr}</p>
          <p style={{ fontSize: 18, fontWeight: 700, marginBottom: 16 }}>
            Total: ₹{booking.totalAmount}
          </p>
          <button className="btn btn-primary btn-block" onClick={handleConfirm} disabled={submitting}>
            {submitting ? "Processing payment..." : "Pay & Confirm Booking"}
          </button>
        </div>
      )}

      {booking && booking.status === "CONFIRMED" && (
        <div className="card confirmation-box">
          <div className="confirmation-icon">✓</div>
          <h3>Booking Confirmed!</h3>
          <p style={{ color: "var(--text-muted)", marginTop: 4 }}>Your PNR</p>
          <div className="pnr-display">{booking.pnr}</div>
          <div style={{ display: "flex", gap: 12, justifyContent: "center", marginTop: 24 }}>
            <button className="btn btn-secondary" onClick={downloadTicket}>Download Ticket (PDF)</button>
            <button className="btn btn-outline" onClick={() => navigate("/my-bookings")}>View My Bookings</button>
          </div>
        </div>
      )}
    </div>
  );
}