function StatCard({ title, value, subtitle, icon: Icon }) {
  return (
    <div className="stat-card">
      <div className="stat-card-header">
        <span>{title}</span>
        {Icon && <Icon size={20} />}
      </div>

      <h2>{value}</h2>

      <p>{subtitle}</p>
    </div>
  );
}

export default StatCard;