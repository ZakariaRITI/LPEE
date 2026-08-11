import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff, Fingerprint, LockKeyhole, ArrowRight } from "lucide-react";
import api from "../services/api";
import { getValidSession, saveSession } from "../services/auth";
import "./Login.css";

function Login({ onLogin }) {
  const navigate = useNavigate();
  const [form, setForm] = useState({ matricule: "", motDePasse: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = ({ target: { name, value } }) => {
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      const { data } = await api.post("/api/auth/login", form);

      if (!data?.token || data.type !== "Bearer") {
        throw new Error("Invalid authentication response.");
      }

      saveSession(data);

      if (!getValidSession()) {
        throw new Error("Invalid authentication response.");
      }

      onLogin();
      navigate("/dashboard", { replace: true });
    } catch (requestError) {
      const messages = requestError.response?.data?.messages;
      setError(
        messages?.matricule ||
          requestError.response?.data?.message ||
          (requestError.response?.status === 401
            ? "Matricule ou mot de passe incorrect."
            : "La connexion au serveur est impossible. Réessayez.")
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-visual" aria-label="Laboratoire Public d'Essais et d'Études">
        <div className="visual-content">
          <img className="lpee-logo" src="/images/Lpee-logo.png" alt="LPEE" />
        </div>
        <img className="lpee-worksite" src="/images/LPEE-image.jpg" alt="Intervention LPEE sur un chantier" />
        <div className="visual-copy">
          <p className="visual-kicker">Référentiel des essais</p>
          <h1>Maîtrisez vos essais de laboratoire.</h1>
          <p>Centralisez, organisez et suivez l’ensemble du référentiel des essais, normes, équipements et documents de votre laboratoire.</p>
        </div>
      </section>

      <section className="login-panel">
        <div className="login-card">
          <div className="mobile-brand"><img src="/images/Lpee-logo.png" alt="LPEE" /></div>
          <p className="eyebrow">Espace sécurisé</p>
          <h2>Bienvenue</h2>
          <p className="intro">Connectez-vous pour accéder à votre tableau de bord.</p>

          <form onSubmit={handleSubmit} noValidate>
            <label htmlFor="matricule">Matricule</label>
            <div className="input-wrap">
              <Fingerprint aria-hidden="true" size={19} />
              <input id="matricule" name="matricule" type="text" autoComplete="username" value={form.matricule} onChange={handleChange} placeholder="MAT-001" required />
            </div>

            <label htmlFor="motDePasse">Mot de passe</label>
            <div className="input-wrap">
              <LockKeyhole aria-hidden="true" size={19} />
              <input id="motDePasse" name="motDePasse" type={showPassword ? "text" : "password"} autoComplete="current-password" value={form.motDePasse} onChange={handleChange} placeholder="Votre mot de passe" required />
              <button className="password-toggle" type="button" onClick={() => setShowPassword((current) => !current)} aria-label={showPassword ? "Masquer le mot de passe" : "Afficher le mot de passe"}>
                {showPassword ? <EyeOff size={19} /> : <Eye size={19} />}
              </button>
            </div>

            {error && <p className="form-error" role="alert">{error}</p>}
            <button className="submit-button" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Connexion en cours…" : <>Se connecter <ArrowRight size={19} /></>}
            </button>
          </form>
          <p className="support-text">Besoin d’aide ? Contactez votre administrateur.</p>
        </div>
      </section>
    </main>
  );
}

export default Login;
