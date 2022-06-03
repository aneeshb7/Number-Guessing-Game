package persistence;
// interface name taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

import org.json.JSONObject;

public interface Writable {
    // method taken from https://journals.sagepub.com/doi/pdf/10.1177/1940161219900126
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
