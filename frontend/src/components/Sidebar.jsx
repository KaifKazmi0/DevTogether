
import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  ClipboardList,
  ChartNoAxesCombined,
  ListTodo,
  Code2
} from "lucide-react";

function Sidebar({ isOpen, onClose }) {
  const navigation = [
    {
      name: "Overview",
      path: "/",
      icon: LayoutDashboard
    },
    {
      name: "Work Logs",
      path: "/logs",
      icon: ClipboardList
    },
    {
      name: "Statistics",
      path: "/statistics",
      icon: ChartNoAxesCombined
    },
    {
      name: "Tasks",
      path: "/tasks",
      icon: ListTodo
    }
  ];

  return (
    <aside className={`sidebar ${isOpen ? "sidebar-open" : ""}`}>
      <div className="sidebar-brand">
        <Code2 size={26} />
        <h2>DevTogether</h2>
      </div>

      <div className="sidebar-section">
        <p className="sidebar-label">WORKSPACE</p>

        <nav className="sidebar-nav">
          {navigation.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink
               key={item.path}
               to={item.path}
               end={item.path === "/"}
               onClick={onClose}
               className={({ isActive }) =>
                 isActive ? "nav-link active" : "nav-link"
                }
              >
                <Icon size={19} />
                <span>{item.name}</span>
              </NavLink>
            );
          })}
        </nav>
      </div>

      <div className="sidebar-user">
        <div className="user-avatar">K</div>
        <div>
          <p className="user-name">Kaif</p>
          <p className="user-role">Developer</p>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;
