# -*- coding: utf-8 -*-
"""
CycleFunc.py

PLM 자동설계 기본 사이클을 이 파일 하나로 수행하는 단독 실행 모듈.

  1. PLM 로그인      : run_login()
  2. WIP 생성        : run_make_wip()
  3. 종속사양 산출    : run_jongsoksung()
  4. BOM 계산        : run_bom_calculation()

  - 기능별 개별 실행 가능
  - 한 번에 실행 : run_cycle() / run_cycle_list() / run_all()

별도 기능(자동설계 사이클과 무관하게 단독 사용)
  * 동일정보 만들기  : run_equal_info_make() / run_equal_info_make_list() / run_equal_info()
  * 속성정보 변경    : run_attr_change() / run_attr_change_list() / run_attr_change_all()

PLM_AUTO_FUNCTION 을 import 하지 않고 필요한 로직을 내부에 모두 포함한다.
"""

import ast
import json
import re
import time

import requests
from bs4 import BeautifulSoup as bs
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager


# ----------------------------------------------------------------------
# 기본 설정
# ----------------------------------------------------------------------
PLM_ID = '2035570'          # PLM 사번
PLM_PW = 'gel1375a!'        # PLM 비밀번호

# BOM 계산 시 기본으로 계산할 파트
#   c : 카, m : 기계, f : 승장, 1/2/3 : 전기 파트
DEFAULT_CAL_PART_LIST = ['c', 'm', 'f', '1', '2', '3']

# PLM 서버 URL
LOGIN_URL = 'http://plmpro.hdel.co.kr/jsp/login/JsLogin.jsp'
OUID_URL = 'https://plmpro.hdel.co.kr/jsp/help/ouidList.jsp?md%24number='
OBJECT_URL = 'http://plmpro.hdel.co.kr/Object.do'
SALES_OBJECT_URL = 'http://plmpro.hdel.co.kr/SalesObject.do'
SUBAE_MANAGER_URL = 'http://plmpro.hdel.co.kr/SubaeManager.do'
ELV_INFO_PAGE_URL = 'http://plmpro.hdel.co.kr/jsp/plmetc/elvinfo/elvinfomation.jsp?iOuid='

# PLM 액션 OUID (PLM 화면의 버튼에 해당하는 고유 ID)
ACTION_APPROVAL = '9504674a'        # 공사정보 승인
ACTION_JONGSOKSUNG = '9507f844'     # 종속사양 산출
CLASS_ELV_INFO = '860cebeb'         # 공사정보 클래스 OUID (동일정보 생성 시 사용)

# 동작 관련 상수
SESSION_WAIT = 10           # 쿠키 동기화 대기(초). PLM 세션이 자리잡는 데 필요
JONGSOK_REPEAT = 3          # 종속사양 산출 반복 횟수(사양이 수렴하도록 여러 번 수행)
MAX_RETRY = 3               # 실패 시 재로그인 후 재시도 최대 횟수

# 재로그인 시 사용할 계정 (run_login 에서 마지막으로 로그인한 계정을 기억)
_LAST_ID = PLM_ID
_LAST_PW = PLM_PW


# ======================================================================
# 내부 공용 함수
# ======================================================================
def _get_ouid(project_no, retry=5):
    """공사번호로 PLM 내부 OUID를 조회한다.

    PLM의 모든 요청은 공사번호가 아니라 OUID로 대상을 지정하므로
    작업 전에 반드시 이 값을 먼저 얻어야 한다.

    반환 : {'elv_info': 공사정보 OUID, 'product': 제품 OUID,
            'dec_no': 제품 OUID 10진수, 'url': 조회 URL}
           조회 실패 시 값들은 None
    """
    url = OUID_URL + project_no
    html = ''

    for i in range(1, retry + 1):
        try:
            res = requests.get(url, timeout=3)
            html = res.text
        except requests.exceptions.RequestException as e:
            print('[OUID] ' + str(i) + '번째 요청 실패 : ' + str(e))
            time.sleep(0.5)
            continue

        if 'elv_info' in html:
            break
        print('[OUID] ' + str(i) + '번 ouid 출력 오류')
        time.sleep(0.8)

    result = {'elv_info': None, 'product': None, 'dec_no': None, 'url': url}
    if 'elv_info' not in html:
        print('[OUID] ' + project_no + ' : elv_info를 찾을 수 없습니다.')
        return result

    token_list = html.split('\n')

    # 'elv_info$vf@xxxxxxxx' 형태에서 접두어를 잘라내고 OUID만 추출
    try:
        result['elv_info'] = [s for s in token_list if 'elv_info$vf@' in s][0][12:]
    except IndexError:
        pass

    try:
        product_vf = [s for s in token_list if 'product$vf@' in s][0][11:]
        result['product'] = product_vf
        result['dec_no'] = str(int(product_vf, 16))     # 16진수 OUID -> 10진수
    except (IndexError, ValueError):
        pass

    return result


