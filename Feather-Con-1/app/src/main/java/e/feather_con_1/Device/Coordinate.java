package e.feather_con_1.device;

/**
 * Created by anurag on 10/8/15.
 */
public class Coordinate {
    private byte X;
    private byte Y;
    private boolean writing;
    public Coordinate(byte x, byte y, byte w) {
        X = x;
        Y = y;
        writing = (w==127);
    }

    public Coordinate getCoordinate() {
        return this;
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
