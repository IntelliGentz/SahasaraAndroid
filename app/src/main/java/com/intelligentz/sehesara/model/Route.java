package com.intelligentz.sehesara.model;

/**
 * Created by Lakshan on 2017-03-25.
 */

public class Route {
    private String routeName;
    private String startCity;
    private String endCity;

    public Route() {
    }

    public Route(String routeName, String startCity, String endCity) {
        this.routeName = routeName;
        this.startCity = startCity;
        this.endCity = endCity;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStartCity() {
        return startCity;
    }

    public void setStartCity(String startCity) {
        this.startCity = startCity;
    }

    public String getEndCity() {
        return endCity;
    }

    public void setEndCity(String endCity) {
        this.endCity = endCity;
    }
}
