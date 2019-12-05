package com.excel.eom.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class GroupRegionList {

    private List<GroupRegion> groupRegions = new ArrayList();

    @Data
    @AllArgsConstructor
    public class GroupRegion {
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

    public boolean isContainGroup(int groupLevel) {
        return findGroupRegion(groupLevel) != null ? true : false;
    }

    public List<Integer> findGroupRegionEndPoint(int groupLevel) {
        GroupRegion groupRegion = findGroupRegion(groupLevel);
        return groupRegion != null ? groupRegion.getEndPointList() : new ArrayList<>();
    }

    public GroupRegion findGroupRegion(int groupLevel) {
        return groupRegions.stream()
                .filter(g -> g.getGroupLevel() == groupLevel)
                .findFirst()
                .orElse(null);
    }

}
