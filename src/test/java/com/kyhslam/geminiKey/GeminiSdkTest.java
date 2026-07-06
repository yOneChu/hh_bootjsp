package com.kyhslam.geminiKey;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiSdkTest {

    public static void main(String[] args) {
        /*try {
            // 시스템 환경 변수(GOOGLE_API_KEY)에 있는 키를 자동으로 가져와 클라이언트를 초기화합니다.
            Client client = new Client();

            String modelId = "gemini-2.5-flash";
            String prompt = "스프링 부트 백엔드 개발자에게 유용한 AI 활용 팁 3가지만 요약해줘.";

            // API 호출 (추가 옵션이 없을 경우 세 번째 인자는 null)
            GenerateContentResponse response = client.models().generateContent(modelId, prompt, null);

            System.out.println("=== 제미나이 답변 ===");
            // .text() 헬퍼 메서드로 텍스트 답변만 깔끔하게 추출할 수 있습니다.
            System.out.println(response.text());

        } catch (Exception e) {
            System.err.println("SDK 실행 중 오류 발생: " + e.getMessage());
            System.err.println("환경 변수(GOOGLE_API_KEY)가 올바르게 설정되었는지 확인해 주세요.");
        }*/
    }
}
