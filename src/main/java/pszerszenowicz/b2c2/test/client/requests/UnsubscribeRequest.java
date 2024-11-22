package pszerszenowicz.b2c2.test.client.requests;

public class UnsubscribeRequest {

    private final String event = "unsubscribe";
    private String instrument;
    private String tag;

    public UnsubscribeRequest(String instrument, String tag) {
        this.instrument = instrument;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
