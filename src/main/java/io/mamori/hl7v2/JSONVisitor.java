package io.mamori.hl7v2;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.Location;
import ca.uhn.hl7v2.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;

/**
 * Convert a HAPI HL7 2.x Message instance to JSON using the existing HAPI visitor infra.
 * <p>
 * This visitor leverages the existing HAPI infrastructure for traversing Message structures
 * to produce a JSON representation with human readable names sourced from the Segments.
 *
 * @Author L.D'Abreo
 */
public class JSONVisitor extends MessageVisitorSupport {

    public static final String DEFAULT_SEP = " ";

    // Generated json
    protected JSONObject payload = new JSONObject();

    // JSON Object stack
    protected Stack<JSONObject> stack = new Stack<JSONObject>();

    // Nice Names from the segment
    protected String[] names;
    // Current field index, name and value
    protected String value;
    protected String name;
    // Separator used to combine field sub-parts
    protected String sep;
    int idx = -1;


    /**
     * Seed JSON with basic message details.
     *
     * @param name
     * @param version
     */
    public JSONVisitor(String name, String version, String sep) {
        payload.put("name", name);
        payload.put("version", version);
        this.sep = sep;
        stack.push(payload);
    }

    /**
     * Seed JSON with basic message details.
     *
     * @param name
     * @param version
     */
    public JSONVisitor(String name, String version) {
      this(name,version,DEFAULT_SEP);
    }

    /**
     * Start message traversal to find all groups, segments and fields.
     *
     * @param message
     * @return
     * @throws HL7Exception
     */
    @Override
    public boolean start(Message message) throws HL7Exception {
        return super.start(message);
    }

    /**
     * Add a new JSON section for each group and push onto the stack.
     *
     * @param group
     * @param location
     * @return
     * @throws HL7Exception
     */
    @Override
    public boolean start(Group group, Location location) throws HL7Exception {
        JSONObject o = new JSONObject();
        JSONObject parent = stack.peek();
        String name = group.getName();
        if (parent.has(name)) {

            if (parent.get(name) instanceof JSONObject) {
                JSONObject child = parent.getJSONObject(name);
                parent.remove(name);
                JSONArray a = new JSONArray();
                parent.put(name,a);
                a.put(child);
                a.put(o);
            }else {
                JSONArray a = parent.getJSONArray(name);
                a.put(o);
            }

        }else {
            parent.put(group.getName(), o);

        }
        stack.push(o);
        return super.start(group, location);
    }

    /**
     * Close the group json and pop the stack.
     *
     * @param group
     * @param location
     * @return
     * @throws HL7Exception
     */
    @Override
    public boolean end(Group group, Location location) throws HL7Exception {
        stack.pop();
        return super.end(group, location);
    }

    /**
     * Add a new JSON section for each segment and push onto the stack.
     *
     * @param segment
     * @param location
     * @return
     * @throws HL7Exception
     */
    @Override
    public boolean start(Segment segment, Location location) throws HL7Exception {
        JSONObject o = new JSONObject();
        JSONObject parent = stack.peek();
        String name = segment.getName();
        if (parent.has(name)) {

            if (parent.get(name) instanceof JSONObject) {
                JSONObject child = parent.getJSONObject(name);
                parent.remove(name);
                JSONArray a = new JSONArray();
                parent.put(name,a);
                a.put(child);
                a.put(o);
            }else {
                JSONArray a = parent.getJSONArray(name);
                a.put(o);
            }

        }else {
            parent.put(segment.getName(), o);

        }
        stack.push(o);

        // get human readable field names
        this.names = segment.getNames();

        // reset field idx
        this.idx = -1;
        return super.start(segment, location);
    }

    /**
     * Close the segment json and pop the stack.
     *
     * @param segment
     * @param location
     * @return
     * @throws HL7Exception
     */
    @Override
    public boolean end(Segment segment, Location location) throws HL7Exception {
        stack.pop();
        return super.end(segment, location);
    }


    /**
     * Get the nice field name from the parent segment and reset the field value.
     *
     * @param field
     * @param location
     * @return
     * @throws HL7Exception
     */
    public boolean start(Field field, Location location) throws HL7Exception {
        ++idx;
        // Get human-readable field name
        name = names[idx];
        // reset value
        value = "";
        return true;
    }

    /**
     * For each field set a JSON attribute.
     * Combine sub-fields into a single attribute using the separator.
     *
     * @param type
     * @param location
     * @return
     * @throws HL7Exception
     */
    public boolean visit(Primitive type, Location location) throws HL7Exception {

        JSONObject o = stack.peek();
        // Null?
        String thisvalue = type.getValue();
        if (thisvalue == null) {
            thisvalue = "";
        }

        // Append sep and current value to existing field value
        value = value + sep + thisvalue.trim();
        o.put(name, value.trim());

        return true;
    }

    /**
     * Return generated JSON.
     *
     * @return
     */
    public JSONObject getPayload() {
        return this.payload;
    }
}