def _session(driver):
    """selenium 로그인 세션의 쿠키를 requests 세션으로 옮겨 담는다.

    화면 조작 대신 POST 요청으로 PLM 기능을 호출하기 위해 필요하다.
    """
    s = requests.Session()
    time.sleep(SESSION_WAIT)

    while True:
        try:
            cookies = driver.get_cookies()
            if cookies:
                for cookie in cookies:
                    s.cookies.set(cookie['name'], cookie['value'])
                break
            time.sleep(1)
        except Exception as e:
            print('[SESSION] 쿠키 획득 예외 : ' + str(e))
            time.sleep(1)

    return s


def _relogin():
    """작업 도중 세션이 끊겼을 때 마지막 로그인 계정으로 다시 로그인한다."""
    print('[RELOGIN] 세션 재로그인 : ' + _LAST_ID)
    return run_login(_LAST_ID, _LAST_PW)


def _fix_encoding(res):
    """응답에 charset이 명시되지 않은 경우에만 인코딩을 추정해서 한글 깨짐을 막는다."""
    content_type = res.headers.get('Content-Type', '') or ''
    if 'charset' not in content_type.lower():
        res.encoding = res.apparent_encoding
    return res


def _parse_plm_response(text):
    """PLM 응답 문자열을 dict로 변환한다.

    응답이 JSON이 아닌 파이썬 dict 표기로 올 때가 있어 두 방식을 순서대로 시도한다.
    """
    try:
        return json.loads(text)
    except (ValueError, TypeError):
        pass

    try:
        return ast.literal_eval(text)
    except (ValueError, SyntaxError):
        print('[PARSE] 응답 해석 실패(앞 500자) : ' + str(text)[:500])
        return {}


def _code_value(session, ouid, code_name):
    """공사정보 화면에서 특성코드 하나의 값을 읽는다.

    session : 로그인 쿠키가 담긴 requests 세션 (_session 으로 생성)
    ouid    : 'elv_info$vf@xxxxxxxx' 형태의 전체 OUID

    pandas.read_html 은 lxml/html5lib 이 필요해서 이 환경에서는 쓸 수 없으므로
    BeautifulSoup 으로 직접 표를 훑는다.
    """
    url = ELV_INFO_PAGE_URL + ouid + '&cOuid=' + CLASS_ELV_INFO

    try:
        res = _fix_encoding(session.get(url))
        soup = bs(res.text, 'html.parser')
        rows = [[cell.get_text(strip=True) for cell in tr.find_all(['th', 'td'])]
                for tr in soup.find_all('tr')]
    except Exception as e:
        print('[CODE] ' + str(code_name) + ' 페이지 조회 실패 : ' + str(e))
        return None

    # 헤더 행에서 '특성코드' / '특성값' 열의 위치를 찾는다
    code_idx = value_idx = None
    for row in rows:
        if '특성코드' in row and '특성값' in row:
            code_idx = row.index('특성코드')
            value_idx = row.index('특성값')
            break

    if code_idx is None:
        print('[CODE] 특성코드 표를 찾지 못했습니다 : ' + url)
        return None

    # 원하는 특성코드가 있는 행의 특성값을 반환
    for row in rows:
        if len(row) > max(code_idx, value_idx) and row[code_idx] == code_name:
            return row[value_idx]

    print('[CODE] ' + str(code_name) + ' 값을 찾지 못했습니다')
    return None


