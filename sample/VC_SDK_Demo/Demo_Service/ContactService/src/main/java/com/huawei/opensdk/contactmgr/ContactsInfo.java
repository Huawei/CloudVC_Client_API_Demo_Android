package com.huawei.opensdk.contactmgr;

import java.io.Serializable;

/**
 * This class is about personal contact information.
 * 联系人信息类
 */
public class ContactsInfo implements Serializable {

    /**
     * User name
     */
    private String name;

    /**
     * Phone
     */
    private String phone;

    /**
     * Mobile phone
     */
    private String mobilePhone;

    /**
     * Office phone
     */
    private String officePhone;

    /**
     * Email
     */
    private String email;

    /**
     * Address
     */
    private String address;

    /**
     * Contact id
     */
    private String id;

    public ContactsInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
