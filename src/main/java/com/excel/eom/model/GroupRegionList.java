package com.excel.eom.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class GroupRegionList {

    private List<GroupRegion> groupRegions = new ArrayList();

    @Data
    @AllArgsConstructor
    private class GroupRegion {
        int groupLevel;
        List<Integer> endPointList;
    }

    /**
     * 그룹의 마지막지점목록 추가
     *
     * @param groupLevel
     * @param endPointList
     * */
    public void addGroupRegion(int groupLevel, List<Integer> endPointList) {
        groupRegions.add(new GroupRegion(groupLevel, endPointList ));
    }

    /**
     * 그룹이 이미 있는가 ?
     *
     * @param groupLevel
     * */
    public boolean isContainGroup(int groupLevel) {
        return findGroupRegion(groupLevel) != null ? true : false;
    }

    /**
     * 그룹의 마지막지점목록 얻기
     *
     * @param groupLevel
     * */
    public List<Integer> findGroupRegionEndPoint(int groupLevel) {
        GroupRegion groupRegion = findGroupRegion(groupLevel);
        return groupRegion != null ? groupRegion.getEndPointList() : new ArrayList<>();
    }

    private GroupRegion findGroupRegion(int groupLevel) {
        return groupRegions.stream()
                .filter(g -> g.getGroupLevel() == groupLevel)
                .findFirst()
                .orElse(null);
    }

}