# ======================================================================
# 1. 로그인
# ======================================================================
def run_login(pdmid=PLM_ID, pdmpw=PLM_PW):
    """PLM에 로그인하고 selenium driver를 반환한다.

    headless 크롬으로 로그인 페이지에 접속해 사번/비밀번호를 입력하고,
    이후 모든 기능은 이 driver의 세션 쿠키를 사용한다.
    """
    global _LAST_ID, _LAST_PW
    _LAST_ID, _LAST_PW = pdmid, pdmpw

    print('[LOGIN] PLM 로그인 시도 : ' + pdmid)

    chrome_options = Options()
    chrome_options.add_argument('headless')
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')
    chrome_options.add_experimental_option('excludeSwitches', ['enable-logging'])
    chrome_options.add_argument('window-size=1920x1080')
    chrome_options.add_argument('lang=ko_KR')

    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()),
                              options=chrome_options)

    driver.get(LOGIN_URL)
    driver.maximize_window()
    driver.implicitly_wait(3)

    # 로그인 폼 입력 후 로그인 버튼 클릭
    driver.find_element(By.CSS_SELECTOR, 'input#_easyui_textbox_input1').send_keys(pdmid)
    driver.find_element(By.CSS_SELECTOR, 'input#_easyui_textbox_input2').send_keys(pdmpw)
    driver.find_element(
        By.CSS_SELECTOR, 'a[href="javascript:login();"] > div.login_btn.mont').click()

    time.sleep(2)
    driver.implicitly_wait(3)

    # 로그인 직후 공지 팝업창이 뜨면 닫고 원래 창으로 돌아온다
    if len(driver.window_handles) == 2:
        driver.switch_to.window(driver.window_handles[-1])
        driver.close()
        driver.switch_to.window(driver.window_handles[0])

    print('[LOGIN] PLM 로그인 완료')
    return driver


def close_driver(driver):
    """사용이 끝난 driver(크롬 프로세스)를 정리한다."""
    try:
        driver.quit()
        print('[CLOSE] 드라이버 종료 완료')
    except Exception as e:
        print('[CLOSE] 드라이버 종료 실패 : ' + str(e))


# ======================================================================
# 2. WIP 생성 (+ 실패 시 공사정보 승인)
# ======================================================================
def run_approval(driver, project_no):
    """공사정보를 승인한다.

    WIP 생성은 승인된 공사정보에서만 가능하므로,
    WIP 생성이 실패하면 이 함수를 먼저 수행한 뒤 재시도한다.
    """
    for i in range(1, MAX_RETRY + 1):
        ouid = _get_ouid(project_no)
        if not ouid['elv_info']:
            print('[APPROVAL] ' + project_no + ' OUID 조회 실패')
            time.sleep(3)
            continue

        payload = {'cmd': 'executeAction',
                   'objectOuid': 'elv_info$vf@' + ouid['elv_info'],
                   'actionOuid': ACTION_APPROVAL}
        res = _session(driver).post(OBJECT_URL, data=payload)

        if res.status_code == 200:
            print('[APPROVAL] ' + project_no + ' 승인 완료!')
            return driver

        print('[APPROVAL] ' + project_no + ' ' + str(i) + '차 승인 실패, 재시도 중...')
        time.sleep(4)
        driver = _relogin()

    print('[APPROVAL] ' + project_no + ' 승인 최종 실패')
    return driver


def run_make_wip(driver, project_no):
    """공사정보의 WIP(작업 중) 버전을 생성한다.

    실패하면 공사정보 승인을 먼저 수행하고, 그래도 안 되면 재로그인 후 재시도한다.
    재로그인 시 driver가 새로 만들어지므로 반환값을 반드시 다시 받아야 한다.
    """
    print('[WIP] ' + project_no + ' WIP 생성 시작')

    for i in range(1, MAX_RETRY + 1):
        ouid = _get_ouid(project_no)
        if not ouid['elv_info']:
            print('[WIP] ' + project_no + ' OUID 조회 실패')
            time.sleep(3)
            continue

        payload = {'cmd': 'makeWip',
                   'objectOuid': 'elv_info$vf@' + ouid['elv_info']}
        try:
            res = _session(driver).post(SALES_OBJECT_URL, data=payload)
        except Exception as e:
            print('[WIP] 요청 예외 : ' + str(e))
            time.sleep(3)
            driver = _relogin()
            continue

        if res.status_code == 200:
            print('[WIP] ' + project_no + ' WIP 생성 완료!')
            return driver

        print('[WIP] ' + project_no + ' ' + str(i) + '차 WIP 생성 실패, 승인 후 재시도...')
        driver = run_approval(driver, project_no)     # 미승인 상태가 원인인 경우 대응
        time.sleep(3)

    print('[WIP] ' + project_no + ' WIP 생성 최종 실패')
    return driver


