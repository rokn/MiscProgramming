//
// Created by rokner on 1/9/18.
//

#ifndef MATRIX_COLOR_H
#define MATRIX_COLOR_H


#include <cstdint>

class Color {
public:
    uint8_t r, g, b, a;
    Color(uint8_t r, uint8_t g, uint8_t b, uint8_t a=255);
};


#endif //MATRIX_COLOR_H
