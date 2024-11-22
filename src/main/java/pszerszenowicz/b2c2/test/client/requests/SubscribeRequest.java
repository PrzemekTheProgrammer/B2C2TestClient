package pszerszenowicz.b2c2.test.client.requests;

import java.util.List;

public class SubscribeRequest {

    private final String event = "subscribe";
    private String instrument;
    private String currency;
    private List<Integer> levels;
    private String tag;

    public SubscribeRequest(String instrument, String currency, List<Integer> levels, String tag) {
        this.instrument = instrument;
        this.currency = currency;
        this.levels = levels;
        this.tag = tag;
    }

    public String getEvent() {
        return event;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Integer> getLevels() {
        return levels;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
