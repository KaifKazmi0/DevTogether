
import { BrowserRouter, Routes, Route } from "react-router-dom";

import DashboardLayout from "./components/layout/DashboardLayout";

import Dashboard from "./pages/Dashboard";
import WorkLogs from "./pages/WorkLogs";
import Statistics from "./pages/Statistics";
import Tasks from "./pages/Tasks";
import NotFound from "./pages/NotFound";
import Login from "./pages/Login";
import Register from "./pages/Register";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route element={<DashboardLayout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/logs" element={<WorkLogs />} />
          <Route path="/statistics" element={<Statistics />} />
          <Route path="/tasks" element={<Tasks />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
