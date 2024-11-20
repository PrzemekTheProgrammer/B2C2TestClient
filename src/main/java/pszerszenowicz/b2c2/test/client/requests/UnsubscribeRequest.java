package pszerszenowicz.b2c2.test.client.requests;

public class UnsubscribeRequest {

    private final String event = "unsubscribe";
    private String instrument;
    private String tag;

    public UnsubscribeRequest(String instrument, String tag) {
        this.instrument = instrument;
        this.tag = tag;
    }
}