# ======================================================================
# 3. 종속사양 산출
# ======================================================================
def run_jongsoksung(driver, project_no, repeat=JONGSOK_REPEAT):
    """종속사양을 산출한다.

    한 번만 돌리면 앞 단계에서 바뀐 사양이 뒤 사양에 반영되지 않을 수 있어
    기본 3회 반복 수행한다. 실패 시 재로그인 후 재시도한다.
    """
    print('[SPEC] ' + project_no + ' 종속사양 산출 시작')

    ouid = _get_ouid(project_no)
    if not ouid['elv_info']:
        print('[SPEC] ' + project_no + ' OUID 조회 실패')
        return driver

    payload = {'cmd': 'executeAction',
               'objectOuid': 'elv_info$vf@' + ouid['elv_info'],
               'actionOuid': ACTION_JONGSOKSUNG}

    success = 0     # 성공 횟수
    fail = 0        # 실패 횟수
    while success < repeat:
        message = ''
        try:
            res = _session(driver).post(OBJECT_URL, data=payload)
            message = res.json()['message']
            print('[SPEC] 응답 : ' + str(message))
        except Exception as e:
            print('[SPEC] 요청/응답 예외 : ' + str(e))

        if message == '종속사양산출이 완료되었습니다.':
            success += 1
            print('[SPEC] ' + project_no + ' ' + str(success) + '번째 종속사양산출 완료!')
            continue

        fail += 1
        print('[SPEC] ' + project_no + ' ' + str(fail) + '차 종속사양산출 실패, 재시도 중...')
        time.sleep(4)
        driver = _relogin()
        if fail > MAX_RETRY:
            print('[SPEC] ' + project_no + ' 종속사양산출 최종 실패')
            break

    return driver


# ======================================================================
# 4. BOM 계산
# ======================================================================
def run_bom_calculation(driver, project_no, cal_part_list=None):
    """선택한 파트의 BOM을 계산한다.

    cal_part_list 를 넘기지 않으면 DEFAULT_CAL_PART_LIST(전 파트)를 계산한다.
    BOM 계산 요청 시 OUID는 반드시 소문자여야 한다.
    """
    if cal_part_list is None:
        cal_part_list = DEFAULT_CAL_PART_LIST

    print('[BOM] ' + project_no + ' BOM 계산 시작 : ' + str(cal_part_list))

    for i in range(1, MAX_RETRY + 1):
        ouid = _get_ouid(project_no)
        if not ouid['elv_info']:
            print('[BOM] ' + project_no + ' OUID 조회 실패')
            time.sleep(3)
            continue

        # 조회한 OUID가 요청한 공사번호의 것이 맞는지 확인(다른 호기 BOM 계산 방지)
        match = re.search(r'=(\S+)', ouid['url'])
        if not match or match.group(1) != project_no:
            print('[BOM] 공사번호 불일치, OUID 재조회')
            continue

        payload = {'cmd': 'bomCalStart',
                   'iOuid': 'elv_info$vf@' + ouid['elv_info'].lower()}
        for part in cal_part_list:
            payload['b_' + part] = part     # 계산할 파트 지정

        result = False
        try:
            res = _session(driver).post(SUBAE_MANAGER_URL, data=payload)
            result = res.json()['result']
        except Exception as e:
            print('[BOM] 요청/응답 예외 : ' + str(e))

        if result:
            print('[BOM] ' + project_no + ' BOM 계산 완료!')
            return driver

        print('[BOM] ' + project_no + ' ' + str(i) + '차 BOM 계산 실패, 재시도 중...')
        time.sleep(3)
        driver = _relogin()

    print('[BOM] ' + project_no + ' BOM 계산 최종 실패')
    return driver


