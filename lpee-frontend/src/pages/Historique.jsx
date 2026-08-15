import { useEffect, useMemo, useState } from "react";
import { History, Search } from "lucide-react";
import api from "../services/api";
import "./Historique.css";

const actionLabels = {
  Creation: "Création",
  Modification: "Modification",
  Suppression: "Suppression",
};

const displayValue = (value) => value == null || value === "" ? "" : `“${value}”`;

const isEssayDetail = (field = "") => {
  const normalized = field.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLocaleLowerCase("fr");
  return normalized === "essai" || normalized === "numero d’essai" || normalized === "numero essai";
};

function ChangeDetail({ item }) {
  const changes = (item.changements || []).filter((change) => !isEssayDetail(change.champ));
  if (!changes.length) return "—";
  return <div className="historique-changes">
    {changes.map((change, index) => <span className="historique-change" key={`${change.champ}-${index}`}>
      <b>{change.champ}</b>
      <span className="change-values">
        {change.ancienneValeur != null && change.ancienneValeur !== "" && <span className="old-value">{displayValue(change.ancienneValeur)}</span>}
        {change.ancienneValeur != null && change.ancienneValeur !== "" && change.nouvelleValeur != null && change.nouvelleValeur !== "" && <span className="change-arrow" aria-label="devient">→</span>}
        {change.nouvelleValeur != null && change.nouvelleValeur !== "" && <span className="new-value">{displayValue(change.nouvelleValeur)}</span>}
      </span>
    </span>)}
  </div>;
}

function Historique() {
  const [actions, setActions] = useState([]);
  const [search, setSearch] = useState("");
  const [actionFilter, setActionFilter] = useState("");
  const [isLoading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    api.get("/api/historique")
      .then(({ data }) => active && setActions(data))
      .catch(() => active && setError("L’historique n’est pas disponible pour le moment."))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, []);

  const filteredActions = useMemo(() => {
    const term = search.trim().toLocaleLowerCase("fr");
    return actions.filter((item) => {
      const matchesUser = !term || `${item.matricule || ""} ${item.nomUser || ""}`.toLocaleLowerCase("fr").includes(term);
      return matchesUser && (!actionFilter || item.action === actionFilter);
    });
  }, [actions, search, actionFilter]);

  const formatDate = (value) => value
    ? new Intl.DateTimeFormat("fr-FR").format(new Date(`${value}T00:00:00`))
    : "—";
  const formatTime = (value) => value ? value.slice(0, 8) : "—";
  const formatDateTime = (item) => `${formatDate(item.date)} ${formatTime(item.heure).slice(0, 5)}`;

  return <section className="historique-page">
    <div className="page-heading">
      <div><p className="dashboard-eyebrow">Administration</p><h1>Historique des actions</h1><p>Consultez les créations, modifications et suppressions effectuées par les utilisateurs.</p></div>
      <span className="page-heading-icon"><History /></span>
    </div>

    <article className="units-list-card historique-card">
      <div className="historique-toolbar">
        <label className="historique-search"><Search size={18} /><input type="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Rechercher par matricule ou nom" aria-label="Rechercher par matricule ou nom" /></label>
        <select value={actionFilter} onChange={(event) => setActionFilter(event.target.value)} aria-label="Filtrer par type d’action">
          <option value="">Toutes les actions</option>
          <option value="Creation">Créations</option>
          <option value="Modification">Modifications</option>
          <option value="Suppression">Suppressions</option>
        </select>
      </div>
      {error && <p className="historique-error" role="alert">{error}</p>}
      <div className="units-table-wrap"><table><thead><tr><th>Action</th><th>Essai concerné</th><th>Action sur</th><th>Détail</th><th>Matricule</th><th>Nom utilisateur</th><th>Date et heure</th></tr></thead><tbody>
        {isLoading ? <tr><td colSpan="7" className="table-state">Chargement de l’historique…</td></tr>
          : filteredActions.length ? filteredActions.map((item, index) => <tr key={`${item.action}-${item.essaiConcerne}-${item.actionSur}-${item.detail}-${item.date}-${item.heure}-${index}`}><td><span className={`historique-action ${item.action.toLowerCase()}`}>{actionLabels[item.action] || item.action}</span></td><td><strong>{item.essaiConcerne || "—"}</strong></td><td><span className="historique-target">{item.actionSur}</span></td><td className="historique-detail"><ChangeDetail item={item} /></td><td>{item.matricule}</td><td>{item.nomUser}</td><td>{formatDateTime(item)}</td></tr>)
            : <tr><td colSpan="7" className="table-state">Aucune action ne correspond aux filtres.</td></tr>}
      </tbody></table></div>
    </article>
  </section>;
}

export default Historique;
