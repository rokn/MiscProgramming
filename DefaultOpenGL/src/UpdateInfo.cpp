//
// Created by rokner on 12/23/17.
//

#include "UpdateInfo.h"

const sf::Time& UpdateInfo::getElapsedTime() const {
    return elapsed;
}

void UpdateInfo::setElapsedTime(const sf::Time &elapsed) {
    UpdateInfo::elapsed = elapsed;
}

float UpdateInfo::elapsedSeconds() const {
    return elapsed.asSeconds();
}
