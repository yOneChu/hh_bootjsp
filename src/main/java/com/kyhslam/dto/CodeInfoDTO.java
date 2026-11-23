package com.kyhslam.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeInfoDTO {

    private String code; //사양
    private String codeName; //사양명
    private String typeName; //특성명
    private String typeVal; //특성값
    private String name;

    // 구분 ELV(육상) / SHIP(선박). 특성코드 사전 팝업 전용이라 다른 API 응답에는 나오지 않게 null 이면 생략
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String div;


}
