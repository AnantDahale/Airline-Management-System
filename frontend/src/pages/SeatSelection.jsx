import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axiosInstance";

function SeatSelection() {
  const { flightId } = useParams();
  const [seats, setSeats] = useState([]);
  const [selected, setSelected] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    api.get(`/flights/${flightId}/seats`).then((res) => {
      setSeats(res.data);
      setLoading(false);
    });
  }, [flightId]);

  const toggleSeat = (seat) => {
    if (seat.status !== "AVAILABLE") return;
    setSelected((prev) =>
      prev.includes(seat.id) ? prev.filter((id) => id !== seat.id) : [...prev, seat.id]
    );
  };

  const proceedToBook = () => {
    navigate(`/flights/${flightId}/book?seatIds=${selected.join(",")}`);
  };

  const seatClass = (seat) => {
    if (selected.includes(seat.id)) return "seat-btn selected";
    if (seat.status !== "AVAILABLE") return "seat-btn taken";
    if (seat.seatClass === "BUSINESS") return "seat-btn available business";
    return "seat-btn available";
  };

  const businessSeats = seats.filter((s) => s.seatClass === "BUSINESS");
  const economySeats = seats.filter((s) => s.seatClass === "ECONOMY");

  if (loading) {
    return <div className="page-container"><p>Loading seats...</p></div>;
  }

  return (
    <div className="page-container">
      <h2 className="page-title">Select Your Seats</h2>

      <div className="seat-legend">
        <span><span className="legend-box" style={{ background: "white", border: "1.5px solid var(--border)" }}></span>Available</span>
        <span><span className="legend-box" style={{ background: "var(--success)" }}></span>Selected</span>
        <span><span className="legend-box" style={{ background: "#f1f5f9" }}></span>Taken</span>
        <span><span className="legend-box" style={{ background: "white", border: "1.5px solid var(--accent)" }}></span>Business</span>
      </div>

      <div className="card">
        {businessSeats.length > 0 && (
          <>
            <div className="seat-section-title">Business Class</div>
            <div className="seat-grid">
              {businessSeats.map((seat) => (
                <button key={seat.id} className={seatClass(seat)} onClick={() => toggleSeat(seat)} disabled={seat.status !== "AVAILABLE"}>
                  {seat.seatNumber}
                </button>
              ))}
            </div>
          </>
        )}

        <div className="seat-section-title">Economy Class</div>
        <div className="seat-grid">
          {economySeats.map((seat) => (
            <button key={seat.id} className={seatClass(seat)} onClick={() => toggleSeat(seat)} disabled={seat.status !== "AVAILABLE"}>
              {seat.seatNumber}
            </button>
          ))}
        </div>
      </div>

      <div className="booking-summary-bar">
        <div>
          <strong>{selected.length}</strong> seat(s) selected
        </div>
        <button className="btn btn-primary" disabled={selected.length === 0} onClick={proceedToBook}>
          Continue to Passenger Details →
        </button>
      </div>
    </div>
  );
}

export default SeatSelection;