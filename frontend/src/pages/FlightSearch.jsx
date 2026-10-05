import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosInstance";

function formatTime(dt) {
  return new Date(dt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}
function formatDate(dt) {
  return new Date(dt).toLocaleDateString([], { weekday: "short", day: "numeric", month: "short" });
}

function FlightSearch() {
  const [source, setSource] = useState("");
  const [destination, setDestination] = useState("");
  const [date, setDate] = useState("");
  const [flights, setFlights] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    setSearched(true);
    try {
      const res = await api.get("/flights/search", { params: { source, destination, date } });
      setFlights(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Search failed");
      setFlights([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="search-hero">
        <h1>Find Your Next Flight</h1>
        <p>Search and book flights to destinations across the country</p>
      </div>

      <form onSubmit={handleSearch} className="search-form">
        <div className="form-group">
          <label className="form-label">From</label>
          <input
            className="form-input"
            placeholder="e.g. DEL"
            value={source}
            onChange={(e) => setSource(e.target.value.toUpperCase())}
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">To</label>
          <input
            className="form-input"
            placeholder="e.g. BOM"
            value={destination}
            onChange={(e) => setDestination(e.target.value.toUpperCase())}
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">Date</label>
          <input
            className="form-input"
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            required
          />
        </div>
        <button className="btn btn-primary" type="submit" disabled={loading}>
          {loading ? "Searching..." : "Search Flights"}
        </button>
      </form>

      <div className="page-container">
        {error && <div className="alert alert-error">{error}</div>}

        {searched && !loading && flights.length === 0 && !error && (
          <div className="empty-state">
            <h3>No flights found</h3>
            <p>Try a different route or date.</p>
          </div>
        )}

        {flights.map((f) => (
          <div key={f.id} className="flight-card">
            <div>
              <span className="flight-number-badge">{f.flightNumber}</span>
              <div className="flight-route" style={{ marginTop: 10 }}>
                {f.route.sourceAirport.code}
                <span className="arrow">✈</span>
                {f.route.destinationAirport.code}
              </div>
              <div className="flight-meta">
                {f.route.sourceAirport.city} → {f.route.destinationAirport.city}
              </div>
              <div className="flight-meta">
                {formatDate(f.departureTime)} · Departs {formatTime(f.departureTime)} → Arrives {formatTime(f.arrivalTime)}
              </div>
            </div>
            <div style={{ textAlign: "right" }}>
              <div className="flight-price">
                ₹{f.basePrice}
                <span>starting price</span>
              </div>
              <button
                className="btn btn-secondary btn-sm"
                style={{ marginTop: 10 }}
                onClick={() => navigate(`/flights/${f.id}/seats`)}
              >
                Select Seats
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default FlightSearch;