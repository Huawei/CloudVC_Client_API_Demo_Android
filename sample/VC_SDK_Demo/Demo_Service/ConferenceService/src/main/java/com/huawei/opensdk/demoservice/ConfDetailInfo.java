package com.huawei.opensdk.demoservice;

import java.util.List;


/**
 * This class is about conference basic information for meeting List query results
 * 会议详情信息类
 * 用于会议详情查询结果
 */
public class ConfDetailInfo extends ConfBaseInfo {
    private List<Member> memberList;

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }
}
