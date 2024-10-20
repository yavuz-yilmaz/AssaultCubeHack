package org.example.util;

public class Geom {
    public static final float PI = 3.1415927f;

    public static final int windowWidth = 1920;
    public static final int windowHeight = 1080;

    public static class Vector3 {

        public float x;
        public float y;
        public float z;

        public Vector3() {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        }

        public Vector3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3 add(Vector3 rhs) {
            return new Vector3(this.x + rhs.x, this.y + rhs.y, this.z + rhs.z);
        }

        public Vector3 subtract(Vector3 rhs) {
            return new Vector3(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z);
        }

        public Vector3 multiply(float rhs) {
            return new Vector3(this.x * rhs, this.y * rhs, this.z * rhs);
        }

        public Vector3 divide(float rhs) {
            return new Vector3(this.x / rhs, this.y / rhs, this.z / rhs);
        }

        public Vector3 addEquals(Vector3 rhs) {
            return this.set(this.add(rhs));
        }

        public Vector3 addEquals(float rhs) {
            return this.set(this.add(new Vector3(rhs, rhs, rhs)));
        }

        public Vector3 subtractEquals(Vector3 rhs) {
            return this.set(this.subtract(rhs));
        }

        public Vector3 multiplyEquals(float rhs) {
            return this.set(this.multiply(rhs));
        }

        public Vector3 divideEquals(float rhs) {
            return this.set(this.divide(rhs));
        }

        public float length() {
            return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        }

        public Vector3 normalize() {
            return this.multiply(1 / this.length());
        }

        public float distance(Vector3 rhs) {
            return this.subtract(rhs).length();
        }

        public Vector3 abs() {
            this.x = Math.abs(this.x);
            this.y = Math.abs(this.y);
            this.z = Math.abs(this.z);
            return this;
        }

        public Vector3 normalizeAngle() {
            if (this.x > 360) this.x -= 360;
            if (this.x < 0) this.x += 360;
            if (this.y > 90) this.y -= 90;
            if (this.y < -90) this.y += 90;
            return this;
        }

        public void normalizeAngle(Vector3 angle) {
            if (angle.x > 360) angle.x -= 360;
            if (angle.x < 0) angle.x += 360;
            if (angle.y > 90) angle.y -= 90;
            if (angle.y < -90) angle.y += 90;
        }

        public String toString() {
            return this.x + " " + this.y + " " + this.z;
        }

        private Vector3 set(Vector3 v) {
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
            return this;
        }
    }

    public static class Vec4 {
        public float x;
        public float y;
        public float z;
        public float w;
    }

    public static boolean worldToScreen(Vector3 pos, Vector3 screen, float[] matrix, int windowWidth, int windowHeight) {
        Vec4 clipCoords = new Vec4();

        clipCoords.x = pos.x * matrix[0] + pos.y * matrix[4] + pos.z * matrix[8] + matrix[12];
        clipCoords.y = pos.x * matrix[1] + pos.y * matrix[5] + pos.z * matrix[9] + matrix[13];
        clipCoords.z = pos.x * matrix[2] + pos.y * matrix[6] + pos.z * matrix[10] + matrix[14];
        clipCoords.w = pos.x * matrix[3] + pos.y * matrix[7] + pos.z * matrix[11] + matrix[15];

        if (clipCoords.w < 0.1f) {
            return false;
        }

        Vector3 NDC = new Vector3(clipCoords.x / clipCoords.w, clipCoords.y / clipCoords.w, clipCoords.z / clipCoords.w);

        screen.x = (windowWidth / 2.0f * NDC.x) + (NDC.x + windowWidth / 2.0f);
        screen.y = (windowHeight / 2.0f * NDC.y) + (NDC.y + windowHeight / 2.0f);

        return true;
    }

    public static Vector3 worldToScreen(Vector3 pos, float[] matrix, int windowWidth, int windowHeight) {
        Vec4 clipCoords = new Vec4();

        clipCoords.x = pos.x * matrix[0] + pos.y * matrix[4] + pos.z * matrix[8] + matrix[12];
        clipCoords.y = pos.x * matrix[1] + pos.y * matrix[5] + pos.z * matrix[9] + matrix[13];
        clipCoords.z = pos.x * matrix[2] + pos.y * matrix[6] + pos.z * matrix[10] + matrix[14];
        clipCoords.w = pos.x * matrix[3] + pos.y * matrix[7] + pos.z * matrix[11] + matrix[15];

        if (clipCoords.w < 0.1f) {
            return new Vector3(0.0f, 0.0f, 0.0f);
        }

        return new Vector3(
                clipCoords.x / clipCoords.w,
                clipCoords.y / clipCoords.w,
                clipCoords.z / clipCoords.w
        );
    }

    public static Vector3 worldToScreen(Vector3 pos, float[] viewMatrix) {
        Vector3 screen = new Vector3();

        if(worldToScreen(pos, screen, viewMatrix, windowWidth, windowHeight))
        {
            return screen;
        }
        return new Vector3(0,0,0);
    }

    public static Vector3 calcAngle(Vector3 origin, Vector3 target) {
        Vector3 results = new Vector3(0.0f, 0.0f, 0.0f);
        results.x = radiansToDegrees((float) -Math.atan2(target.x - origin.x, target.y - origin.y));
        if (results.x <= 0.0f) {
            results.x += 360.0f;
        }

        results.y = radiansToDegrees((float) Math.asin((target.z - origin.z) / origin.distance(target)));
        return results;
    }

    public static Vector3 degreesToRadians(Vector3 vec) {
        return new Vector3(
                degreesToRadians(vec.x),
                degreesToRadians(vec.y),
                degreesToRadians(vec.z)
        );
    }

    public static float degreesToRadians(float num) {
        return num / 180.0f * PI;
    }

    public static Vector3 radiansToDegrees(Vector3 vec) {
        return new Vector3(
                vec.x / PI * 180.0f,
                vec.y / PI * 180.0f,
                vec.z / PI * 180.0f
        );
    }

    public static float radiansToDegrees(float num) {
        return num / PI * 180.0f;
    }
}
