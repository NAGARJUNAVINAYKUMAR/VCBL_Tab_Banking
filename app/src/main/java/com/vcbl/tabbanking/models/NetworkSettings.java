package com.vcbl.tabbanking.models;

/**
 * Created by Balajee on 19-12-2017.
 */

public class NetworkSettings {

    private String id, Url, Port, Path;

    public NetworkSettings() {
    }

    public String getTableName() {
        return "NetworkSettings";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return this.Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getPort() {
        return this.Port;
    }

    public void setPort(String Port) {
        this.Port = Port;
    }

    public String getPath() {
        return this.Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

}
