package nc.uap.plugin.studio.ui.preference.prop;

public class DomainInfo {
    private ClusterInfo cluster;

    private SingleServerInfo server;

    public ClusterInfo getCluster() {
        return this.cluster;
    }

    public void setCluster(ClusterInfo cluster) {
        this.cluster = cluster;
    }

    public SingleServerInfo getServer() {
        return this.server;
    }

    public void setServer(SingleServerInfo server) {
        this.server = server;
    }
}
