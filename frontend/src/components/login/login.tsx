
import { Link } from 'react-router-dom';
import "../App.css"
import "./login.css"

function Login() {
    return (
        <>
            <div className="login">
                <h1>Login</h1>
                <h3 style={{ fontSize: "16px", marginBottom: "20px", color: "#555" }}>Enter your login credentials</h3>

                <form action="">
                    <label>
                        Username:
                    </label>
                    <input type="text" id="first" name="first"
                        placeholder="Enter your Username" required />

                    <label>
                        Password:
                    </label>
                    <input type="password" id="password" name="password"
                        placeholder="Enter your Password" required />

                    <div className="wrap">
                        <button type="submit">
                            Submit
                        </button>
                    </div>
                </form>

                <div style={{ marginTop: "15px" }}>
                    <p>Not registered?</p>
                    <Link to="/create" style={{ textDecoration: 'none', color: '#4CAF50' }}>
                        Create an account
                    </Link>
                </div>
                <div style={{ marginTop: "15px" }}>
                    <Link to="/guest" style={{ textDecoration: 'none', color: '#4CAF50' }}>
                        Continue as guest
                    </Link>
                </div>
            </div>
        </>
    )
}

export default Login