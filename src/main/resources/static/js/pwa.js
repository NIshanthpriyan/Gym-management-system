// PWA Installation & Service Worker Handler
let deferredPrompt;

if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch(err => {
      console.log('SW registration error:', err);
    });
  });
}

window.addEventListener('beforeinstallprompt', (e) => {
  // Prevent Chrome 67 and earlier from automatically showing the prompt
  e.preventDefault();
  // Stash the event so it can be triggered later.
  deferredPrompt = e;
  
  // Show install button if it exists
  const installBtn = document.getElementById('pwaInstallBtn');
  if (installBtn) {
    installBtn.style.display = 'inline-flex';
  }
  
  // Create floating install button if not on page
  createFloatingInstallButton();
});

function createFloatingInstallButton() {
  if (document.getElementById('floatingPwaBtn')) return;
  
  const btn = document.createElement('button');
  btn.id = 'floatingPwaBtn';
  btn.innerHTML = '<i class="fas fa-download me-2"></i> Install Mobile App';
  btn.style.cssText = `
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 9999;
    background: linear-gradient(135deg, #6366f1 0%, #ec4899 100%);
    color: #ffffff;
    border: none;
    border-radius: 50px;
    padding: 12px 24px;
    font-weight: 600;
    font-size: 14px;
    box-shadow: 0 8px 24px rgba(99, 102, 241, 0.5);
    cursor: pointer;
    display: flex;
    align-items: center;
    transition: transform 0.2s, box-shadow 0.2s;
    font-family: 'Outfit', sans-serif;
  `;
  
  btn.onmouseover = () => { btn.style.transform = 'translateY(-3px)'; };
  btn.onmouseout = () => { btn.style.transform = 'translateY(0)'; };
  
  btn.onclick = triggerInstall;
  document.body.appendChild(btn);
}

function triggerInstall() {
  if (deferredPrompt) {
    deferredPrompt.prompt();
    deferredPrompt.userChoice.then((choiceResult) => {
      if (choiceResult.outcome === 'accepted') {
        console.log('User accepted the install prompt');
        const floatBtn = document.getElementById('floatingPwaBtn');
        if (floatBtn) floatBtn.remove();
        const navBtn = document.getElementById('pwaInstallBtn');
        if (navBtn) navBtn.style.display = 'none';
      }
      deferredPrompt = null;
    });
  } else {
    // If browser doesn't support deferred prompt (e.g. iOS Safari)
    alert('To install this app on your phone:\n1. Tap the Share button (or 3-dots menu in Chrome)\n2. Tap "Add to Home Screen"');
  }
}

window.addEventListener('appinstalled', () => {
  console.log('GymFit App was installed');
  const floatBtn = document.getElementById('floatingPwaBtn');
  if (floatBtn) floatBtn.remove();
});
