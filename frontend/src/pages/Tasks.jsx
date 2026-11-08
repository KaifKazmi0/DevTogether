import { ListTodo } from "lucide-react";

function Tasks() {
  return (
    <div className="dashboard">
      <div className="dashboard-heading">
        <h1>Tasks</h1>
        <p>Track project work, assignments, and completion.</p>
      </div>

      <section className="placeholder-card">
        <ListTodo size={34} />
        <h2>Coming soon</h2>
        <p>
          Task cards with Todo, In Progress, and Done statuses, assignees, due
          dates, and progress summaries will live here.
        </p>
      </section>
    </div>
  );
}

export default Tasks;
