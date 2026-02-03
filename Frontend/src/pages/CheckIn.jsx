import "./CheckIn.css";
import Navbar from "../components/Navbar";
import { useState, useEffect } from "react";
import api from "../services/api";
import { useNavigate, useLocation } from "react-router-dom";

export default function CheckIn() {
  const navigate = useNavigate();
  const location = useLocation(); 
  
  // 1. Check if we are checking in for a specific challenge
  const challengeData = location.state || {}; 
  const { challengeId, challengeName } = challengeData;

  const [formData, setFormData] = useState({
    distance: "",
    time: "",
    notes: ""
  });

  const [loading, setLoading] = useState(false);
  const [history, setHistory] = useState([]);
  const [stats, setStats] = useState({ streak: 0 });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await api.get("/dashboard/stats");
        setStats({ streak: response.data.streak });
      } catch (error) {
        console.error("Failed to fetch stats", error);
      }
    };
    fetchStats();
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const response = await api.get("/daily-checkins/history");
      setHistory(response.data);
    } catch (error) {
      console.error("Failed to load history", error);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // ✅ VALIDATION: Check if fields are empty or invalid
    // We use formData.distance and formData.time because that is where your state is stored
    if (!formData.distance || !formData.time || parseFloat(formData.distance) <= 0 || parseInt(formData.time) <= 0) {
      alert("Please enter a valid Distance and Time before checking in!");
      setLoading(false);
      return; // ❌ STOP HERE. Do not send request.
    }

    try {
      const response = await api.post("/daily-checkins", {
        challengeId: challengeId || null, 
        distance: parseFloat(formData.distance),
        time: parseInt(formData.time),
        notes: formData.notes
      });

      alert(`Checked in successfully! You earned ${response.data.pointsEarned} points.`);
      
      fetchHistory(); 
      const statsRes = await api.get("/dashboard/stats");
      setStats({ streak: statsRes.data.streak });
      
      // Clear form
      setFormData({ distance: "", time: "", notes: "" });

      // Redirect back to challenge if applicable
      if (challengeId) {
          navigate(`/challenges/${challengeId}`);
      }

    } catch (error) {
      console.error("Check-in failed", error);
      const msg = error.response?.data?.message || "Failed to check in.";
      alert(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <main className="checkin-page">
        
        <div className="back-nav" onClick={() => navigate("/dashboard")}>
          ← Back to Dashboard
        </div>

        <header className="checkin-header">
          <h1>{challengeId ? `Log Activity for "${challengeName}"` : "Daily Check-In"}</h1>
          <p>Track your progress and keep your streak alive!</p>
        </header>

        <div className="checkin-layout">
          <section className="checkin-left">
            <div className="date-card">
              📅 {new Date().toLocaleDateString()}
              <span>⏰ Don’t forget to check in today</span>
            </div>

            <div className="checkin-form-card">
              <h3>🏆 Log Your Activity</h3>
              <form onSubmit={handleSubmit}> {/* ✅ Wrapped in form tag for better browser handling */}
                
                <label>Distance (km) *</label>
                <input 
                  name="distance" 
                  type="number" 
                  placeholder="e.g. 5.0"
                  value={formData.distance} 
                  onChange={handleChange}
                  required // 👈 Forces browser validation
                  min="0.1" // 👈 Prevents zero or negative
                  step="0.1" // 👈 Allows decimals
                />
                
                <label>Time (minutes) *</label>
                <input 
                  name="time" 
                  type="number" 
                  placeholder="e.g. 30"
                  value={formData.time} 
                  onChange={handleChange}
                  required // 👈 Forces browser validation
                  min="1"  // 👈 Prevents zero or negative
                />
                
                <label>Notes (Optional)</label>
                <textarea 
                  name="notes" 
                  placeholder="How did it feel?"
                  value={formData.notes} 
                  onChange={handleChange}
                />
                
                <button className="submit-btn" type="submit" disabled={loading}>
                  {loading ? "Checking In..." : (challengeId ? "Submit Challenge Entry" : "Submit Check-In")}
                </button>
              </form>
            </div>

            <div className="history-card">
              <h3>Your Recent Activity</h3>
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Distance</th>
                    <th>Time</th>
                    <th>Points</th>
                  </tr>
                </thead>
                <tbody>
                  {history.length > 0 ? (
                    history.map((h, i) => (
                      <tr key={i}>
                        <td>{h.checkinDate}</td>
                        <td>{h.distance ? `${h.distance} km` : "-"}</td>
                        <td>{h.timeMinutes ? `${h.timeMinutes} min` : "-"}</td>
                        <td><span className="points-badge">+{h.pointsEarned}</span></td>
                      </tr>
                    ))
                  ) : (
                    <tr><td colSpan="4" style={{textAlign:"center"}}>No check-ins yet. Start today!</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </section>

          <aside className="checkin-right">
            <div className="streak-card">
              <h4>🔥 Current Streak</h4>
              <h1>{stats.streak}</h1>
              <p>Days in a row!</p>
            </div>
          </aside>
        </div>
      </main>
    </>
  );
}