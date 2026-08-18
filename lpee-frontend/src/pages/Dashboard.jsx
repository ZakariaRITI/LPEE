import { useEffect, useMemo, useRef, useState } from "react";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
import {
  BarChart3, Beaker, BookOpen, Boxes, Building2, ChevronDown, ChevronRight,
  FileText, LayoutDashboard, LogOut, Menu, Settings, UserRound, Wrench, X,
} from "lucide-react";
import api from "../services/api";
import { clearSession } from "../services/auth";
import Unite from "./Unite";
import Produit from "./Produit";
import Equipement from "./Equipement";
import Norme from "./Norme";
import Essai from "./Essai";
import RealisationEssai from "./RealisationEssai";
import Utilisateur from "./Utilisateur";
import Historique from "./Historique";
import Profil from "./Profil";
import Vue360 from "./Vue360";
import "./Dashboard.css";

const menuGroups = [
  { label: "Référentiel", icon: BookOpen, items: [["Unité", "unites"], ["Produit", "produits"], ["Équipement", "equipements"], ["Norme", "normes"], ["Essai", "essais"], ["Réalisation Essai", "realisations-essais"]] },
  { label: "Administration", icon: Settings, items: [["Utilisateur", "utilisateurs"], ["Historique", "historique"]] },
  { label: "Document", icon: FileText, items: [["Essai", "documents/essais"], ["Norme", "documents/normes"]] },
  { label: "Vue 360", icon: BarChart3, items: [["Accéder à la vue 360", "vue-360"]] },
];

const emptyData = { unites: [], equipements: [], essais: [], realisations: [] };

function groupBy(items, getLabel) {
  return items.reduce((groups, item) => {
    const label = getLabel(item);
    groups[label] = (groups[label] || 0) + 1;
    return groups;
  }, {});
}

function BarListChart({ data, ariaLabel, emptyMessage }) {
  const entries = Object.entries(data);
  const max = Math.max(...entries.map(([, value]) => value), 1);

  if (!entries.length) return <p className="chart-empty">{emptyMessage}</p>;

  return <div className="bar-chart" aria-label={ariaLabel}>
    {entries.map(([label, value]) => <div className="bar-row" key={label}>
      <span title={label}>{label}</span>
      <div className="bar-track"><div className="bar-fill" style={{ width: `${(value / max) * 100}%` }} /></div>
      <strong>{value}</strong>
    </div>)}
  </div>;
}

const productChartColors = ["#0877b6", "#f2b632", "#20a47b", "#7658c9", "#e46f61", "#36a4c7", "#d78b2d", "#4f78c4"];

function ProductDonutChart({ data }) {
  const entries = Object.entries(data).filter(([, value]) => value > 0);
  const total = entries.reduce((sum, [, value]) => sum + value, 0);
  if (!total) return <p className="chart-empty">Aucun produit associé à une unité.</p>;

  let offset = 0;
  return <div className="product-donut-chart">
    <svg viewBox="0 0 42 42" role="img" aria-label="Répartition des produits par unité">
      <circle className="donut-base" cx="21" cy="21" r="15.9155" />
      {entries.map(([label, value], index) => {
        const percentage = (value / total) * 100;
        const segmentOffset = offset;
        offset += percentage;
        return <circle key={label} className="product-donut-segment" cx="21" cy="21" r="15.9155" pathLength="100" stroke={productChartColors[index % productChartColors.length]} strokeDasharray={`${percentage} ${100 - percentage}`} strokeDashoffset={-segmentOffset} />;
      })}
      <text className="product-donut-total" x="21" y="20">{total}</text>
      <text className="product-donut-label" x="21" y="25">produits</text>
    </svg>
    <div className="product-donut-legend">
      {entries.map(([label, value], index) => <div key={label}>
        <i style={{ background: productChartColors[index % productChartColors.length] }} />
        <span title={label}>{label}</span>
        <strong>{value} · {Math.round((value / total) * 100)}%</strong>
      </div>)}
    </div>
  </div>;
}

