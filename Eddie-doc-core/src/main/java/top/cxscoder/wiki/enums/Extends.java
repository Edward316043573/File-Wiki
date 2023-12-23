package top.cxscoder.wiki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Edward
 * @date 2023-12-23 1:43
 * @copyright Copyright (c) 2023 Edward
 */
@Getter
@AllArgsConstructor
public enum Extends {
    // 编辑框类型 1=HTML 2=Markdown 3=PDF 4=DOCX 5= XLSX 6=PPTX
    HTML(1, "html"),
    MARKDOWN(2,"md"),
    PDF(3,"pdf"),
    DOCX(4,"docx"),
    XLSX(5,"xlsx"),
    PPTX(6,"pptx")
    ;

    private final Integer code;
    private final String extendName;

    public static String getExtends(Integer code) {
        for(Extends item: Extends.values()) {
            if(item.getCode().equals(code)) {
                return item.getExtendName();
            }
        }
        return null;
    }
}
