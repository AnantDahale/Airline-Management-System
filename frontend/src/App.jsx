import { BrowserRouter, Routes, Route, Link, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import Register from "./pages/Register";
import Login from "./pages/Login";
import FlightSearch from "./pages/FlightSearch";
import SeatSelection from "./pages/SeatSelection";
import BookingPage from "./pages/BookingPage";
import MyBookings from "./pages/MyBookings";
import AdminDashboard from "./pages/admin/AdminDashboard";
import ManageAirports from "./pages/admin/ManageAirports";
import ManageAircraft from "./pages/admin/ManageAircraft";
import ManageFlights from "./pages/admin/ManageFlights";
import StaffDashboard from "./pages/staff/StaffDashboard";

function Nav() {
  const { user, logout } = useAuth();
  return (
    <nav className="navbar">
      <Link to="/search" className="navbar-brand">✈ SkyLine Airlines</Link>
      <div className="navbar-links">
        <Link to="/search">Flights</Link>
        {user?.role === "ADMIN" && <Link to="/admin">Admin</Link>}
        {(user?.role === "STAFF" || user?.role === "ADMIN") && <Link to="/staff">Staff</Link>}
        {user ? (
          <>
            <Link to="/my-bookings">My Bookings</Link>
            <span style={{ fontSize: 14, opacity: 0.9 }}>Hi, {user.name}</span>
            <button className="btn-logout" onClick={logout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">
              <button className="btn btn-primary btn-sm">Sign Up</button>
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Nav />
        <Routes>
          <Route path="/" element={<Navigate to="/search" replace />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/search" element={<FlightSearch />} />
          <Route path="/flights/:flightId/seats" element={<SeatSelection />} />
          <Route path="/flights/:flightId/book" element={<BookingPage />} />
          <Route path="/my-bookings" element={<MyBookings />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/airports" element={<ManageAirports />} />
          <Route path="/admin/aircraft" element={<ManageAircraft />} />
          <Route path="/admin/flights" element={<ManageFlights />} />
          <Route path="/staff" element={<StaffDashboard />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;