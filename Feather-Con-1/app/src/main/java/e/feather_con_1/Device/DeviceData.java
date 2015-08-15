package e.feather_con_1.device;

/**
 * Created by anurag on 10/8/15.
 */
public class DeviceData {
    private byte delimiter;
    private byte X;
    private byte Y;
    private boolean writing;
    public DeviceData(byte d, byte x, byte y, byte w) {
        delimiter = d;
        X = x;
        Y = y;
        writing = (w==127);
    }

    public DeviceData getCoordinate() {
        return this;
    }

    public byte getDelimiter() { return delimiter; }

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
