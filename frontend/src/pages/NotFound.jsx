import { Link } from "react-router-dom";
import { ArrowLeft, Compass } from "lucide-react";

function NotFound() {
  return (
    <div className="not-found">
      <Compass size={42} />
      <p className="not-found-code">404</p>
      <h1>Page not found</h1>
      <p>The page you are looking for does not exist or has moved.</p>

      <Link to="/" className="primary-button">
        <ArrowLeft size={16} />
        Back to overview
      </Link>
    </div>
  );
}

export default NotFound;
