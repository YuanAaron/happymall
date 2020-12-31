package cn.coderap.enums;

/**
 * 支付方式 枚举
 */
public enum PayMethodEnum {
    WECHAT(1,"微信"),
    ALIPAY(2,"支付宝");

    public final Integer type;
    public final String value;
    PayMethodEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
