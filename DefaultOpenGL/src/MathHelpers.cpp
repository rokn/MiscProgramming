//
// Created by rokner on 12/25/17.
//

#include "MathHelpers.h"

float MathHelpers::lerp(float from, float to, float amount) {
    if(amount >= 1.f) {
        return to;
    } else if (amount <= 0.f) {
        return from;
    }
    float diff = to - from;
    return from + diff * amount;
}
