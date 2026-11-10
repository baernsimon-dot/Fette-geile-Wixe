package com.example.testclient.settings;

public class BoolSetting extends Setting {
    public boolean value;
    public BoolSetting(String name, boolean def) { super(name); this.value = def; }
}
