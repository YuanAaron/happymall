package cn.coderap.enums;

/**
 * 评价等级 枚举
 * Created by yw
 * 2020-11-19
 */
public enum CommentLevelEnum {
    GOOD(1,"好评"),
    NORMAL(2,"中评"),
    BAD(3,"差评");

    public final Integer type;
    public final String value;

    CommentLevelEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
