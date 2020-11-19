package cn.coderap.enums;

/**
 * 分类 枚举
 * Created by yw
 * 2020-11-19
 */
public enum CategoryEnum {
    ONE(1,"一级大分类"),
    TWO(2,"二级分类"),
    THREE(3,"三级小分类");

    public final Integer type;
    public final String value;

    CategoryEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
