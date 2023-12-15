package edu.blaylock.client.ui;

public record Rect(int x, int y, int width, int height) {

    public Rect offset(int x, int y) {
        return new Rect(this.x + x, this.y + y, width, height);
    }

    public Rect resize(int width, int height) {
        return new Rect(x, y, this.width + width, this.height + height);
    }

    public Rect translate(int x, int y) {
        return new Rect(x, y, width, height);
    }

    public Rect size(int width, int height) {
        return new Rect(x, y, width, height);
    }
}
