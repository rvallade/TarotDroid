package fr.commun.game;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonEntity {
    
    public JSONObject toJSON() throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        StdSerializerProvider sp = new StdSerializerProvider();
        sp.setNullValueSerializer(new NullSerializer());
        // we don't want null values in the result
        mapper.setSerializerProvider(sp);
        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        return new JSONObject(mapper.writeValueAsString(this));
    }
}