function Dashboard({ onLogout }) {
  const location = useLocation();
  const navigate = useNavigate();
  const profileAreaRef = useRef(null);
  const [isSidebarOpen, setSidebarOpen] = useState(false);
  const [isProfileOpen, setProfileOpen] = useState(false);
  const [openMenus, setOpenMenus] = useState(() => new Set(menuGroups.map((group) => group.label)));
  const [data, setData] = useState(emptyData);
  const [isLoading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [currentUser, setCurrentUser] = useState(null);
  const [session] = useState(() => JSON.parse(localStorage.getItem("lpee_session") || "null"));

  useEffect(() => {
    let active = true;
    const requests = [
      ["unites", "/api/unites"],
      ["equipements", "/api/equipements"],
      ["essais", "/api/essais"],
      ["realisations", "/api/realisations-essais"],
      ["currentUser", `/api/utilisateurs/${session?.idUser}`],
    ];

    Promise.all(requests.map(([key, endpoint]) => api.get(endpoint).then(({ data: response }) => [key, response])))
      .then((responses) => {
        if (!active) return;
        const result = Object.fromEntries(responses);
        setCurrentUser(result.currentUser);
        delete result.currentUser;
        setData(result);
      })
      .catch((error) => {
        if (error.response?.status === 401) {
          clearSession();
          onLogout();
          navigate("/login", { replace: true });
          return;
        }
        if (active) setLoadError("Les statistiques ne sont pas disponibles pour le moment.");
      })
      .finally(() => active && setLoading(false));

    return () => { active = false; };
  }, [navigate, onLogout, session?.idUser]);

  useEffect(() => {
    if (!isProfileOpen) return undefined;

    const closeProfileOnOutsideClick = (event) => {
      if (!profileAreaRef.current?.contains(event.target)) setProfileOpen(false);
    };

    document.addEventListener("pointerdown", closeProfileOnOutsideClick);
    return () => document.removeEventListener("pointerdown", closeProfileOnOutsideClick);
  }, [isProfileOpen]);

  const stats = useMemo(() => {
    const normalize = (value) => (value || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().trim();
    return {
      essais: data.essais.length,
      equipements: data.equipements.length,
      regionales: data.unites.filter((unite) => normalize(unite.typeUnite) === "regionale").length,
      specialisees: data.unites.filter((unite) => normalize(unite.typeUnite) === "specialisee").length,
    };
  }, [data]);
  const essaisParUnite = useMemo(() => groupBy(data.realisations, (realisation) => {
    const unite = data.unites.find((item) => item.idUnite === realisation.idUnite);
    return unite?.nomUnite || unite?.codeUnite || `Unité #${realisation.idUnite}`;
  }), [data.realisations, data.unites]);
  const produitsParUnite = useMemo(() => {
    const essaisById = new Map(data.essais.map((essai) => [essai.idEssai, essai]));
    const unitesById = new Map(data.unites.map((unite) => [unite.idUnite, unite]));
    const productsByUnit = new Map();

    data.realisations.forEach((realisation) => {
      const idProduit = essaisById.get(realisation.idEssai)?.idProduit;
      if (!idProduit) return;
      const unite = unitesById.get(realisation.idUnite);
      const label = unite?.nomUnite || unite?.codeUnite || `Unité #${realisation.idUnite}`;
      if (!productsByUnit.has(label)) productsByUnit.set(label, new Set());
      productsByUnit.get(label).add(idProduit);
    });

    return Object.fromEntries([...productsByUnit].map(([label, products]) => [label, products.size]));
  }, [data.essais, data.realisations, data.unites]);
  const activePath = location.pathname.replace("/dashboard/", "") || "accueil";

  const toggleMenu = (label) => setOpenMenus((current) => {
    const next = new Set(current);
    next.has(label) ? next.delete(label) : next.add(label);
    return next;
  });
  const closeSidebar = () => setSidebarOpen(false);
  const logout = () => { clearSession(); onLogout(); navigate("/login", { replace: true }); };

  return <div className="dashboard-shell">
    <header className="topbar">
      <button className="mobile-menu" onClick={() => setSidebarOpen(true)} aria-label="Ouvrir le menu"><Menu /></button>
      <Link className="topbar-brand" to="/dashboard" aria-label="Accueil LPEE"><span className="logo-display"><img src="/images/logo_LPEE.png" alt="LPEE" /></span></Link>
      <div className="topbar-title"><span>Référentiel LPEE</span><small>Gestion des essais de laboratoire</small></div>
      <div className="topbar-actions">
        <div className="profile-area" ref={profileAreaRef}>
          <button className="profile-trigger" onClick={() => setProfileOpen((open) => !open)} aria-expanded={isProfileOpen} aria-label="Ouvrir le profil utilisateur">
            <span className="profile-avatar"><UserRound size={22} /></span>
            <span className="profile-details"><strong>{currentUser?.nomUser || currentUser?.email || "Chargement…"}</strong><small>{session?.role || ""}</small></span>
            <ChevronDown size={17} />
          </button>
          {isProfileOpen && <div className="profile-menu"><strong>{currentUser?.nomUser || currentUser?.email}</strong><span>{currentUser?.email}</span><small>{session?.role}</small><Link to="/dashboard/profil" onClick={() => setProfileOpen(false)}>Voir le profil</Link></div>}
        </div>
        <button className="topbar-logout" onClick={logout} aria-label="Déconnexion"><LogOut size={18} /><span>Déconnexion</span></button>
      </div>
    </header>

    <aside className={`sidebar ${isSidebarOpen ? "sidebar-open" : ""}`}>
      <div className="sidebar-mobile-head"><img src="/images/Lpee-logo.png" alt="LPEE" /><button onClick={closeSidebar} aria-label="Fermer le menu"><X /></button></div>
      <NavLink className="dashboard-link" to="/dashboard" end onClick={closeSidebar}><LayoutDashboard size={20} />Tableau de bord</NavLink>
      <nav aria-label="Navigation principale">
        {menuGroups.map(({ label, icon: Icon, items }) => {
          const expanded = openMenus.has(label);
          return <div className="nav-group" key={label}>
            <button className="nav-group-toggle" onClick={() => toggleMenu(label)} aria-expanded={expanded}><span><Icon size={19} />{label}</span>{expanded ? <ChevronDown size={18} /> : <ChevronRight size={18} />}</button>
            {expanded && <div className="submenu">{items.map(([item, path]) => <NavLink key={`${label}-${path}`} to={`/dashboard/${path}`} className={({ isActive }) => isActive || activePath === path ? "active" : ""} onClick={closeSidebar}>{item}</NavLink>)}</div>}
          </div>;
        })}
      </nav>
      <button className="logout-button" onClick={logout}><LogOut size={19} />Déconnexion</button>
    </aside>
    {isSidebarOpen && <button className="sidebar-backdrop" aria-label="Fermer le menu" onClick={closeSidebar} />}

    <main className="dashboard-content">
      {activePath === "unites" ? <Unite /> : activePath === "produits" ? <Produit /> : activePath === "equipements" ? <Equipement /> : activePath === "normes" ? <Norme /> : activePath === "essais" ? <Essai /> : activePath === "realisations-essais" ? <RealisationEssai /> : activePath === "utilisateurs" ? <Utilisateur /> : activePath === "historique" ? <Historique /> : activePath === "profil" ? <Profil /> : activePath === "vue-360" ? <Vue360 /> : <>
      <section className="dashboard-heading"><div><p className="dashboard-eyebrow">Vue d’ensemble</p><h1>Tableau de bord</h1><p>Suivez l’état de votre référentiel des essais de laboratoire.</p></div><span className="live-status"><i />Données en temps réel</span></section>
      {loadError && <p className="dashboard-error" role="alert">{loadError}</p>}
      <section className="kpi-grid" aria-label="Indicateurs clés">
        <article className="kpi-card"><span className="kpi-icon blue"><Beaker /></span><div><p>Essai</p><strong>{isLoading ? "—" : stats.essais}</strong></div></article>
        <article className="kpi-card"><span className="kpi-icon yellow"><Wrench /></span><div><p>Équipement</p><strong>{isLoading ? "—" : stats.equipements}</strong></div></article>
        <article className="kpi-card"><span className="kpi-icon green"><Building2 /></span><div><p>Unité régionale</p><strong>{isLoading ? "—" : stats.regionales}</strong></div></article>
        <article className="kpi-card"><span className="kpi-icon purple"><Building2 /></span><div><p>Unité spécialisée</p><strong>{isLoading ? "—" : stats.specialisees}</strong></div></article>
      </section>
      <section className="charts-grid">
        <article className="chart-card"><div className="chart-heading"><div><h2>Répartition des essais par unité</h2><p>Essais regroupés par unité.</p></div><Building2 size={21} /></div>{isLoading ? <p className="chart-empty">Chargement des données…</p> : <BarListChart data={essaisParUnite} ariaLabel="Nombre d’essais par unité" emptyMessage="Aucun essai disponible." />}</article>
        <article className="chart-card"><div className="chart-heading"><div><h2>Répartition des produits par unité</h2><p>Produits distincts associés aux essais de chaque unité.</p></div><Boxes size={21} /></div>{isLoading ? <p className="chart-empty">Chargement des données…</p> : <ProductDonutChart data={produitsParUnite} />}</article>
      </section>
      </>}
    </main>
  </div>;
}

export default Dashboard;
