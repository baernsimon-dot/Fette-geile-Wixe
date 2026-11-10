package com.example.testclient.settings;

public class NumSetting extends Setting {
    public double value;
    public final double min, max, step;
    public NumSetting(String name, double def, double min, double max, double step) {
        super(name); this.value = def; this.min = min; this.max = max; this.step = step;
    }
    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        if (step > 0) v = Math.round(v / step) * step;
        this.value = v;
    }
    public float f() { return (float) value; }
}
