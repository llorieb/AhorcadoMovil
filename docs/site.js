(() => {
  // GitHub Pages will normally be served as:
  // https://OWNER.github.io/REPOSITORY/
  // We derive the repository link automatically.
  const host = window.location.hostname;
  const pathParts = window.location.pathname.split("/").filter(Boolean);

  let repoUrl = "#";

  if (host.endsWith(".github.io") && pathParts.length > 0) {
    const owner = host.replace(".github.io", "");
    const repo = pathParts[0];
    repoUrl = `https://github.com/${owner}/${repo}`;
  }

  const repoLink = document.getElementById("repoLink");
  if (repoLink && repoUrl !== "#") {
    repoLink.href = repoUrl;
  }

  // Once the Google Play listing is public, paste the final URL here.
  // Example:
  // https://play.google.com/store/apps/details?id=com.llorieb.ahorcado
  const playStoreUrl = "";

  for (const id of ["playButton", "playButtonBottom"]) {
    const button = document.getElementById(id);
    if (!button) continue;

    if (playStoreUrl) {
      button.href = playStoreUrl;
      button.classList.remove("store-button-disabled");
      button.removeAttribute("aria-disabled");
      const small = button.querySelector("small");
      if (small) small.textContent = "Disponible en";
    } else {
      button.addEventListener("click", event => event.preventDefault());
    }
  }
})();