# ======================================================================
# [별도 기능] 동일정보 만들기 (TEST 호기 생성)
#   - 자동설계 사이클(run_cycle)과는 무관하게 단독으로 사용한다.
# ======================================================================
def run_equal_info_make(driver, project_no):
    """기존 공사정보의 사양을 그대로 복사해 새 공사정보(TEST 호기)를 만든다.

    처리 순서
      1) 원본 공사정보의 전체 특성값을 조회         (objectInfoAjax)
      2) 신규 등록용으로 가공 (공사번호/OUID 제거)
      3) 새 공사정보로 등록                        (registObject)
      4) PLM이 새로 채번한 공사번호(TEST 번호)를 조회

    반환 : [신규 OUID, 신규 공사번호]  / 실패 시 [None, None]
    """
    print('[EQUAL] ' + project_no + ' 동일정보 생성 시작')

    for i in range(1, MAX_RETRY + 1):
        ouid = _get_ouid(project_no)
        if not ouid['elv_info']:
            print('[EQUAL] ' + project_no + ' OUID 조회 실패')
            time.sleep(3)
            continue

        session = _session(driver)

        # 1) 원본 공사정보의 모든 특성값을 dict로 받아온다
        res = session.post(SALES_OBJECT_URL,
                           data={'cmd': 'objectInfoAjax',
                                 'objectOuid': 'elv_info$vf@' + ouid['elv_info']})
        _fix_encoding(res)
        print('[EQUAL] 원본 사양 조회 응답 : status=' + str(res.status_code)
              + ', length=' + str(len(res.text)))
        source_info = _parse_plm_response(res.text)
        if not source_info:
            print('[EQUAL] ' + project_no + ' ' + str(i) + '차 원본 사양 조회 실패, 재시도 중...')
            time.sleep(3)
            continue

        # 2) 받아온 사양을 신규 등록용 payload로 가공
        payload = {}
        for key, value in source_info.items():
            if 'name@' in key:
                continue                # 화면 표시용 항목이라 등록 시에는 불필요
            if key in ('md$number', 'ouid'):
                continue                # 공사번호는 PLM이 새로 채번, OUID는 신규 생성
            if value is None:
                continue                # 값 없는 항목은 전송 대상에서 제외
            # 폼 전송이 가능하도록 문자열이 아닌 값은 문자열로 변환
            payload[key] = value if isinstance(value, str) else str(value)

        payload['cmd'] = 'registObject'
        payload['classOuid'] = CLASS_ELV_INFO

        # 3) 새 공사정보 등록
        res = _fix_encoding(session.post(SALES_OBJECT_URL, data=payload))
        time.sleep(3)
        print('[EQUAL] 동일정보 등록 응답 : status=' + str(res.status_code)
              + ', body=' + res.text[:300])
        new_info = _parse_plm_response(res.text)
        new_ouid = new_info.get('iOuid')
        if not new_ouid:
            print('[EQUAL] ' + project_no + ' ' + str(i) + '차 동일정보 등록 실패, 재시도 중...')
            time.sleep(3)
            continue

        # 4) 새로 채번된 공사번호(TEST 번호) 확인
        new_project_no = _code_value(session, new_ouid, 'md$number')
        print('[EQUAL] ' + project_no + ' 동일정보 생성 완료! -> '
              + str(new_project_no) + ' (' + str(new_ouid) + ')')
        return [new_ouid, new_project_no]

    print('[EQUAL] ' + project_no + ' 동일정보 생성 최종 실패')
    return [None, None]


