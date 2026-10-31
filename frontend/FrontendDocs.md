step 1 : 
npm install react-router-dom lucide-react

- react-router-dom — navigation between Dashboard, Login and Register.
- lucide-react — consistent icons throughout the dashboard.


step 2 : 
Build the Dashboard Layout
Our next coding task
Create:

src/components/layout/DashboardLayout.jsx

This component will eventually combine:
- Sidebar
- Navbar
- Main content area using React Router's Outlet
Why Outlet? It lets us reuse the same sidebar and navbar across multiple dashboard pages without duplicating the layout.

building sidebar study this codes ==>
{sidebar.jsx, navbar.jsx,DashboardLayout.jsx}



Note:
There are two common export/import patterns:
Export	Import
export default Dashboard	import Dashboard from "./Dashboard"
export { Dashboard }	import { Dashboard } from "./Dashboard"

They must match.