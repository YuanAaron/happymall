package cn.coderap.pojo.vo;

/**
 * 分布式会话中，用于封装uniqueToken和用户信息
 * Created by yw
 * 2021/3/8
 */
public class UsersVO {
    //用户信息
    private String id;
    private String username;
    private String nickname;
    private String face;
    private String sex;
    //分布式中用户会话token
    private String uniqueToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUniqueToken() {
        return uniqueToken;
    }

    public void setUniqueToken(String uniqueToken) {
        this.uniqueToken = uniqueToken;
    }
}
