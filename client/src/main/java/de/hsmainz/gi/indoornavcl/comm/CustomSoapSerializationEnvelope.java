package de.hsmainz.gi.indoornavcl.comm;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;


/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class CustomSoapSerializationEnvelope extends SoapSerializationEnvelope {

    public boolean useHeader = false;

    public CustomSoapSerializationEnvelope(int version) {
        super(version);
    }

    /**
     * Writes the complete envelope including header and body elements to the
     * given XML writer.
     */
    @Override
    public void write(XmlSerializer writer) throws IOException {
        writer.setPrefix("soap", env);
        writer.startTag(env, "Envelope");
        if (useHeader) {
            writer.startTag(env, "Header");
            writeHeader(writer);
            writer.endTag(env, "Header");
        }
        writer.startTag(env, "Body");
        writeBody(writer);
        writer.endTag(env, "Body");
        writer.endTag(env, "Envelope");
    }
}
