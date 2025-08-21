"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        let msg = "Login failed";
        try {
          const j = await res.json();
          msg = j?.message || msg;
        } catch {}
        throw new Error(msg);
      }

      const data: { token: string; role?: string } = await res.json();
      localStorage.setItem("token", data.token);

      if (data.role === "ADMIN" || email.toLowerCase() === "admin@example.com") {
        router.push("/admin");
      } else {
        router.push("/user");
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Something went wrong";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="screen">
      <div aria-hidden className="backdrop">
        {/* subtle film perforations on the sides */}
        <div className="film film-left" />
        <div className="film film-right" />
      </div>

      <section className="card" role="dialog" aria-labelledby="title">
        <header className="card-head">
          <div className="badge">🎟️</div>
          <h1 id="title">Sign in</h1>
          <p className="sub">Welcome back to CineBook</p>
        </header>

        {error && (
          <div className="alert" role="alert">
            <span className="alert-dot" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleLogin} className="form" noValidate>
          <label className="field">
            <span className="label">Email</span>
            <div className="input-wrap">
              <span className="icon" aria-hidden>✉️</span>
              <input
                type="email"
                inputMode="email"
                autoComplete="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </label>

          <label className="field">
            <span className="label">Password</span>
            <div className="input-wrap">
              <span className="icon" aria-hidden>🔒</span>
              <input
                type={showPwd ? "text" : "password"}
                autoComplete="current-password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                minLength={6}
              />
              <button
                type="button"
                className="ghost"
                aria-label={showPwd ? "Hide password" : "Show password"}
                onClick={() => setShowPwd((s) => !s)}
                title={showPwd ? "Hide" : "Show"}
              >
                {showPwd ? "🙈" : "👁️"}
              </button>
            </div>
          </label>

          <div className="row">
            <label className="remember">
              <input type="checkbox" /> <span>Remember me</span>
            </label>
            <a className="link" href="/forgot-password">Forgot password?</a>
          </div>

          <button className="cta" type="submit" disabled={loading}>
            {loading ? (
              <>
                <span className="spinner" aria-hidden />
                Signing in…
              </>
            ) : (
              "Sign in"
            )}
          </button>
        </form>

        <footer className="card-foot">
          <div className="divider"><span>or</span></div>
          <div className="alt-actions">
            <button className="alt-btn" type="button" onClick={() => alert("SSO coming soon")}>
              Continue with Google
            </button>
            <button className="alt-btn" type="button" onClick={() => alert("SSO coming soon")}>
              Continue with GitHub
            </button>
          </div>
          <p className="meta">
            Don’t have an account? <a className="link" href="/register">Create one</a>
          </p>
        </footer>
      </section>

      <style jsx>{`
        :root{
          --bg:#0f1115;
          --card-bg:rgba(255,255,255,0.06);
          --card-stroke:rgba(255,255,255,0.12);
          --fg:#eaeef5;
          --muted:#a8b0bf;
          --accent:#d5b069; /* warm ticket-gold, low saturation */
          --accent-2:#8ea0ff; /* cool slate for focus/hover */
          --error:#ff6b6b;
          --shadow: 0 10px 30px rgba(0,0,0,0.35);
        }
        .screen{
          min-height:100dvh;
          background:
            radial-gradient(1200px 600px at 70% -10%, rgba(142,160,255,0.18), transparent 60%),
            radial-gradient(1200px 600px at 0% 100%, rgba(213,176,105,0.14), transparent 60%),
            var(--bg);
          display:grid;
          place-items:center;
          padding:24px;
          color:var(--fg);
          position:relative;
          overflow:hidden;
        }
        .backdrop{
          position:absolute; inset:0; pointer-events:none; opacity:0.4;
        }
        .film{
          position:absolute; top:0; bottom:0; width:80px;
          background:
            linear-gradient(to bottom, transparent 0 24px, rgba(255,255,255,0.06) 24px 40px, transparent 40px 64px) repeat-y;
          filter:blur(0.3px);
          mask-image:linear-gradient(to bottom, transparent, black 20%, black 80%, transparent);
        }
        .film-left{ left:0; }
        .film-right{ right:0; transform:scaleX(-1); }

        .card{
          width:100%;
          max-width:440px;
          border-radius:24px;
          border:1px solid var(--card-stroke);
          background:linear-gradient(180deg, rgba(255,255,255,0.10), rgba(255,255,255,0.05)) , var(--card-bg);
          box-shadow:var(--shadow);
          backdrop-filter: blur(10px) saturate(120%);
          padding:24px 24px 8px;
          position:relative;
        }
        .card::before{
          /* ticket stub notches */
          content:"";
          position:absolute; left:-10px; top:100px; width:20px; height:20px; border-radius:50%;
          background:var(--bg); box-shadow: 0 200px 0 0 var(--bg);
        }
        .card::after{
          content:"";
          position:absolute; right:-10px; top:180px; width:20px; height:20px; border-radius:50%;
          background:var(--bg); box-shadow: 0 200px 0 0 var(--bg);
        }

        .card-head{
          text-align:center; margin-bottom:10px;
        }
        .badge{
          width:52px; height:52px; border-radius:14px;
          display:grid; place-items:center; margin:0 auto 8px;
          background: radial-gradient(120% 120% at 30% 30%, rgba(213,176,105,0.35), rgba(142,160,255,0.20));
          border:1px solid var(--card-stroke);
        }
        h1{
          margin:0; font-size:28px; letter-spacing:0.2px; font-weight:700;
        }
        .sub{ margin:4px 0 0; color:var(--muted); font-size:14px; }

        .alert{
          display:flex; gap:10px; align-items:center;
          border:1px solid rgba(255,107,107,0.35);
          background:rgba(255,107,107,0.08);
          color: var(--fg);
          padding:10px 12px; border-radius:12px; margin:14px 0 4px;
        }
        .alert-dot{
          width:9px; height:9px; border-radius:50%; background:var(--error); flex:0 0 auto;
          box-shadow:0 0 10px rgba(255,107,107,0.6);
        }

        .form{ margin-top:8px; display:grid; gap:14px; }
        .field{ display:grid; gap:8px; }
        .label{ font-size:12px; color:var(--muted); letter-spacing:0.3px; }
        .input-wrap{
          display:flex; align-items:center; gap:8px;
          padding:12px 12px;
          border-radius:14px;
          border:1px solid var(--card-stroke);
          background:rgba(10,12,18,0.35);
          transition:border 0.2s ease, box-shadow 0.2s ease, transform 0.02s ease;
        }
        .input-wrap:focus-within{
          border-color:rgba(142,160,255,0.65);
          box-shadow:0 0 0 4px rgba(142,160,255,0.12);
        }
        .icon{ opacity:0.9; }
        input{
          flex:1; background:transparent; color:var(--fg); border:0; outline:0;
          font-size:15px; letter-spacing:0.2px;
        }
        .ghost{
          border:0; background:transparent; color:var(--muted);
          padding:4px 6px; border-radius:8px; cursor:pointer;
        }
        .ghost:hover{ color:var(--fg); }

        .row{
          display:flex; align-items:center; justify-content:space-between; margin-top:2px;
        }
        .remember{ display:flex; align-items:center; gap:8px; color:var(--muted); font-size:13px; }
        .remember input{ accent-color: var(--accent-2); }

        .link{
          color:var(--fg); text-decoration:none; border-bottom:1px dashed rgba(255,255,255,0.25);
        }
        .link:hover{ border-bottom-color: rgba(255,255,255,0.6); }

        .cta{
          margin-top:8px;
          width:100%;
          border:1px solid rgba(213,176,105,0.65);
          background:
            linear-gradient(180deg, rgba(213,176,105,0.28), rgba(213,176,105,0.18));
          color:#0f1115;
          font-weight:700; letter-spacing:0.3px;
          padding:12px 16px; border-radius:14px;
          cursor:pointer; transition: transform 0.02s ease, box-shadow 0.2s ease;
          box-shadow: 0 8px 20px rgba(213,176,105,0.18);
        }
        .cta:hover{ box-shadow: 0 10px 26px rgba(213,176,105,0.24); }
        .cta:active{ transform: translateY(1px); }
        .cta:disabled{
          opacity:0.7; cursor:not-allowed; transform:none; box-shadow:none;
        }
        .spinner{
          width:16px; height:16px; border-radius:50%;
          border:2px solid rgba(0,0,0,0.25); border-top-color: rgba(0,0,0,0.6);
          display:inline-block; margin-right:8px;
          animation: spin 0.8s linear infinite;
        }
        @keyframes spin{ to{ transform: rotate(360deg); } }

        .card-foot{ padding:18px 0 8px; }
        .divider{
          position:relative; text-align:center; margin:8px 0 12px; color:var(--muted); font-size:12px;
        }
        .divider::before, .divider::after{
          content:""; position:absolute; top:50%; width:40%; height:1px; background:var(--card-stroke);
        }
        .divider::before{ left:0; } .divider::after{ right:0; }
        .divider span{ background:transparent; padding:0 8px; }

        .alt-actions{ display:grid; grid-template-columns:1fr 1fr; gap:10px; }
        .alt-btn{
          border:1px solid var(--card-stroke);
          background:rgba(255,255,255,0.04);
          color:var(--fg); padding:10px 12px; border-radius:12px; cursor:pointer;
        }
        .alt-btn:hover{ border-color: rgba(142,160,255,0.55); box-shadow:0 0 0 4px rgba(142,160,255,0.10); }

        .meta{ text-align:center; margin:14px 0 6px; color:var(--muted); }
        @media (max-width:480px){
          .card{ padding:20px 16px 8px; }
        }
      `}</style>
    </main>
  );
}