def run_equal_info_make_list(driver, project_no_list):
    """여러 호기의 동일정보를 순서대로 만든다.

    반환 : [[원본 공사번호, 신규 OUID, 신규 공사번호], ...]
    """
    result_list = []

    for idx, project_no in enumerate(project_no_list, start=1):
        print('[PROGRESS] ' + str(idx) + '/' + str(len(project_no_list)) + ' : ' + project_no)
        try:
            new_ouid, new_project_no = run_equal_info_make(driver, project_no)
        except Exception as e:
            print('[ERROR] ' + project_no + ' 동일정보 생성 실패 : ' + str(e))
            new_ouid, new_project_no = None, None
        result_list.append([project_no, new_ouid, new_project_no])
        time.sleep(0.8)     # PLM 서버 부하 방지용 간격

    return result_list


def run_equal_info(project_no_list, pdmid=PLM_ID, pdmpw=PLM_PW, quit_driver=True):
    """로그인부터 동일정보 생성까지 한 번에 수행한다(단독 실행용 진입 함수).

    project_no_list : 공사번호 문자열 또는 공사번호 리스트
    반환            : [[원본 공사번호, 신규 OUID, 신규 공사번호], ...]
    """
    if isinstance(project_no_list, str):
        project_no_list = [project_no_list]

    driver = run_login(pdmid, pdmpw)
    result_list = []
    try:
        result_list = run_equal_info_make_list(driver, project_no_list)
    finally:
        if quit_driver:
            close_driver(driver)

    print('[RESULT] 동일정보 생성 결과 : ' + str(result_list))
    return result_list


# ======================================================================
# [별도 기능] 호기 속성정보 변경
#   - 바꿀 속성을 {특성코드: 값} 딕셔너리로 넘기면 몇 개든 한 번에 변경한다.
#   - 자주 쓰는 특성코드
#       designer        : 담당 설계자 사번 (여러 명이면 리스트 또는 콤마 구분)
#       MANAGER_M       : 기계 담당자명
#       MANAGER_E       : 전기 담당자명
#       md$user         : 담당자명
#       md$description  : 현장명
# ======================================================================
def _to_form_value(value):
    """속성값을 폼 전송용 문자열로 바꾼다.

    designer 처럼 여러 값을 넣는 속성은 리스트로 넘기면 콤마로 이어 붙인다.
    """
    if isinstance(value, (list, tuple, set)):
        return ','.join(str(v) for v in value)
    return value if isinstance(value, str) else str(value)


def run_attr_change(driver, project_no, attr_dict, verify=False):
    """호기(공사정보)의 속성정보를 변경한다.

    project_no : 공사번호
    attr_dict  : 바꿀 속성 {특성코드: 값}
                 예) {'designer': '2035570'}
                     {'designer': ['2035570', '2014718'], 'MANAGER_E': '오찬석'}
    verify     : True면 변경 후 값을 다시 읽어 반영 여부를 확인한다.

    반환 : 성공 True / 실패 False
    """
    if not attr_dict:
        print('[ATTR] ' + project_no + ' 변경할 속성이 없습니다')
        return False

    print('[ATTR] ' + project_no + ' 속성정보 변경 시작 : ' + str(attr_dict))

    for i in range(1, MAX_RETRY + 1):
        ouid = _get_ouid(project_no)
        if not ouid['elv_info']:
            print('[ATTR] ' + project_no + ' OUID 조회 실패')
            time.sleep(3)
            continue

        # 바꿀 속성들을 그대로 payload에 얹는다(속성 개수 제한 없음)
        payload = {'cmd': 'objectUpdate',
                   'objectOuid': 'elv_info$vf@' + ouid['elv_info']}
        for key, value in attr_dict.items():
            payload[key] = _to_form_value(value)

        session = _session(driver)
        try:
            res = _fix_encoding(session.post(OBJECT_URL, data=payload))
        except Exception as e:
            print('[ATTR] 요청 예외 : ' + str(e))
            time.sleep(3)
            driver = _relogin()
            continue

        if res.status_code != 200:
            print('[ATTR] ' + project_no + ' ' + str(i) + '차 변경 실패(status='
                  + str(res.status_code) + '), 재시도 중...')
            time.sleep(3)
            driver = _relogin()
            continue

        # PLM이 메시지를 돌려주면 함께 출력한다
        message = _parse_plm_response(res.text).get('message')
        print('[ATTR] ' + project_no + ' 속성정보 변경 완료!'
              + (' : ' + str(message) if message else ''))

        # 변경된 값이 실제로 반영됐는지 다시 읽어 확인
        if verify:
            full_ouid = 'elv_info$vf@' + ouid['elv_info']
            for key, value in attr_dict.items():
                current = _code_value(session, full_ouid, key)
                print('[ATTR] 확인 ' + str(key) + ' = ' + str(current)
                      + ' (요청값 : ' + _to_form_value(value) + ')')

        return True

    print('[ATTR] ' + project_no + ' 속성정보 변경 최종 실패')
    return False


