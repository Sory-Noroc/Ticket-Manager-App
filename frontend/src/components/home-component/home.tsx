import { Link } from 'react-router-dom'
import "./home.css"

function Home() {
    return (
        <section className="home">
            <div className="hero">
                <h1 id="heroText">Are you tired of losing your event tickets?</h1>
                <div style={{ marginTop: 40, marginBottom: 10 }}>
                    <button style={{ marginRight: 20 }}>Log In</button>
                    <button className="secondary">Sign In</button>
                </div>
                <Link to="/guest" style={{ textDecoration: 'none' }}>Continue as guest</Link>
                <div>
                    <img width="200" src="/tickets.png" alt="tickets" />
                </div>
            </div>

            <div className="testimonials">
                
            </div>
        </section>
    )
}

export default Home