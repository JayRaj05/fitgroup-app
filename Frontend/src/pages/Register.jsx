import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Register.css";
import gymImage from "../assets/login-image.jpg";

function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // ✅ FIX 1: Send the actual state 'formData', not hardcoded empty strings!
      await register(formData);
      
      // Redirect to login after success
      navigate("/login"); 
    } catch (err) {
      alert("User already exists! Please try logging in.");
    }
  };

  return (
    <div className="register-page">
      <div className="register-wrapper">
        <div className="register-image-card">
          <img src={gymImage} alt="Fitness motivation" />
          <p className="register-quote">
            "Stronger together, one streak at a time."
          </p>
        </div>

        <form className="register-form-card" onSubmit={handleSubmit}>
          <div className="logo">🏋️</div>
          <h2 className="app-name">FitQuest</h2>

          <div className="form-group">
            <label>Full Name</label>
            {/* ✅ FIX 2: Name must be 'fullName' to match your state and backend */}
            <input 
                name="fullName" 
                onChange={handleChange} 
                required 
            />
          </div>

          <div className="form-group">
            <label>Email</label>
            <input name="email" type="email" onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input name="password" type="password" onChange={handleChange} required />
          </div>

          <button className="register-btn">Create Account</button>

          <p className="login-text">
            Already have an account? <a href="/login">Sign in</a>
          </p>
        </form>
      </div>
    </div>
  );
}

export default Register;