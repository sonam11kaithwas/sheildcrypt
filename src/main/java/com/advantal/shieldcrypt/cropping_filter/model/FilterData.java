package com.advantal.shieldcrypt.cropping_filter.model;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterData {
    private String filterName;
    private PhotoFilter filterType;

    public FilterData(String filterName, PhotoFilter filterType) {
        this.filterName = filterName;
        this.filterType = filterType;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public PhotoFilter getFilterType() {
        return filterType;
    }

    public void setFilterType(PhotoFilter filterType) {
        this.filterType = filterType;
    }
}
