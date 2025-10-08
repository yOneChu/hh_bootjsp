

// 'YYYYMMDD'를 'YYYY-MM-DD'로 변환
function formatDate(dateStr) {
    if (!/^\d{8}$/.test(dateStr)) {
        throw new Error("입력값은 8자리 숫자 문자열이어야 합니다. 예: 20251008");
    }

    const year = dateStr.substring(0, 4);
    const month = dateStr.substring(4, 6);
    const day = dateStr.substring(6, 8);

    return `${year}-${month}-${day}`;
}