import { Bell, Search, Menu } from "lucide-react";

function Navbar({ onMenuClick, isSidebarOpen }) {
  return (
    <header className="navbar">
      <button
        className="mobile-menu-button"
        onClick={onMenuClick}
        aria-expanded={isSidebarOpen}
        aria-label={
          isSidebarOpen
            ? "Close navigation menu"
            : "Open navigation menu"
        }
      >
  <Menu size={22} />
</button>

      <div className="navbar-search">
        <Search size={18} />
        <input
          type="text"
          placeholder="Search anything..."
        />
      </div>

      <div className="navbar-actions">
        <button className="notification-button" aria-label="Notifications">
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