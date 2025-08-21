
"use client";

import { useRouter } from "next/navigation";

export default function HomePage() {
  const router = useRouter();

  // You can replace these with your own files in /public/posters/...
  const posters = [
    "https://images.unsplash.com/photo-1517602302552-471fe67acf66?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1512428559087-560fa5ceab42?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1524984690821-49b3f4b6d3a5?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1496317556649-f930d733eea0?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1478720568477-152d9b164e26?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1517601640432-3c86b1b98f49?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963f?q=80&w=800&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1460881680858-30d872d5b530?q=80&w=800&auto=format&fit=crop",
  ];

  return (
    <main className="screen">
      {/* subtle backdrop details */}
      <div aria-hidden className="backdrop">
        {/* poster wall */}
        <div className="poster-wall">
          {posters.map((src, i) => (
            <div key={i} className="poster" style={{ backgroundImage: `url(${src})` }} />
          ))}
          {/* dark veil so content pops */}
          <div className="veil" />
        </div>

        {/* ambient lights + film edges */}
        <div className="spot spot-a" />
        <div className="spot spot-b" />
        <div className="film film-left" />
        <div className="film film-right" />
      </div>

      <section className="hero" role="region" aria-label="Welcome">
        <div className="marquee">
          <span className="dot" />
          <h1 className="title">CineBook</h1>
          <span className="dot" />
        </div>
        <p className="tagline">Book the perfect show—fast, easy, and elegant.</p>

        <div className="cta-row">
          <button type="button" className="btn btn-primary" onClick={() => router.push("/register")}>
            Create account
          </button>
          <button type="button" className="btn btn-ghost" onClick={() => router.push("/login")}>
            I already have an account
          </button>
        </div>

        <div className="divider">
          <span>or</span>
        </div>

        <div className="quick-row">
          <button type="button" className="chip" onClick={() => router.push("/movies")}>Browse Movies</button>
          <button type="button" className="chip" onClick={() => router.push("/shows")}>Find Shows Near Me</button>
        </div>

        <div className="ticket">
          <div className="ticket-left">
            <div className="stamp">🎬</div>
            <div>
              <div className="ticket-title">Tonight’s Picks</div>
              <div className="ticket-sub">Fresh releases & fan favorites</div>
            </div>
          </div>
          <button type="button" className="btn btn-mini" onClick={() => router.push("/movies")}>
            Explore
          </button>
        </div>
      </section>

      <style jsx>{`
        :root{
          --bg:#0f1115;
          --fg:#eaeef5;
          --muted:#a8b0bf;
          --panel:rgba(255,255,255,0.06);
          --stroke:rgba(255,255,255,0.12);
          --accent:#d5b069;   /* warm ticket-gold */
          --focus:#8ea0ff;    /* soft slate highlight */
          --shadow:0 12px 40px rgba(0,0,0,0.35);
        }

        .screen{
          min-height:100dvh;
          background:
            radial-gradient(1200px 600px at 80% -10%, rgba(142,160,255,0.18), transparent 60%),
            radial-gradient(1200px 600px at 0% 100%, rgba(213,176,105,0.12), transparent 60%),
            var(--bg);
          display:grid; place-items:center; padding:32px; color:var(--fg);
          position:relative; overflow:hidden;
        }

        .backdrop{ position:absolute; inset:0; pointer-events:none; }

        /* ===== Poster collage ===== */
        .poster-wall{
          position:absolute; inset:0;
          display:grid;
          grid-template-columns:repeat(8, 1fr);
          grid-auto-rows:120px;
          gap:6px;
          padding:6px;
          opacity:.35;
          mix-blend-mode:screen;     /* posters blend softly with bg */
          filter:saturate(0.85) contrast(0.9);
        }
        .poster{
          background-size:cover; background-position:center;
          border-radius:8px;
          box-shadow:0 10px 30px rgba(0,0,0,.35) inset;
        }
        /* make some tiles bigger for visual rhythm */
        .poster:nth-child(6n){ grid-column: span 2; grid-row: span 2; }
        .poster:nth-child(10n){ grid-column: span 3; }
        .poster:nth-child(14n){ grid-row: span 2; }

        /* soft top/bottom fade so content remains readable */
        .veil{
          position:absolute; inset:0;
          background:
            linear-gradient(to bottom, rgba(15,17,21,.85), rgba(15,17,21,.2) 20%, rgba(15,17,21,.2) 80%, rgba(15,17,21,.9));
          pointer-events:none;
        }

        /* ambient spots & film */
        .spot{ position:absolute; filter:blur(50px); opacity:0.22; }
        .spot-a{ width:380px; height:380px; left:8%; top:12%; background:#8ea0ff; }
        .spot-b{ width:420px; height:420px; right:5%; bottom:8%; background:#d5b069; opacity:0.18; }
        .film{
          position:absolute; top:0; bottom:0; width:84px;
          background:
            linear-gradient(to bottom, transparent 0 24px, rgba(255,255,255,0.06) 24px 40px, transparent 40px 64px) repeat-y;
          mask-image:linear-gradient(to bottom, transparent, black 20%, black 80%, transparent);
          opacity:0.35;
        }
        .film-left{ left:0; }
        .film-right{ right:0; transform:scaleX(-1); }

        .hero{
          width:100%; max-width:820px;
          border:1px solid var(--stroke);
          background:linear-gradient(180deg, rgba(255,255,255,0.10), rgba(255,255,255,0.05)), var(--panel);
          backdrop-filter: blur(10px) saturate(120%);
          border-radius:28px; padding:30px 28px; box-shadow:var(--shadow);
          position:relative;
        }
        .hero::before, .hero::after{
          content:""; position:absolute; width:22px; height:22px; border-radius:50%;
          background:var(--bg);
        }
        .hero::before{ left:-11px; top:130px; box-shadow:0 200px 0 0 var(--bg); }
        .hero::after{ right:-11px; top:220px; box-shadow:0 200px 0 0 var(--bg); }

        .marquee{
          display:flex; align-items:center; justify-content:center; gap:14px; margin-top:6px;
        }
        .dot{ width:8px; height:8px; border-radius:50%; background:var(--accent); box-shadow:0 0 12px rgba(213,176,105,0.6); }
        .title{
          margin:0; font-size:42px; font-weight:800; letter-spacing:0.6px;
          text-shadow:0 2px 12px rgba(0,0,0,0.35);
        }
        .tagline{
          text-align:center; margin:8px 0 18px; color:var(--muted); font-size:16px;
        }

        .cta-row{
          display:flex; flex-wrap:wrap; gap:12px; justify-content:center;
          margin:8px 0 10px;
        }
        .btn{
          border-radius:14px; padding:12px 18px; font-weight:700; letter-spacing:0.3px;
          cursor:pointer; transition: box-shadow .18s ease, transform .02s ease, border-color .18s ease;
          border:1px solid var(--stroke);
        }
        .btn:active{ transform: translateY(1px); }
        .btn-primary{
          background:linear-gradient(180deg, rgba(213,176,105,0.42), rgba(213,176,105,0.24));
          color:#0f1115; border-color:rgba(213,176,105,0.75);
          box-shadow:0 10px 22px rgba(213,176,105,0.28);
        }
        .btn-primary:hover{ box-shadow:0 12px 28px rgba(213,176,105,0.36); }
        .btn-ghost{
          background:rgba(255,255,255,0.06); color:var(--fg);
        }
        .btn-ghost:hover{ border-color:rgba(142,160,255,0.55); box-shadow:0 0 0 4px rgba(142,160,255,0.14); }
        .btn-mini{
          padding:10px 14px; font-size:14px;
          background:rgba(255,255,255,0.08); color:var(--fg);
        }
        .btn-mini:hover{ border-color:rgba(142,160,255,0.55); box-shadow:0 0 0 4px rgba(142,160,255,0.12); }

        .divider{
          position:relative; text-align:center; margin:10px auto 14px; color:var(--muted); font-size:12px; width:min(420px, 90%);
        }
        .divider::before, .divider::after{
          content:""; position:absolute; top:50%; width:40%; height:1px; background:var(--stroke);
        }
        .divider::before{ left:0; } .divider::after{ right:0; }
        .divider span{ background:transparent; padding:0 8px; }

        .quick-row{
          display:flex; gap:10px; justify-content:center; flex-wrap:wrap; margin-bottom:14px;
        }
        .chip{
          border-radius:999px; padding:10px 14px; font-size:14px; cursor:pointer;
          background:rgba(255,255,255,0.06); border:1px solid var(--stroke); color:var(--fg);
        }
        .chip:hover{ border-color:rgba(142,160,255,0.55); box-shadow:0 0 0 4px rgba(142,160,255,0.10); }

        .ticket{
          margin:8px auto 0; display:flex; align-items:center; justify-content:space-between; gap:12px;
          background:rgba(10,12,18,0.40); border:1px dashed rgba(255,255,255,0.20);
          padding:14px 16px; border-radius:18px; max-width:640px;
        }
        .ticket-left{ display:flex; align-items:center; gap:12px; }
        .stamp{
          width:40px; height:40px; border-radius:12px; display:grid; place-items:center;
          background: radial-gradient(120% 120% at 30% 30%, rgba(213,176,105,0.35), rgba(142,160,255,0.20));
          border:1px solid var(--stroke);
        }
        .ticket-title{ font-weight:700; letter-spacing:0.2px; }
        .ticket-sub{ font-size:13px; color:var(--muted); }

        @media (max-width:520px){
          .title{ font-size:32px; }
          .ticket{ flex-direction:column; align-items:stretch; }
          .btn-mini{ width:100%; }
          .poster-wall{ grid-auto-rows:90px; }
        }
      `}</style>
    </main>
  );
}
