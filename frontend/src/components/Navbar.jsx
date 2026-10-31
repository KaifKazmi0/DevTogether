
import { Bell, Search } from "lucide-react";

function Navbar() {
  return (
    <header className="navbar">
      <div className="navbar-search">
        <Search size={18} />
        <input
          type="text"
          placeholder="Search anything..."
        />
      </div>

      <div className="navbar-actions">
        <button className="notification-button">
          <Bell size={20} />
        </button>

        <div className="navbar-profile">
          <div className="user-avatar">K</div>
          <span>Kaif</span>
        </div>
      </div>
    </header>
  );
}

export default Navbar;
