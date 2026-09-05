/* =============================================================================
 * PLM_DOC_MENU — 문서 매뉴얼(readDBFile.html) 왼쪽 사이드바 메뉴 구조 테이블
 *
 *   · 지금까지 api.js 의 SPEC_GROUPS / DB_SPEC_TYPES 배열에 하드코딩되어 있던
 *     "카테고리(대분류) + 명세서(하위 명세서 포함)" 구조를 DB로 옮긴 것이다.
 *   · 본문(마크다운)은 기존처럼 PLM_LLM_METADATA(CATEGORY, CONTENT) 에 저장한다.
 *     PLM_DOC_MENU.ID  ==  PLM_LLM_METADATA.CATEGORY  로 1:1 연결된다.
 *
 *   실행 대상 DB : PLMPRDIF (10.225.80.35)
 *   실행 방법    : SSMS 등에서 아래 스크립트를 한 번만 실행 (재실행해도 안전)
 * ===========================================================================*/

/* ---------------------------------------------------------------------------
 * 1) 테이블 생성
 * -------------------------------------------------------------------------*/
IF OBJECT_ID('dbo.PLM_DOC_MENU', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PLM_DOC_MENU (
        ID           NVARCHAR(100)  NOT NULL,   -- 메뉴 코드 (명세서면 PLM_LLM_METADATA.CATEGORY 와 동일)
        MENU_TYPE    NVARCHAR(10)   NOT NULL,   -- 'GROUP'(대분류) | 'SPEC'(명세서)
        NAME         NVARCHAR(200)  NOT NULL,   -- 화면에 보이는 이름
        ICON         NVARCHAR(50)       NULL,   -- lucide 아이콘 이름
        COLOR        NVARCHAR(20)       NULL,   -- GROUP 전용: blue | violet | amber
        GROUP_ID     NVARCHAR(100)      NULL,   -- SPEC 전용: 소속 대분류 ID
        PARENT_ID    NVARCHAR(100)      NULL,   -- SPEC 전용: 상위 명세서 ID (없으면 최상위)
                                                --   상위의 상위도 가능 — 단계 제한 없이 중첩된다
        SORT_NO      INT            NOT NULL CONSTRAINT DF_PLM_DOC_MENU_SORT   DEFAULT(0),
        READ_URL     NVARCHAR(500)      NULL,   -- 조회 API (NULL 이면 localStorage 목업)
        SAVE_URL     NVARCHAR(500)      NULL,   -- 저장 API (NULL 이면 읽기 전용)
        SAVE_METHOD  NVARCHAR(10)       NULL,   -- 기본 POST
        SAVE_FORMAT  NVARCHAR(10)       NULL,   -- 'form' | 'json'
        SAVE_FIELD   NVARCHAR(50)       NULL,   -- 본문 파라미터명 (예: updatedContent)
        USE_YN       CHAR(1)        NOT NULL CONSTRAINT DF_PLM_DOC_MENU_USE    DEFAULT('Y'),
        CREATE_DATE  DATETIME       NOT NULL CONSTRAINT DF_PLM_DOC_MENU_CDATE  DEFAULT(GETDATE()),
        UPDATE_DATE  DATETIME           NULL,
        CONSTRAINT PK_PLM_DOC_MENU PRIMARY KEY (ID)
    );

    CREATE INDEX IX_PLM_DOC_MENU_TREE ON dbo.PLM_DOC_MENU (MENU_TYPE, GROUP_ID, PARENT_ID, SORT_NO);
END
GO

/* ---------------------------------------------------------------------------
 * 2) 현재 api.js 에 하드코딩되어 있는 메뉴를 그대로 이관 (없는 것만 INSERT)
 * -------------------------------------------------------------------------*/

/* --- 대분류(카테고리) --- */
MERGE dbo.PLM_DOC_MENU AS T
USING (VALUES
    ('db',   'GROUP', N'DB 명세서',  'database',    'blue',   10),
    ('api',  'GROUP', N'API 정의서', 'webhook',     'violet', 20),
    ('rule', 'GROUP', N'기타 규칙',  'scroll-text', 'amber',  30)
) AS S (ID, MENU_TYPE, NAME, ICON, COLOR, SORT_NO)
   ON T.ID = S.ID
WHEN NOT MATCHED THEN
    INSERT (ID, MENU_TYPE, NAME, ICON, COLOR, SORT_NO)
    VALUES (S.ID, S.MENU_TYPE, S.NAME, S.ICON, S.COLOR, S.SORT_NO);
GO

/* --- 명세서 ---
 * READ_URL / SAVE_URL 은 기존 api.js 값과 동일하게 넣는다.
 * (NULL 이면 화면에서 localStorage 목업으로 동작 — 기존 동작 그대로) */
MERGE dbo.PLM_DOC_MENU AS T
USING (VALUES
    /* ---------------- DB 명세서 ---------------- */
    ('ECO_WORKFLOW',        N'ECO 검증-WorkFlow', 'git-branch',     'db',   NULL,
     '/api/getLogicVerifyAsDB?key=subae&type=ECO_VERIFY',
     '/api/update_PLM_DB_MetaData?key=subae&type=ECO_VERIFY',                  10),
    ('LOGIC_WORKFLOW',      N'로직 검증-WorkFlow', 'workflow',      'db',   NULL,
     '/api/getLogicVerifyAsDB?key=subae&type=LOGIC_VERIFY_WORKFLOW',
     '/api/update_PLM_DB_MetaData?key=subae&type=LOGIC_VERIFY_WORKFLOW',       20),
    ('ECO_VERIFY',          N'ECO 검증',          'shield-check',   'db',   NULL,
     NULL, NULL,                                                              30),
    ('LOGIC_WRITE',         N'로직 작성',         'pen-tool',       'db',   NULL,
     '/api/getLogicVerifyAsDB?key=subae&type=LOGIC_WRITE',
     '/api/update_PLM_DB_MetaData?key=subae&type=LOGIC_WRITE',                 40),
    ('WRITE_KYH',           N'작성_김영환',       'file-text',      'db',   'LOGIC_WRITE',
     '/api/getLogicVerifyAsDB?key=subae&type=WRITE_KYH',
     '/api/update_PLM_DB_MetaData?key=subae&type=WRITE_KYH',                   10),
    ('WRITE_KJH',           N'작성_김지현',       'file-text',      'db',   'LOGIC_WRITE',
     '/api/getLogicVerifyAsDB?key=subae&type=WRITE_KJH',
     '/api/update_PLM_DB_MetaData?key=subae&type=WRITE_KJH',                   20),
    ('WRITE_LJY',           N'작성_이지은',       'file-text',      'db',   'LOGIC_WRITE',
     '/api/getLogicVerifyAsDB?key=subae&type=WRITE_LJY',
     '/api/update_PLM_DB_MetaData?key=subae&type=WRITE_LJY',                   30),
    ('LOGIC_VERIFY',        N'로직 검증',         'check-check',    'db',   NULL,
     '/api/getLogicVerifyAsDB?key=subae&type=LOGIC_VERIFY',
     '/api/update_PLM_DB_MetaData?key=subae&type=LOGIC_VERIFY',                50),
    ('LOGIC_VERIFY_SAMPLE', N'샘플 명세서',       'file-text',      'db',   'LOGIC_VERIFY',
     NULL, NULL,                                                              10),
    ('VERIFY_SDJ',          N'검증_서동기',       'check-check',    'db',   'LOGIC_VERIFY',
     '/api/getLogicVerifyAsDB?key=subae&type=VERIFY_SEO',
     '/api/update_PLM_DB_MetaData?key=subae&type=VERIFY_SEO',                  20),
    ('VERIFY_SJW',          N'검증_손정원',       'check-check',    'db',   'LOGIC_VERIFY',
     '/api/getLogicVerifyAsDB?key=subae&type=VERIFY_SEO',
     '/api/update_PLM_DB_MetaData?key=subae&type=VERIFY_SEO',                  30),

    /* ---------------- API 정의서 ---------------- */
    ('API_COMMON',          N'공통 API 규격',     'plug',           'api',  NULL, NULL, NULL, 10),
    ('API_LOGIC',           N'수배로직 API',      'network',        'api',  NULL, NULL, NULL, 20),
    ('API_ERROR',           N'에러 코드 정의',    'alert-circle',   'api',  NULL, NULL, NULL, 30),

    /* ---------------- 기타 규칙 ---------------- */
    ('RULE_CODING',         N'코딩 컨벤션',       'braces',         'rule', NULL, NULL, NULL, 10),
    ('RULE_NAMING',         N'명명 규칙',         'case-sensitive', 'rule', NULL, NULL, NULL, 20),
    ('RULE_DEPLOY',         N'배포 / 운영 규칙',  'rocket',         'rule', NULL, NULL, NULL, 30)
) AS S (ID, NAME, ICON, GROUP_ID, PARENT_ID, READ_URL, SAVE_URL, SORT_NO)
   ON T.ID = S.ID
WHEN NOT MATCHED THEN
    INSERT (ID, MENU_TYPE, NAME, ICON, GROUP_ID, PARENT_ID, READ_URL, SAVE_URL,
            SAVE_METHOD, SAVE_FORMAT, SAVE_FIELD, SORT_NO)
    VALUES (S.ID, 'SPEC', S.NAME, S.ICON, S.GROUP_ID, S.PARENT_ID, S.READ_URL, S.SAVE_URL,
            'POST', 'form', 'updatedContent', S.SORT_NO);
GO

/* ---------------------------------------------------------------------------
 * 3) 카테고리 고유 주소 채우기
 *    화면 상단 'API 링크' 카드에서 복사해 쓰는 주소.
 *    그 카테고리에 속한 문서 목록 + 문서별 조회/저장 주소를 돌려준다.
 * -------------------------------------------------------------------------*/
UPDATE dbo.PLM_DOC_MENU
   SET READ_URL = '/api/docmenu/group/' + ID + '?key=subae'
 WHERE MENU_TYPE = 'GROUP'
   AND (READ_URL IS NULL OR READ_URL = '');
GO

/* ---------------------------------------------------------------------------
 * 4) 확인
 * -------------------------------------------------------------------------*/
SELECT ID, MENU_TYPE, NAME, GROUP_ID, PARENT_ID, SORT_NO, READ_URL
  FROM dbo.PLM_DOC_MENU
 ORDER BY MENU_TYPE DESC, GROUP_ID, ISNULL(PARENT_ID, ID), SORT_NO;
GO
