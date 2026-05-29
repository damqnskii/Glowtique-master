(function () {
    const storageKey = 'glowtique-theme';

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem(storageKey, theme);

        document.querySelectorAll('.theme-toggle').forEach((button) => {
            const icon = button.querySelector('.theme-toggle-icon');
            button.setAttribute('aria-pressed', String(theme === 'light'));
            button.setAttribute('title', theme === 'light' ? 'Включи тъмна тема' : 'Включи светла тема');
            button.setAttribute('aria-label', theme === 'light' ? 'Включи тъмна тема' : 'Включи светла тема');
            if (icon) {
                icon.textContent = theme === 'light' ? '☀' : '☾';
            }
        });
    }

    const initialTheme = localStorage.getItem(storageKey) || document.documentElement.getAttribute('data-theme') || 'dark';
    applyTheme(initialTheme);

    document.addEventListener('click', (event) => {
        const button = event.target.closest('.theme-toggle');
        if (!button) {
            return;
        }

        const currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
        applyTheme(currentTheme === 'dark' ? 'light' : 'dark');
    });
})();
