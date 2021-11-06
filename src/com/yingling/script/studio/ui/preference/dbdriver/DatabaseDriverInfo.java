package com.yingling.script.studio.ui.preference.dbdriver;

public class DatabaseDriverInfo {
    private String databaseType;

    private DriverInfo[] driver;

    public DriverInfo[] getDatabase() {
        return this.driver;
    }

    public void setDatabase(DriverInfo[] driver) {
        this.driver = driver;
    }

    public String getDatabaseType() {
        return this.databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String toString() {
        return getDatabaseType();
    }
}
