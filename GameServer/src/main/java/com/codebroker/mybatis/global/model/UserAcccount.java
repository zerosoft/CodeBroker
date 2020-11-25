package com.codebroker.mybatis.global.model;

/**
 * Table: user_acccount
 */
public class UserAcccount {
    /**
     * Column: sid
     */
    private Long sid;

    /**
     * Column: account
     */
    private String account;

    /**
     * Column: password
     */
    private String password;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }
}