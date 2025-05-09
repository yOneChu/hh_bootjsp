package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Setter
@Getter
@Table(name = "block_history")
public class BlockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_history_id")
    private Long id;

    private String blockNo;
    private String version; //버전
    private String blockName;
    private String levelFlag;
    private String floor_part; //증별부품
    private String gc_product; //제품군
    private String block_opt; //품목구분
    private String uom;
    private String origin_div; //자재유형 - 외주(ROH)

    private String partname_manager;  //부품명관리
    private String modifyuser; //수정자
    private String lossrate;   //로스율
    private String material_check; //재질관리
    private String block_status; //활성상태
    private String drawingonly; //자재번호 사용 불가

    private String pickName1;
    private String pickName2;
    private String pickName3;
    private String pickName4;
    private String pickName5;
    private String pickName6;
    private String pickName7;
    private String pickName8;
    private String pickName9;
    private String pickName10;
    private String pickName11;
    private String pickName12;
    private String pickName13;
    private String pickName14;
    private String pickName15;
    private String pickName16;
    private String pickName17;
    private String pickName18;
    private String pickName19;
    private String pickName20;
    private String pickName21;
    private String pickName22;
    private String pickName23;
    private String pickName24;
    private String pickName25;
    private String pickName26;
    private String pickName27;
    private String pickName28;
    private String pickName29;
    private String pickName30;

    private String pick1;
    private String pick2;
    private String pick3;
    private String pick4;
    private String pick5;
    private String pick6;
    private String pick7;
    private String pick8;
    private String pick9;
    private String pick10;
    private String pick11;
    private String pick12;
    private String pick13;
    private String pick14;
    private String pick15;
    private String pick16;
    private String pick17;
    private String pick18;
    private String pick19;
    private String pick20;
    private String pick21;
    private String pick22;
    private String pick23;
    private String pick24;
    private String pick25;
    private String pick26;
    private String pick27;
    private String pick28;
    private String pick29;
    private String pick30;

    private String qty1;
    private String qty2;
    private String qty3;
    private String qty4;
    private String qty5;
    private String qty6;
    private String qty7;
    private String qty8;
    private String qty9;
    private String qty10;
    private String qty11;
    private String qty12;
    private String qty13;
    private String qty14;
    private String qty15;
    private String qty16;
    private String qty17;
    private String qty18;
    private String qty19;
    private String qty20;
    private String qty21;
    private String qty22;
    private String qty23;
    private String qty24;
    private String qty25;
    private String qty26;
    private String qty27;
    private String qty28;
    private String qty29;
    private String qty30;

    private String cmt1;
    private String cmt2;
    private String cmt3;
    private String cmt4;
    private String cmt5;
    private String cmt6;
    private String cmt7;
    private String cmt8;
    private String cmt9;
    private String cmt10;
    private String cmt11;
    private String cmt12;
    private String cmt13;
    private String cmt14;
    private String cmt15;
    private String cmt16;
    private String cmt17;
    private String cmt18;
    private String cmt19;
    private String cmt20;
    private String cmt21;
    private String cmt22;
    private String cmt23;
    private String cmt24;
    private String cmt25;
    private String cmt26;
    private String cmt27;
    private String cmt28;
    private String cmt29;
    private String cmt30;

    private String color1;
    private String color2;
    private String color3;
    private String color4;
    private String color5;
    private String color6;
    private String color7;
    private String color8;
    private String color9;
    private String color10;
    private String color11;
    private String color12;
    private String color13;
    private String color14;
    private String color15;
    private String color16;
    private String color17;
    private String color18;
    private String color19;
    private String color20;
    private String color21;
    private String color22;
    private String color23;
    private String color24;
    private String color25;
    private String color26;
    private String color27;
    private String color28;
    private String color29;
    private String color30;

}
