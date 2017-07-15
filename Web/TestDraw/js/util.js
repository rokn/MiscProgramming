/**
 * Created by rokner on 5/14/17.
 */

function toRadians (angle) {
  return angle * (Math.PI / 180);
}

function lerp (from, to, val) {
  return from + (val * (to - from))
}
