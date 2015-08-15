package e.feather_con_1.device;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 10/8/15.
 */
public class DeviceData {
    private static List<Byte> previousData = null;
    private static final byte DELIMITER = -128;
    private byte X;
    private byte Y;
    private boolean writing;

    private DeviceData(byte x, byte y, byte w) {
        X = x;
        Y = y;
        writing = (w == 1);
    }

    public static List<DeviceData> getDeviceData(byte[] data) {
        List<DeviceData> list = new LinkedList<>();
        Byte x = null;
        Byte y = null;
        boolean seen_delimiter = false;
        for (int i = 0; i < data.length; i++) {
            if (i==0 && previousData!=null) {
                for (int k = 0; k < previousData.size(); k++) {
                    if (k == 0) {
                        seen_delimiter = true;
                    } else if (x == null) {
                        x = previousData.get(k);
                    } else if (y == null) {
                        y = previousData.get(k);
                    }
                }
                previousData = null;
            }
            if(previousData==null) {
                previousData = new LinkedList<>();
            }
            if (data[i] == DELIMITER) {
                seen_delimiter = true;
                x = y = null;
                previousData.add(data[i]);
            } else if (x == null && seen_delimiter) {
                x = data[i];
                y = null;
                previousData.add(data[i]);
            } else if(y==null && seen_delimiter) {
                y = data[i];
                previousData.add(data[i]);
            } else if(seen_delimiter) {
                list.add(new DeviceData(x.byteValue(), y.byteValue(), data[i]));
                x = y = null;
                seen_delimiter = false;
                previousData = null;
            }
        }
        return list;
    }

    public byte getX() {
        return X;
    }

    public byte getY() {
        return Y;
    }

    public boolean isWriting() {
        return writing;
    }

    @Override
    public String toString() {
        return "(" + this.X + "," + this.Y + "," + writing + ")";
    }
}
