//
// Created by rokner on 12/23/17.
//

#ifndef CONFIG_H
#define CONFIG_H

#include "typedefs.h"

struct Config {
    std::string appName;
    uint antialiasLevel;
    uint windowWidth;
    uint windowHeight;
    uint glMajorVersion;
    uint glMinorVersion;
};


#endif //CONFIG_H