def run_designer_change(driver, project_no, designer, verify=False):
    """담당 설계자(designer)만 바꾸는 단축 함수.

    designer : 사번 문자열 또는 사번 리스트 (예 '2035570' / ['2035570', '2014718'])
    """
    return run_attr_change(driver, project_no, {'designer': designer}, verify=verify)


def run_attr_change_list(driver, project_no_list, attr_dict, verify=False):
    """여러 호기의 속성정보를 같은 값으로 변경한다.

    반환 : [[공사번호, 성공여부], ...]
    """
    result_list = []

    for idx, project_no in enumerate(project_no_list, start=1):
        print('[PROGRESS] ' + str(idx) + '/' + str(len(project_no_list)) + ' : ' + project_no)
        try:
            ok = run_attr_change(driver, project_no, attr_dict, verify=verify)
        except Exception as e:
            print('[ERROR] ' + project_no + ' 속성정보 변경 실패 : ' + str(e))
            ok = False
        result_list.append([project_no, ok])
        time.sleep(0.8)     # PLM 서버 부하 방지용 간격

    fail_list = [no for no, ok in result_list if not ok]
    if fail_list:
        print('[RESULT] 속성정보 변경 실패 호기 : ' + str(fail_list))
    else:
        print('[RESULT] 전체 호기 속성정보 변경 완료')

    return result_list


def run_attr_change_all(project_no_list, attr_dict, pdmid=PLM_ID, pdmpw=PLM_PW,
                        verify=False, quit_driver=True):
    """로그인부터 속성정보 변경까지 한 번에 수행한다(단독 실행용 진입 함수).

    project_no_list : 공사번호 문자열 또는 공사번호 리스트
    attr_dict       : 바꿀 속성 {특성코드: 값}
    반환            : [[공사번호, 성공여부], ...]
    """
    if isinstance(project_no_list, str):
        project_no_list = [project_no_list]

    driver = run_login(pdmid, pdmpw)
    result_list = []
    try:
        result_list = run_attr_change_list(driver, project_no_list, attr_dict, verify=verify)
    finally:
        if quit_driver:
            close_driver(driver)

    return result_list


# ======================================================================
# 5. 통합 실행 (한 호기)
# ======================================================================
def run_cycle(driver, project_no, cal_part_list=None):
    """한 호기에 대해 WIP 생성 -> 종속사양 산출 -> BOM 계산을 순서대로 수행한다.

    로그인은 포함하지 않는다(여러 호기에서 driver를 재사용하기 위함).
    로그인까지 한 번에 하려면 run_all() 을 사용한다.
    """
    print('=' * 60)
    print('[CYCLE] ' + project_no + ' 사이클 시작')
    print('=' * 60)

    driver = run_make_wip(driver, project_no)
    driver = run_jongsoksung(driver, project_no)
    driver = run_bom_calculation(driver, project_no, cal_part_list)

    print('[CYCLE] ' + project_no + ' 사이클 완료')
    return driver


# ======================================================================
# 6. 통합 실행 (여러 호기)
# ======================================================================
def run_cycle_list(driver, project_no_list, cal_part_list=None):
    """여러 호기에 대해 run_cycle 을 반복 수행한다.

    한 호기에서 예외가 나더라도 나머지 호기는 계속 진행하고,
    마지막에 실패한 호기 목록을 출력/반환한다.
    """
    fail_list = []

    for idx, project_no in enumerate(project_no_list, start=1):
        print('[PROGRESS] ' + str(idx) + '/' + str(len(project_no_list)) + ' : ' + project_no)
        try:
            driver = run_cycle(driver, project_no, cal_part_list)
        except Exception as e:
            print('[ERROR] ' + project_no + ' 처리 실패 : ' + str(e))
            fail_list.append(project_no)
        time.sleep(0.8)     # PLM 서버 부하 방지용 간격

    if fail_list:
        print('[RESULT] 실패 호기 : ' + str(fail_list))
    else:
        print('[RESULT] 전체 호기 정상 처리 완료')

    return driver, fail_list


