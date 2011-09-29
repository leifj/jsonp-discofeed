
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stefan
 */
public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    static public byte[] getUrlBytes(URL url) {
        try {
            InputStream in = url.openStream();
            BufferedInputStream bufIn = new BufferedInputStream(in);
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[100];
                for (;;) {
                    int len = bufIn.read(b);
                    if (len == -1) {
                        break;
                    } else {
                        bos.write(b, 0, len);
                    }
                }
                byte[] data = bos.toByteArray();
                bos.close();
                return data;
            } catch (Exception ex) {
                LOG.info(" I/O Error - " + ex.getMessage());
            }
        } catch (MalformedURLException mue) {
            LOG.info("Invalid URL: " + url.toString());
        } catch (IOException ioe) {
            LOG.info("I/O Error - " + ioe.getMessage());
        }
        return null;
    }

    public static byte[] readBinaryFile(File file) {
        List inp = new LinkedList<Byte>();
        try {
            FileInputStream fi = new FileInputStream(file);
            while (fi.available() > 0) {
                inp.add(fi.read());
            }
        } catch (IOException ex) {
            LOG.info(ex.getMessage());
            return null;
        }
        byte[] b = new byte[inp.size()];
        int i = 0;
        for (Object o : inp) {
            int val = (Integer) o;
            b[i++] = (byte) val;
        }
        return b;
    }
}