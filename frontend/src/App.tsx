import { Link, BrowserRouter, Routes, Route } from 'react-router-dom'
import { useState } from 'react'

import SignIn from './components/sign-in/SignIn'
import Home from "./components/home-component/home"
import GuestChoice from './components/guest-choice'
import CreateAccount from './components/create-account/create-account'
import './App.css'

function App() {
  const [isDarkModeActive, setIsDarkModeActive] = useState(false)

  const switchModes = (darkMode: boolean) => {
    if (darkMode === true) {
      setIsDarkModeActive(false)
    } else if (darkMode === false) {
      setIsDarkModeActive(true)
    }
  }

  return (
    <div className={isDarkModeActive ? "dark-mode" : "light-mode"}>
      <BrowserRouter>
        <nav>
          <Link to="/"><p className="projectTitle">Event Manager</p></Link>
          <div className="links">
            <Link to="/login">Login</Link>
            <Link to="/create">Sign Up</Link>
          </div>
        </nav>

        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<SignIn />} />
            <Route path="/guest" element={<GuestChoice />} />
            <Route path="/create" element={<CreateAccount />} />
          </Routes>
        </main>
      </BrowserRouter>
      <footer>
        <div>
          <p>
            @ All rights reserved. 2025
          </p>
        </div>
      </footer>
    </div>
  )
}

export default App