# ======================================================================
# 7. 로그인 + 전체 사이클 (가장 바깥쪽 진입 함수)
# ======================================================================
def run_all(project_no_list, pdmid=PLM_ID, pdmpw=PLM_PW,
            cal_part_list=None, quit_driver=True):
    """로그인부터 BOM 계산까지 4가지 기능을 한 번에 수행한다.

    project_no_list : 공사번호 문자열 또는 공사번호 리스트
    quit_driver     : True면 작업 종료 후 크롬 드라이버를 자동 종료
    """
    # 공사번호를 하나만 넘겨도 동작하도록 리스트로 변환
    if isinstance(project_no_list, str):
        project_no_list = [project_no_list]

    driver = run_login(pdmid, pdmpw)
    fail_list = []
    try:
        driver, fail_list = run_cycle_list(driver, project_no_list, cal_part_list)
    finally:
        if quit_driver:
            close_driver(driver)

    return fail_list


if __name__ == '__main__':
    # ------------------------------------------------------------------
    # 사용 예시 1) 한 번에 수행
    # ------------------------------------------------------------------
    project_list = ['TEST-630652']
    #run_all(project_list)



    #driver = run_make_wip(driver, 'TEST-630231') 



    # 동일정보 테스트
    # driver = run_login()
    # new_ouid, new_project_no = run_equal_info_make(driver, 'TEST-630652')
    # close_driver(driver)
    # print(new_ouid)
    # print(new_project_no)



    ## 속성정보 테스트
    driver = run_login()
    run_attr_change(driver, 'TEST-630652', {                         # 여러 속성 동시 변경
         #'designer': ['2035570', '2014718'],                        # 여러 명은 리스트로
         'EL_ZFDA': '20260909',  # 기계구조 최초설계
         'EL_ZFDB': '20260911',  # 기계의장 최초설계
         'EL_ZFDC': '20261011', # 전기구조 최초설계
         'EL_ZFDD': '20261015' # 전기_의장 최초 설계
         #'MANAGER_E': '김영환',
     }, verify=True)                    
    close_driver(driver)



    # ------------------------------------------------------------------
    # 사용 예시 2) 기능별 개별 수행
    # ------------------------------------------------------------------
    # driver = run_login()                                   # 1. 로그인
    # driver = run_make_wip(driver, 'TEST-514627')           # 2. WIP 생성
    # driver = run_jongsoksung(driver, 'TEST-514627')        # 3. 종속사양 산출
    # driver = run_bom_calculation(driver, 'TEST-514627',    # 4. BOM 계산
    #                              ['c', 'm', 'f'])
    # close_driver(driver)

    # ------------------------------------------------------------------
    # 사용 예시 3) 동일정보 만들기 (사이클과 별개로 단독 수행)
    # ------------------------------------------------------------------
    # run_equal_info(['200338L01', '200477L01'])             # 로그인 포함
    #
    # driver = run_login()
    # new_ouid, new_project_no = run_equal_info_make(driver, '200338L01')
    # close_driver(driver)

    # ------------------------------------------------------------------
    # 사용 예시 4) 속성정보 변경 (사이클과 별개로 단독 수행)
    # ------------------------------------------------------------------
    # 로그인 포함, 여러 호기를 같은 값으로 변경
    # run_attr_change_all(['200338L01', '200477L01'], {'designer': '2035570'})
    #
    # driver = run_login()
    # run_designer_change(driver, '200338L01', '2035570')            # 담당자만 변경
    # run_attr_change(driver, '200338L01', {                         # 여러 속성 동시 변경
    #     'designer': ['2035570', '2014718'],                        # 여러 명은 리스트로
    #     'MANAGER_E': '오찬석',
    # }, verify=True)                                                # verify=True면 반영 확인
    # close_driver(driver)
