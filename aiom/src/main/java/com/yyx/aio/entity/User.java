package com.yyx.aio.entity;

import com.yyx.aio.common.entity.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class User extends BaseEntity implements UserDetails{
    private Long id;

    private String loginName;

    private Short userType;

    private String userName;

    private Integer errorPass;

    private String password;

    private String email;

    private String mobile;

    private String telephone;

    private Integer state;

    private String userCode;

    private Long userOrg;

    private Integer loginErrorTimes;

    private Integer lockStatus;

    //权限？
    private List<? extends GrantedAuthority> authorities;

    public void setGrantedAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (state == -1)
        {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Short getUserType() {
        return userType;
    }

    public void setUserType(Short userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getErrorPass() {
        return errorPass;
    }

    public void setErrorPass(Integer errorPass) {
        this.errorPass = errorPass;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getUserOrg() {
        return userOrg;
    }

    public void setUserOrg(Long userOrg) {
        this.userOrg = userOrg;
    }

    public Integer getLoginErrorTimes() {
        return loginErrorTimes;
    }

    public void setLoginErrorTimes(Integer loginErrorTimes) {
        this.loginErrorTimes = loginErrorTimes;
    }

    public Integer getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Integer lockStatus) {
        this.lockStatus = lockStatus;
    }

    public void setAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}