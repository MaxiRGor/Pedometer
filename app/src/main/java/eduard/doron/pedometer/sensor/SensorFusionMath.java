package eduard.doron.pedometer.sensor;

class SensorFusionMath {

    private SensorFusionMath() {
    }

    static float sum(float[] array) {
        float returnValue = 0;
        for (float v : array) {
            returnValue += v;
        }
        return returnValue;
    }

/*    public static float[] cross(float[] arrayA, float[] arrayB) {
        float[] retArray = new float[3];
        retArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1];
        retArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2];
        retArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0];
        return retArray;
    }*/

    static float norm(float[] array) {
        float returnValue = 0;
        for (float v : array) {
            returnValue += v * v;
        }
        return (float) Math.sqrt(returnValue);
    }

    // Note: only works with 3D vectors.
    static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

/*    public static float[] normalize(float[] a) {
        float[] returnValue = new float[a.length];
        float norm = norm(a);
        for (int i = 0; i < a.length; i++) {
            returnValue[i] = a[i] / norm;
        }
        return returnValue;
    }*/
}
