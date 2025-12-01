import {Link} from "react-router-dom"
export default function GuestChoice() {
    return (
        <div className="main">
                <h1>Guest Mode</h1>
                <h3>Provide an email please:</h3>

                <form action="">
                    <label>
                        Email:
                        <input type="text" id="email" name="email"
                            placeholder="Enter your Email" required />
                    </label>
                    
                    <div className="wrap">
                        <button type="submit">
                            Submit
                        </button>
                    </div>
                </form>

                <div>
                    <p>Not registered?</p>
                    <Link to="/create" style={{ textDecoration: 'none' }}>
                        Create an account
                    </Link>
                </div>
                <div>
                    <Link to="/" style={{ textDecoration: 'none' }}>
                        Home
                    </Link>
                </div>
            </div>
    )
}