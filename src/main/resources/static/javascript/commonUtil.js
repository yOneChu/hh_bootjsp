
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


// 로딩바 제거 함수
function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
}


// 로딩바 표시 함수
function showLoading() {

    console.log('showloading ------------');

    const isDark = document.body.classList.contains('dark-mode');

    // 다크/라이트 모드별 색상 값
    const overlayBg = isDark ? 'rgba(0, 0, 0, 0.6)' : 'rgba(0, 0, 0, 0.5)';
    const panelBg = isDark ? '#0b1220' : '#ffffff';
    const panelText = isDark ? '#e5e7eb' : '#333333';
    const panelShadow = isDark ? '0 4px 10px rgba(0,0,0,0.4)' : '0 4px 6px rgba(0, 0, 0, 0.1)';
    const spinnerBase = isDark ? '#25314a' : '#f3f3f3';
    const spinnerTop = isDark ? '#3b82f6' : '#3498db';

    // 로딩바 HTML 생성
    const loadingHtml = `
        <div id="loadingOverlay" style="
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: ${overlayBg};
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        ">
            <div style="
                background: ${panelBg};
                padding: 30px;
                border-radius: 10px;
                text-align: center;
                box-shadow: ${panelShadow};
                border: 1px solid ${isDark ? '#25314a' : '#e5e7eb'};
            ">
                <div style="
                    border: 4px solid ${spinnerBase};
                    border-top: 4px solid ${spinnerTop};
                    border-radius: 50%;
                    width: 40px;
                    height: 40px;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 15px;
                "></div>
                <p style="margin: 0; font-size: 16px; color: ${panelText};">데이터 분석 중입니다...</p>
            </div>
        </div>
        <style>
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        </style>
    `;

    // 로딩바를 body에 추가
    document.body.insertAdjacentHTML('beforeend', loadingHtml);
}




