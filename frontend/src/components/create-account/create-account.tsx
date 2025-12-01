import {Link} from "react-router-dom"
import "./create-account.css"

export default function CreateAccount() {
    return (
        <>
            <div className="create-account">
                <h1>Create Account</h1>

                <form action="">
                    <label>
                        Username:
                        <input type="text" id="first" name="first"
                            placeholder="Enter your Username" required />
                    </label>
                    <label>
                        Password:
                        <input type="password" id="password" name="password"
                            placeholder="Enter your Password" required />
                    </label>

                    <label>
                        Email: 
                        <input type="email" id="email" name="email"
                            placeholder="Enter Email" required />
                    </label>

                    <div className="wrap">
                        <button type="submit">
                            Request Account
                        </button>
                    </div>
                </form>

                <div style={{marginTop: "15px"}}>
                    <p>Already registered?</p>
                    <Link to="/login" style={{ textDecoration: 'none', color: '#4CAF50' }}>
                        Login
                    </Link>
                </div>
            </div>
        </>
    )
}

