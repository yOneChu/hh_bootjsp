
// 다크모드: 저장된 테마 적용
const savedTheme = localStorage.getItem('theme');
const $themeLabel = $('label[for="darkModeToggle"]');

const updateThemeLabel = function(isDark) {
    if (isDark) {
        $themeLabel.text('🌙 다크모드');
    } else {
        $themeLabel.text('☀️ 라이트모드');
    }
};

if (savedTheme === 'dark') {
    $('body').addClass('dark-mode');
    $('#darkModeToggle').prop('checked', true);
    updateThemeLabel(true);
} else {
    $('#darkModeToggle').prop('checked', false);
    updateThemeLabel(false);
}

// 다크모드 토글 스위치 핸들러
$('#darkModeToggle').on('change', function() {
    const willBeDark = $(this).is(':checked');
    if (willBeDark) {
        $('body').addClass('dark-mode');
        localStorage.setItem('theme', 'dark');
    } else {
        $('body').removeClass('dark-mode');
        localStorage.setItem('theme', 'light');
    }
    updateThemeLabel(willBeDark);
});


